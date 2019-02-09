package oneapp.workbox.services.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class WorkFlowArtifact implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WorkFlowArtifact() {
		super();
	}

	public WorkFlowArtifact(String id, String artifactName, String artifactClassDefinition, String artifactId,
			String workFlowDefId) {
		super();
		this.id = id;
		this.artifactName = artifactName;
		this.artifactClassDefinition = artifactClassDefinition;
		this.artifactId = artifactId;
		this.workFlowDefId = workFlowDefId;
	}

	@Id
	@Column(name = "ID", length = 70)
	private String id;

	@Column(name = "ARTIFACT_NAME", length = 60)
	private String artifactName;

	@Column(name = "ARTIFACT_CLASS_DEFINITION", length = 50)
	private String artifactClassDefinition;

	@Column(name = "ARTIFACT_ID", length = 70)
	private String artifactId;

	@Id
	@Column(name = "WORKFLOW_DEFINITION_ID", length = 60)
	private String workFlowDefId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getArtifactName() {
		return artifactName;
	}

	public void setArtifactName(String artifactName) {
		this.artifactName = artifactName;
	}

	public String getArtifactClassDefinition() {
		return artifactClassDefinition;
	}

	public void setArtifactClassDefinition(String artifactClassDefinition) {
		this.artifactClassDefinition = artifactClassDefinition;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getWorkFlowDefId() {
		return workFlowDefId;
	}

	public void setWorkFlowDefId(String workFlowDefId) {
		this.workFlowDefId = workFlowDefId;
	}

	@Override
	public String toString() {
		return "WorkFlowArtifact [id=" + id + ", artifactName=" + artifactName + ", artifactClassDefinition="
				+ artifactClassDefinition + ", artifactId=" + artifactId + ", workFlowDefId=" + workFlowDefId + "]";
	}

}
