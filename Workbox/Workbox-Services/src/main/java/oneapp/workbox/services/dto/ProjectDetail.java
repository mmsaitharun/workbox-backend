package oneapp.workbox.services.dto;

import java.util.Date;

public class ProjectDetail {

	public ProjectDetail() {
		super();
	}

	public ProjectDetail(Integer projectId, String projectDescription, String reason, String projectLead,
			String regionCode, Date createdAt, String createdAtInString, String userCreated) {
		super();
		this.projectId = projectId;
		this.projectDescription = projectDescription;
		this.reason = reason;
		this.projectLead = projectLead;
		this.regionCode = regionCode;
		this.createdAt = createdAt;
		this.createdAtInString = createdAtInString;
		this.userCreated = userCreated;
	}

	private Integer projectId;
	private String projectDescription;
	private String reason;
	private String projectLead;
	private String regionCode;
	private Date createdAt;
	private String createdAtInString;
	private String userCreated;

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getProjectLead() {
		return projectLead;
	}

	public void setProjectLead(String projectLead) {
		this.projectLead = projectLead;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedAtInString() {
		return createdAtInString;
	}

	public void setCreatedAtInString(String createdAtInString) {
		this.createdAtInString = createdAtInString;
	}

	public String getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
	}

	@Override
	public String toString() {
		return "ProjectDetail [projectId=" + projectId + ", projectDescription=" + projectDescription + ", reason="
				+ reason + ", projectLead=" + projectLead + ", regionCode=" + regionCode + ", createdAt=" + createdAt
				+ ", createdAtInString=" + createdAtInString + ", userCreated=" + userCreated + "]";
	}

}
