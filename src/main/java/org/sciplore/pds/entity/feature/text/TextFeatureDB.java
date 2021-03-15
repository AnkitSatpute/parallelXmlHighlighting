package org.sciplore.pds.entity.feature.text;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sciplore.pds.util.ConventionUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * TextFeature Document for Elasticsearch. Contains the documents plaintext and a mapping
 * allowing to map plaintext positions to the original TEI document.
 *
 * @author Vincent Stange
 */
@Entity
@Table(name = "text_feature")
@NoArgsConstructor
@Getter
@Setter
public class TextFeatureDB {

    @Column(name = "text")
    private String text;
    
    @Column(name = "tags")
    private String tags;
    
    @Column(name = "postags")
    private String postags;

    @JsonIgnore
    @Transient
    private TreeMap<Integer, Integer> mapping;
    private TreeMap<Integer, Integer> tagging;

    @Column(name = "mapping_string")
    private String mappingString;
    private String mappingString_pos;
    
    @Column(name = "tagging_string")
    private String taggingString;

    /**
     * Creates an instance of TextFeature.
     *
     * @param srcDocumentId Id of the parent document.
     * @param scopes        List of scopes.
     */
    public TextFeatureDB(Integer srcDocumentId, List<String> scopes) {
      //  super(srcDocumentId, scopes);
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

    private void setMappingString(String encodeMapping) {
		// TODO Auto-generated method stub
        
    		this.mappingString = encodeMapping;
    		
	}
    
    private void setMappingString_pos(String encodeMapping_pos) {
		// TODO Auto-generated method stub
        
    		this.mappingString_pos = encodeMapping_pos;
    		
	}
    
    public void settagging(TreeMap<Integer, Integer> tagging) {
        this.tagging = tagging;
        settaggingString(ConventionUtil.encodeMapping(tagging));
    }

    private void settaggingString(String encodeMapping1) {
		// TODO Auto-generated method stub
        
    		this.taggingString = encodeMapping1;
    		
	}

	/**
     * Transforms a starting index of a plain text section into the starting index of our TEI XML body
     *
     * @param startPos Start position of a plain text segment
     * @param mapping  Text to XML index position mapping
     * @return Start position of a xml text segment
     */
    public static int getStartPositionFromTxtToXml(int startPos, TreeMap<Integer, Integer> mapping) {
        Map.Entry<Integer, Integer> entry = mapping.floorEntry(startPos);
        int offset = startPos - entry.getKey();
        return entry.getValue() + offset;
    }

    /**
     * Transforms a end index of a plain text section into the end index of our TEI XML body
     * <br />
     * Hint: Why 2 methods for start and end? Because the ending positions needs to be offset by 1
     * because of the absolute character positions of substring.
     *
     * @param endPos  End position of a plain text segment
     * @param mapping Text to XML index position mapping
     * @return End position of a xml text segment
     */
    public static int getEndPositionFromTxtToXml(int endPos, TreeMap<Integer, Integer> mapping) {
        Map.Entry<Integer, Integer> entry = mapping.floorEntry(endPos - 1);
        int offset = endPos - entry.getKey();
        return entry.getValue() + offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextFeatureDB)) return false;

        TextFeatureDB that = (TextFeatureDB) o;

        return getSrcDocumentId().equals(that.getSrcDocumentId());
    }

	@Override
    public int hashCode() {
        return getSrcDocumentId().hashCode();
    }

	public String getText() {
		// TODO Auto-generated method stub
		return this.text;
	}

	public void setText(String newText) {
		// TODO Auto-generated method stub
		
		this.text = newText;
		
	}
	

	public void setTags(String newtags) {
		// TODO Auto-generated method stub
		
		this.tags = newtags;
		
	}
	
	public void setposTags(String pos) {
		// TODO Auto-generated method stub
		
		this.postags = pos;
		
	}

	public String getMappingString() {
		// TODO Auto-generated method stub
		return this.mappingString;
	}
	
	public String gettaggingString() {
		// TODO Auto-generated method stub
		return this.taggingString;
	}

	public String gettags() {
		// TODO Auto-generated method stub
		return this.tags;
	}
	
	public String gettagspos() {
		// TODO Auto-generated method stub
		return this.postags;
	}


}
