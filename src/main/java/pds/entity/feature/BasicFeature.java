package pds.entity.feature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;

/**
 * Basic attribute class for most of the used features.
 * <p>
 * Hint regarding scopes:
 * In Elasticsearch there are no direct references, such as foreign key,
 * so we double-filed the attribute "scope" from the @DocumentData for
 * easy searching.
 *
 * @author Vincent Stange
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class BasicFeature {

    @Id
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private Integer srcDocumentId;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private List<String> scopes;
}

