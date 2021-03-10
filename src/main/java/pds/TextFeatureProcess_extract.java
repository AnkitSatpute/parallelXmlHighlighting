package pds;

import pds.entity.feature.ElasticCopy;
import pds.entity.feature.text.TextFeature;
import pds.entity.feature.text.TextFeatureDB;
import pds.entity.feature.text.TextFeatureRepository;
import pds.entity.feature.text.TextFeatureRepositoryDB;


import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.ladenthin.javacommons.FileHelper;

import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.Collections;
import java.util.Properties;
import org.apache.commons.io.*;

/**
 * Transforms and extracts the TEI Body into plaintext.
 *
 * @author Marco Beck
 */
public class TextFeatureProcess_extract { 
	
	public static void main(String[] args) throws IOException
     {
    	
        TextFeatureDB textFeature = new TextFeatureDB(1, Collections.emptyList());
           
        //Input-File as xml
     //   String filePath = "src/main/resources/input_document.xml";
        String filePath = args[0];
        StringBuilder stringBuilder = new StringBuilder();
        try (Stream stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) 
        {
         stream.forEach(s -> stringBuilder.append(s).append("\n"));
        }
        catch (IOException e) 
        {
         e.printStackTrace();
        }
        
      
        // transform it    plain and tags
        transformToPlainText(stringBuilder.toString(), textFeature);
        transformTotags(stringBuilder.toString(), textFeature);
     
        
        //Output-file as txt
        String output_file ="src/main/resources/output_plain.txt";
        Files.write(Paths.get(output_file), textFeature.getText().getBytes("UTF8"));

        
        //Output-mapping-file as txt
        String output_mapping_file ="src/main/resources/output_mapping.txt";
        Files.write(Paths.get(output_mapping_file), textFeature.getMappingString().getBytes("UTF8"));
        
        //Output-xml-tags as txt
       String output_xml_tags_txt ="src/main/resources/output_xml_tags.txt";
       Files.write(Paths.get(output_xml_tags_txt), textFeature.gettags().getBytes("UTF8"));
             
       
        System.out.println(stringBuilder.toString());
       System.out.println("------------------------");
       System.out.println(textFeature.getText());       
       System.out.println("------------------------");
       System.out.println(textFeature.getMapping());
       System.out.println("------------------------");   
       System.out.println(textFeature.gettags());
 
    }

    /**
     * Transform an XML String into Plain Text, all XML Elements will be ignored/excluded: <br/>
     * The Plain Text is stored into the TextFeature.
     * <br/>
     * In addition, a mapping is stored in the TextFeature that allows to trace the
     * char-positions of plain text to XML.
     *
     * @param xmlContent  XML String
     * @param textFeature Result entity which stores the plain text and the mapping.
     * @return The textFeature paramater will be returned.
     */
    public static TextFeatureDB transformToPlainText(String xmlContent, TextFeatureDB textFeature) {
        TreeMap<Integer, Integer> map = new TreeMap<>();

        StringWriter plaintextOut = new StringWriter(xmlContent.length());

        int posTei = 0;
        int posTxt = 0;

        StringTokenizer tokenizer = new StringTokenizer(xmlContent, "<>", true);

        boolean insideTag = false;
        boolean skipTagContent = false;

        while (tokenizer.hasMoreTokens()) {
            String tok = tokenizer.nextToken();
            posTei += tok.length();

            switch (tok) {
                case "<":
                    insideTag = true;
                    break;
                case ">":
                    insideTag = false;
                    if (!skipTagContent) map.put(posTxt, posTei);
                    break;
                default:
                    if (insideTag) {
                        if (tok.startsWith("formula") || tok.startsWith("ref")) {
                            skipTagContent = true;
                        } else if (tok.startsWith("/formula") || tok.startsWith("/ref")) {
                            skipTagContent = false;
                        }
                    } else if (!skipTagContent) {
                        posTxt += tok.length();
                        plaintextOut.write(tok + " ");
                    }
            }
            
            
        }

        // save it all :)
        textFeature.setText(plaintextOut.toString());
        textFeature.setMapping(map);
        
     //   textFeature.getMappingString()
        
        return textFeature;
	 }
    
    public static TextFeatureDB transformTotags(String xmlContent, TextFeatureDB textFeature) {
        TreeMap<Integer, Integer> map2 = new TreeMap<>();

        StringWriter plaintextOut_tags = new StringWriter(xmlContent.length());
        StringWriter plaintextOuts_tags = new StringWriter(xmlContent.length());

        int posTei = 0;
        int posTxt = 0;

        StringTokenizer tokenizer = new StringTokenizer(xmlContent, "><", true);

        boolean insideTag = false;
        boolean skipTagContent = false;

        while (tokenizer.hasMoreTokens()) {
            String tok = tokenizer.nextToken();
            posTei += tok.length();

            switch (tok) {
                case "<":
                    insideTag = true;
                    break;
                case ">":
                    insideTag = false;
                    if (!skipTagContent) map2.put(posTxt, posTei);
                    plaintextOuts_tags.write(posTxt+"-" + posTei + "+");
                    break;
                default:
                    if (!insideTag) {
                    	plaintextOuts_tags.write("sss");
                        if (tok.startsWith("formula") || tok.startsWith("ref")) {
                            skipTagContent = true;
                        } else if (tok.startsWith("/formula") || tok.startsWith("/ref")) {
                            skipTagContent = false;
                        }
                    } else if (!skipTagContent) {
                        posTxt += tok.length();
                        plaintextOut_tags.write("<"+tok+">");
                    }
            }
            
            
        }

        // save it all :)
        textFeature.setTags(plaintextOut_tags.toString());
        textFeature.setposTags(plaintextOuts_tags.toString());
        textFeature.settagging(map2);

        
     //   textFeature.getMappingString()
        
        return textFeature;
	 }
    
    }
