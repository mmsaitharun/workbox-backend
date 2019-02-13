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

	public WorkFlowEvent(String id, String artifactName, String artifactClassDefinition, String artifactId, String workFlowDefId, String artifactIcon) {
		super(id, artifactName, artifactClassDefinition, artifactId, workFlowDefId, artifactIcon);
	}

	@Override
	public String toString() {
		return "WorkFlowEvent [getId()=" + getId() + ", getArtifactName()=" + getArtifactName()
				+ ", getArtifactClassDefinition()=" + getArtifactClassDefinition() + ", getArtifactId()="
				+ getArtifactId() + ", toString()=" + super.toString() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + "]";
	}
	
}
