package org.sciplore.pds.entity.feature.text;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sciplore.pds.entity.feature.BasicFeature;
import org.sciplore.pds.util.ConventionUtil;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * TextFeature Document for Elasticsearch. Contains the documents plaintext and a mapping
 * allowing to map plaintext positions to the original TEI document.
 * 
 * @author Vincent Stange
 */
//@Document(indexName = "texts", type = "text", replicas = 0, shards = 2)
@NoArgsConstructor
@Getter
@Setter
public class TextFeature {

  //  @Field(type = FieldType.String)
    private String text;

    @JsonIgnore
    private TreeMap<Integer, Integer> mapping;
    private TreeMap<Integer, Integer> tagging;

 //   @Field(type = FieldType.String, index = FieldIndex.no)
    private String mappingString;
    private String taggingString;

    /**
     * Creates an instance of TextFeature.
     * 
     * @param srcDocumentId Id of the parent document.
     * @param scopes List of scopes.
     */
    public TextFeature(Integer srcDocumentId, List<String> scopes) {
  //      super(srcDocumentId, scopes);
    }

    public TreeMap<Integer, Integer> getMapping() {
        if (mapping == null) {
            mapping = (ConventionUtil.decodeMapping(getMappingString()));
        }
        return mapping;
    }
    
    public TreeMap<Integer, Integer> gettagging() {
        if (tagging == null) {
            tagging = (ConventionUtil.decodeMapping(gettaggingString()));
        }
        return tagging;
    }

    public void setMapping(TreeMap<Integer, Integer> mapping) {
        this.mapping = mapping;
        setMappingString(ConventionUtil.encodeMapping(mapping));
    }
        
    public void settagging(TreeMap<Integer, Integer> tagging) {
        this.tagging = tagging;
        settaggingString(ConventionUtil.encodeMapping(tagging));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextFeature)) return false;

        TextFeature that = (TextFeature) o;

        return getSrcDocumentId().equals(that.getSrcDocumentId());
    }

    @Override
    public int hashCode() {
        return getSrcDocumentId().hashCode();
    }
}
