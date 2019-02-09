package oneapp.workbox.services.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "WORKFLOW_EVENTS")
public class WorkFlowEvent extends WorkFlowArtifact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WorkFlowEvent() {
		super();
	}

	public WorkFlowEvent(String id, String artifactName, String artifactClassDefinition, String artifactId, String workFlowDefId) {
		super(id, artifactName, artifactClassDefinition, artifactId, workFlowDefId);
	}

	@Override
	public String toString() {
		return "WorkFlowEvent [getId()=" + getId() + ", getArtifactName()=" + getArtifactName()
				+ ", getArtifactClassDefinition()=" + getArtifactClassDefinition() + ", getArtifactId()="
				+ getArtifactId() + ", toString()=" + super.toString() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + "]";
	}
	
}
