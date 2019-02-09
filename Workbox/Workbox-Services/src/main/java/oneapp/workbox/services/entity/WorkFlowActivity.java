package oneapp.workbox.services.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "WORKFLOW_ACTIVITIES")
public class WorkFlowActivity extends WorkFlowArtifact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WorkFlowActivity() {
		super();
	}

	public WorkFlowActivity(String id, String artifactName, String artifactClassDefinition, String artifactId, String workFlowDefId,
			String activityPriority) {
		super(id, artifactName, artifactClassDefinition, artifactId, workFlowDefId);
		this.activityPriority = activityPriority;
	}

	@Column(name = "ACTIVITY_PRIORITY", length = 40)
	private String activityPriority;

	public String getActivityPriority() {
		return activityPriority;
	}

	public void setActivityPriority(String activityPriority) {
		this.activityPriority = activityPriority;
	}

	@Override
	public String toString() {
		return "WorkFlowActivity [activityPriority=" + activityPriority + ", getId()=" + getId()
				+ ", getArtifactName()=" + getArtifactName() + ", getArtifactClassDefinition()="
				+ getArtifactClassDefinition() + ", getArtifactId()=" + getArtifactId() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
	}

}
