package oneapp.workbox.services.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "WORKFLOW_SEQUENCE_FLOWS")
public class WorkFlowSequenceFlow extends WorkFlowArtifact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WorkFlowSequenceFlow() {
		super();
	}

	public WorkFlowSequenceFlow(String id, String artifactName, String artifactClassDefinition, String artifactId, String workFlowDefId,
			String sourceRef, String targetRef) {
		super(id, artifactName, artifactClassDefinition, artifactId, workFlowDefId);
		this.sourceRef = sourceRef;
		this.targetRef = targetRef;
	}

	@Column(name = "FLOW_SOURCE_REF", length = 70)
	private String sourceRef;

	@Column(name = "FLOW_SOURCE_TARGET", length = 70)
	private String targetRef;

	public String getSourceRef() {
		return sourceRef;
	}

	public void setSourceRef(String sourceRef) {
		this.sourceRef = sourceRef;
	}

	public String getTargetRef() {
		return targetRef;
	}

	public void setTargetRef(String targetRef) {
		this.targetRef = targetRef;
	}

	@Override
	public String toString() {
		return "WorkFlowSequenceFlow [sourceRef=" + sourceRef + ", targetRef=" + targetRef + ", getId()=" + getId()
				+ ", getArtifactName()=" + getArtifactName() + ", getArtifactClassDefinition()="
				+ getArtifactClassDefinition() + ", getArtifactId()=" + getArtifactId() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
	}

}
