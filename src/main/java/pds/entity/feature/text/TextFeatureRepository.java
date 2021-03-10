package pds.entity.feature.text;


import java.util.Optional;

/**
 * Standard Text Respository based on a ElasticSearch Respository.
 * 
 * @author Vincent Stange
 */
public interface TextFeatureRepository extends ElasticsearchRepository<TextFeature, Integer> {

    /**
     * Gets the TextFeature for a specific document.
     * 
     * @param srcDocumentId ElasticSearch id of a document.
     * @return TextFeature or null, if no TextFeature is given for the document.
     */
    Optional<TextFeature> getBySrcDocumentId(Integer srcDocumentId);

}

