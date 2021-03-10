package pds.entity.feature;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.List;

/**
 * Basic attribute class for most of the used features (database entity).
 * <p>
 * Hint regarding scopes:
 * Scopes are not saved for any db entities! Field exists only for backward compatibility
 * for elasticsearch documents.
 *
 * @author Vincent Stange
 */
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class BasicFeatureDB {

    public BasicFeatureDB(Integer srcDocumentId2, List<String> scopes2) {
		// TODO Auto-generated constructor stub
	}

	@Id
    @Column(name = "src_document_id", unique = true, nullable = false, updatable = false)
    private Integer srcDocumentId;

    @Transient
    private List<String> scopes;
}
