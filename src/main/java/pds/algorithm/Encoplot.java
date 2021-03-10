package pds.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.sciplore.pds.algorithm.AlgorithmId;
import org.sciplore.pds.algorithm.Detailed;
import org.sciplore.pds.algorithm.DetailedAlgorithmA;
import org.sciplore.pds.algorithm.type.ResultFormat;
import org.sciplore.pds.algorithm.type.SimilarityFeature;
import org.sciplore.pds.config.AlgorithmConfig;
import org.sciplore.pds.config.ApplicationProperties;
import org.sciplore.pds.detection.DetectionException;
import org.sciplore.pds.detection.pattern.TextPattern;
import org.sciplore.pds.detection.pattern.TextPatternRepository;
import org.sciplore.pds.entity.feature.text.TextFeatureDB;
import org.sciplore.pds.entity.feature.text.TextFeatureRepositoryDB;
import org.sciplore.pds.entity.result.ResultData;
import org.sciplore.pds.util.CommandExecutor;
import org.sciplore.pds.util.ConventionUtil;
import org.sciplore.pds.util.TemporaryFileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Detailed Similarity Algorithm called Encoplot,
 * developed by Grozea, Gehl & Popescu.
 * <br />
 * Encoplot is an external C program and wrapped
 * by a python programm for result interpretations.
 * <br />
 * Description: <br/>
 * Encoplot is a string matching algorithm that finds
 * text patterns and determines their position.
 *
 * @author Vincent Stange
 */
@Component("Encoplot")
@Detailed
@Scope("prototype")
@Slf4j
public class Encoplot extends DetailedAlgorithmA<TextFeatureDB> {

    private final TextFeatureRepositoryDB textFeatureRepository;
    private final TextPatternRepository textPatternRepository;
    private final ApplicationProperties properties;
    private final AlgorithmConfig algorithmConfig;

    private double threshold;

    // Encoplot Wrapper in python
    private Path encoWrapper;

    // Encoplot as a program in c
    private Path enco;

    @Autowired
    public Encoplot(TextFeatureRepositoryDB textFeatureRepository, TextPatternRepository textPatternRepository, ApplicationProperties properties, AlgorithmConfig algorithmConfig) {
        super(AlgorithmId.Encoplot,
                SimilarityFeature.text,
                ResultFormat.text,
                60
        );
        setDescription("Encoplot is a string matching algorithm that finds text patterns and determines their position.");
        this.textFeatureRepository = textFeatureRepository;
        this.textPatternRepository = textPatternRepository;
        this.properties = properties;
        this.algorithmConfig = algorithmConfig;
    }

    @PostConstruct
    public void initComponent() {
        encoWrapper = Paths.get(properties.getExternalLibs(), "encoplot_similarity.py");
        enco = Paths.get(properties.getExternalLibs(), "encoplot");
        threshold = algorithmConfig.getEncoplot().getThreshold();
    }

    @Override
    protected List<TextFeatureDB> loadCandidatesByFeature(List<Integer> selectedDocumentIds) {
        // this algorithm relies on pre-fetched data via PreFetchTexts
        return getPreFetchedData().getTextFeatures();
    }

    @Override
    public List<Integer> executeAlgorithm(List<TextFeatureDB> selectedCandidates, ResultData resultData) throws DetectionException {
        List<Integer> computedDocIds = Collections.synchronizedList(new ArrayList<>(selectedCandidates.size()));

        // 1. Get text of the source document
        TextFeatureDB srcText = textFeatureRepository.findOne(sourceDocument.getDocumentId());
        int[] srcByteToCharPos = ConventionUtil.utf8ByteIndexToChar(srcText.getText());

        try (TemporaryFileManager tmp = new TemporaryFileManager("encoplot_alg")) {
            // 2.1. Store text from the source document into a temp file
            Path srcTextFile = createTextFile(srcText.getText(), tmp);

            // 2.2. Store text of every candidate into a separate temp file
            HashMap<TextFeatureDB, Path> candidateMap = new HashMap<>(selectedCandidates.size());
            for (TextFeatureDB candidate : selectedCandidates) {
                candidateMap.put(candidate, createTextFile(candidate.getText(), tmp));
            }

            // 3. Compare source file with every other file / Encoplot
            ExecutorService executor = Executors.newFixedThreadPool(Math.min(selectedCandidates.size(), algorithmConfig.getTextAlgorithmThreads()));
            for (Map.Entry<TextFeatureDB, Path> candidate : candidateMap.entrySet()) {
                executor.execute(() -> {
                    try {
                        Integer documentId = executeComparison(srcText, srcByteToCharPos, srcTextFile, candidate);
                        if (documentId != null) {
                            computedDocIds.add(documentId);
                        }
                    } catch (Exception e) {
                        log.error("encoplot algorithm (src: {}) (sel: {}) ",
                                srcText.getSrcDocumentId(),
                                candidate.getKey().getSrcDocumentId(),
                                e);
                    }
                });
            }
            try {
                executor.shutdown();
                executor.awaitTermination(3, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                log.warn("enco execution interrupted", e);
            }
        } catch (Exception e) {
            throw new DetectionException("encoplot algorithm", e);
        }

        return computedDocIds;
    }

    @SuppressWarnings("unchecked")
    private Integer executeComparison(TextFeatureDB srcText, int[] srcByteToCharPos, Path srcTextFile, Map.Entry<TextFeatureDB, Path> candidate) throws Exception {
        TextPattern textPattern = new TextPattern(sourceDocument.getDocumentId(), candidate.getKey().getSrcDocumentId(), getId());

        // 3.1 Execute encoplot
        String output = execEncoplotComparison(encoWrapper, enco, srcTextFile, candidate.getValue());

        // 3.2 interpret the output of our encoplot
        Map<String, Object> jsonOutputMap = JsonParserFactory.getJsonParser().parseMap(output);
        ArrayList<Map<String, Integer>> patternResult = (ArrayList<Map<String, Integer>>) jsonOutputMap.get("pattern");
        Integer pattern_score = patternResult.get(0).get("pattern_score");

        // percentage coverage of on the shortest document text
        double scoreValue = pattern_score / 100.;
        if (scoreValue > 0) {
            ArrayList<Map<String, Integer>> textpattern_doc1 = (ArrayList<Map<String, Integer>>) jsonOutputMap.get("textpattern_doc1");
            ArrayList<Map<String, Integer>> textpattern_doc2 = (ArrayList<Map<String, Integer>>) jsonOutputMap.get("textpattern_doc2");

            int[] selByteToCharPos = ConventionUtil.utf8ByteIndexToChar(candidate.getKey().getText());

            for (int i = 0; i < textpattern_doc1.size(); i++) {
                // byte positions based on the plain text
                TextPosition bytePosition = new TextPosition();
                Map<String, Integer> match_doc1 = textpattern_doc1.get(i);
                bytePosition.startPosA = match_doc1.get("start_character");
                bytePosition.endPosA = match_doc1.get("end_character");

                Map<String, Integer> match_doc2 = textpattern_doc2.get(i);
                bytePosition.startPosB = match_doc2.get("start_character");
                bytePosition.endPosB = match_doc2.get("end_character");

                TextPosition textPosition = transferByteToTextPosition(bytePosition, srcByteToCharPos, selByteToCharPos);

                textPosition = excludeWhitespaceAtEnd(textPosition, srcText.getText());

                TextPosition xmlPosition = transferTextToXmlPosition(textPosition, srcText.getMapping(), candidate.getKey().getMapping());

                // save it as a match, value is the length of the match inside the plain text
                int length = textPosition.endPosA - textPosition.startPosA;
                textPattern.addMatch(xmlPosition.startPosA, xmlPosition.endPosA, xmlPosition.startPosB, xmlPosition.endPosB, length);
            }
        }

        // Threshold - score too low, sort it out
        if (scoreValue < threshold) {
            return null;
        }
        textPattern.setValue(scoreValue);

        // 4. Save the results and add as a candidate
        return textPatternRepository.save(textPattern).getSelectedDoc();
    }

    /**
     * @param textPosition positions in plain text document
     * @param srcText      plain text from source document
     * @return altered textPosition parameter
     */
    static TextPosition excludeWhitespaceAtEnd(TextPosition textPosition, String srcText) {
        if (srcText.charAt(textPosition.endPosA - 1) == ' ') {
            textPosition.endPosA = textPosition.endPosA - 1;
            textPosition.endPosB = textPosition.endPosB - 1;
        }
        return textPosition;
    }

    /**
     * Convert byte to character position - prerequisite: UTF-8 encoding.
     * Byte Array were created via ConventionUtil.utf8ByteIndexToChar
     *
     * @param bytePosition     positions on byte stream of plain text document
     * @param srcByteToCharPos array to map byte to text position for source document
     * @param selByteToCharPos array to map byte to text position for candidate document
     * @return new position object referring to plain text positions
     */
    static TextPosition transferByteToTextPosition(TextPosition bytePosition, int[] srcByteToCharPos, int[] selByteToCharPos) {
        TextPosition textPosiiton = new TextPosition();
        try {
            textPosiiton.startPosA = srcByteToCharPos[bytePosition.startPosA];
            textPosiiton.endPosA = srcByteToCharPos[bytePosition.endPosA] + 1;
            textPosiiton.startPosB = selByteToCharPos[bytePosition.startPosB];
            textPosiiton.endPosB = selByteToCharPos[bytePosition.endPosB] + 1;
            return textPosiiton;
        } catch (ArrayIndexOutOfBoundsException e) {
            // if this error occurs, something may be wrong with our text to xml positioning
            log.error("ArrayIndexOutOfBoundsException: " +
                    " src start: " + bytePosition.startPosA + "," + textPosiiton.startPosA + ";" +
                    " src end: " + bytePosition.endPosA + "," + textPosiiton.endPosA + ";" +
                    " sel start: " + bytePosition.startPosB + "," + textPosiiton.startPosB + ";" +
                    " sel end: " + bytePosition.endPosB + "," + textPosiiton.endPosB + ";" +
                    " SourceByte: " + srcByteToCharPos.length + ";" +
                    " SelByte: " + selByteToCharPos.length + ";", e);
            throw e;
        }
    }

    /**
     * Convert text to xml position.
     *
     * @param textPosition     positions in plain text document
     * @param srcMapping       floor mapping of text positions for source document
     * @param candidateMapping floor mapping of text positions for candidate document
     * @return positions in xml document
     */
    private static TextPosition transferTextToXmlPosition(TextPosition textPosition, TreeMap<Integer, Integer> srcMapping, TreeMap<Integer, Integer> candidateMapping) {
        TextPosition xmlPosition = new TextPosition();
        xmlPosition.startPosA = TextFeatureDB.getStartPositionFromTxtToXml(textPosition.startPosA, srcMapping);
        xmlPosition.endPosA = TextFeatureDB.getEndPositionFromTxtToXml(textPosition.endPosA, srcMapping);
        xmlPosition.startPosB = TextFeatureDB.getStartPositionFromTxtToXml(textPosition.startPosB, candidateMapping);
        xmlPosition.endPosB = TextFeatureDB.getEndPositionFromTxtToXml(textPosition.endPosB, candidateMapping);
        return xmlPosition;
    }

    /**
     * Executes the Encoplot.
     *
     * @param encoWrapper   Path to the Encoplot Wrapper Program
     * @param enco          Path to the original Encoplot executables
     * @param srcTextFile   Path to the source text file
     * @param candidateFile Path to the candidate text file
     * @return Currently Unknown
     * @throws Exception Execution error of encoplot or its result transformation.
     */
    static String execEncoplotComparison(Path encoWrapper, Path enco, Path srcTextFile, Path candidateFile) throws Exception {
        // execute Encoplot as a python program
        CommandExecutor commandExecutor = new CommandExecutor("python",
                encoWrapper.toString(),
                enco.toString(),
                srcTextFile.toAbsolutePath().toString(),
                candidateFile.toAbsolutePath().toString());
        return commandExecutor.exec(200L);
    }

    /**
     * Creates a temporary file with a plain text as its content. Text is saved in lower case.
     *
     * @param text Plain text as a String
     * @param tmp  TemporaryFileManager is mandatory, as it will manager the lifespan of the created temp file.
     * @return Path to the temp file
     * @throws java.io.IOException Writing error
     */
    private static Path createTextFile(String text, TemporaryFileManager tmp) throws IOException {
        Path tempFile = tmp.createTempFile();
        Files.write(tempFile, text.toLowerCase().getBytes(StandardCharsets.UTF_8));
        return tempFile;
    }

    public static class TextPosition {
        int startPosA = 0;
        int endPosA = 0;
        int startPosB = 0;
        int endPosB = 0;
    }

}
