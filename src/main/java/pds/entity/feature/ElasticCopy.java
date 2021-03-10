package pds.entity.feature;

import lombok.extern.slf4j.Slf4j;


import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates copies of database objects for Elasticsearch.
 * <p>
 * This class helps as an interim solution: the migration of all data
 * to a database, while the algorithms continue to use Elasticsearch.
 *
 * @author Vincent Stange
 */
@Slf4j
public class ElasticCopy {

    /**
     * Creates a new elasticsearch document out of the database entity.
     *
     * @param db database entity
     * @return elasticsearch document
     */
    public static CitationFeature copyCitationFeature(CitationFeatureDB db, List<String> scopes) {
        CitationFeature es = new CitationFeature(db.getSrcDocumentId(), scopes);
        es.setCitations(db.getCitations().stream().map(c -> new Citation(
                (c.getUniqueRefId() != null ? c.getUniqueRefId().toString() : null),
                c.getTeiId(),
                c.getStartPosition(),
                c.getEndPosition(),
                c.getOrderingNum()
        )).collect(Collectors.toList()));
        es.setReferenceIds(db.getReferenceIds().stream().map(l -> Long.toString(l)).collect(Collectors.toList()));
        return es;
    }

    /**
     * Creates a new elasticsearch document out of the database entity.
     *
     * @param db database entity
     * @return elasticsearch document
     */
    public static FormulaFeature copyFormulaFeature(FormulaFeatureDB db, List<String> scopes) {
        FormulaFeature es = new FormulaFeature(db.getSrcDocumentId(), scopes);
        es.setFormulas(db.getFormulas().stream().map(f -> new Formula(
                f.getElementId(),
                f.getOrdinal(),
                f.getXPath(),
                f.getIdentifierCount(),
                f.getMathml(),
                f.getStartPosition(),
                f.getEndPosition()
        )).collect(Collectors.toList()));
        return es;
    }

    /**
     * Creates a new elasticsearch document out of the database entity.
     *
     * @param db database entity
     * @return elasticsearch document
     */
    public static HistogramFeature copyHistogramFeature(HistogramFeatureDB db, List<String> scopes) {
        HistogramFeature hf = new HistogramFeature(db.getSrcDocumentId(), scopes);
        hf.setHistogram(db.getHistogram().stream().map(h -> new Histogram(
                h.getIdentifier(),
                h.getValue()
        )).collect(Collectors.toList()));
        return hf;
    }

    /**
     * Creates a new elasticsearch document out of the database entity.
     *
     * @param db database entity
     * @return elasticsearch document
     */
    public static SherlockFeature copySherlockFeature(SherlockFeatureDB db, List<String> scopes) {
        SherlockFeature es = new SherlockFeature(db.getSrcDocumentId(), scopes);
        es.setFingerprints(db.getFingerprints());
        return es;
    }

    /**
     * Creates a new elasticsearch document out of the database entity.
     *
     * @param db database entity
     * @return elasticsearch document
     */
    public static TextFeature copyTextFeature(TextFeatureDB db, List<String> scopes) {
        TextFeature es = new TextFeature(db.getSrcDocumentId(), scopes);
        es.setText(db.getText());
        es.setMapping(db.getMapping());
        return es;
    }

    /**
     * Creates a new elasticsearch document out of the database entity.
     *
     * @param db database entity
     * @return elasticsearch document
     */
    public static ReferenceFeature copyReferenceFeature(UniqueReference db) {
        try {
            ReferenceFeature es = new ReferenceFeature();
            es.setReferenceId(db.getReferenceId());
            es.setTitle(UniqueReferenceService.asciiFolding(db.getTitle()));
            es.setSurnames(UniqueReferenceService.getSurnameList(db));
            es.setPubYear(UniqueReferenceService.parsePubYear(db.getPubDate()));
            return es;
        } catch (Exception e) {
            log.warn("Reference not parsable: {}", e.getMessage());
            return null;
        }
    }
}
