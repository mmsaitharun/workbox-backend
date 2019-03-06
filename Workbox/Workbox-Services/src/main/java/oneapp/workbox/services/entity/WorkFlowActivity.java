package oneapp.workbox.services.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import oneapp.workbox.services.dto.WorkFlowDetailAttribute;

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
			String activityPriority, String artifactIcon) {
		super(id, artifactName, artifactClassDefinition, artifactId, workFlowDefId, artifactIcon);
		this.activityPriority = activityPriority;
	}

	@Column(name = "ACTIVITY_PRIORITY", length = 40)
	private String activityPriority;
	
	@Transient
	private List<WorkFlowDetailAttribute> attributes;

	public String getActivityPriority() {
		return activityPriority;
	}

	public void setActivityPriority(String activityPriority) {
		this.activityPriority = activityPriority;
	}

	public List<WorkFlowDetailAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<WorkFlowDetailAttribute> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return "WorkFlowActivity [activityPriority=" + activityPriority + ", getId()=" + getId()
				+ ", getArtifactName()=" + getArtifactName() + ", getArtifactClassDefinition()="
				+ getArtifactClassDefinition() + ", getArtifactId()=" + getArtifactId() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
	}

}
