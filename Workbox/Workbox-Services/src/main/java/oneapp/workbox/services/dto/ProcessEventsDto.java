package oneapp.workbox.services.dto;

import java.util.Date;

public class ProcessEventsDto {
	
	private String processId;
	private String requestId;
	private String name;
	private String subject;
	private String status;
	private String startedBy;
	private Date startedAt;
	private Date completedAt;
	private String startedByUser;
	private String startedAtInString;
	private String completedAtInString;
	private String startedByDisplayName;
	private String processDisplayName;
    private String processDefinitionId;
    
    /* Key added for storing processes to db */
    private String tId;
    
    private String projectId;
    
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String gettId() {
		return tId;
	}

	public void settId(String tId) {
		this.tId = tId;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStartedBy() {
		return startedBy;
	}

	public void setStartedBy(String startedBy) {
		this.startedBy = startedBy;
	}

	public Date getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}

	public Date getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(Date completedAt) {
		this.completedAt = completedAt;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getStartedByUser() {
		return startedByUser;
	}

	public void setStartedByUser(String startedByUser) {
		this.startedByUser = startedByUser;
	}

	public String getStartedAtInString() {
		return startedAtInString;
	}

	public void setStartedAtInString(String startedAtInString) {
		this.startedAtInString = startedAtInString;
	}

	public String getCompletedAtInString() {
		return completedAtInString;
	}

	public void setCompletedAtInString(String completedAtInString) {
		this.completedAtInString = completedAtInString;
	}

	public String getStartedByDisplayName() {
		return startedByDisplayName;
	}

	public void setStartedByDisplayName(String startedByDisplayName) {
		this.startedByDisplayName = startedByDisplayName;
	}

	public String getProcessDisplayName() {
		return processDisplayName;
	}

	public void setProcessDisplayName(String processDisplayName) {
		this.processDisplayName = processDisplayName;
	}

	@Override
	public String toString() {
		return "ProcessEventsDto [processId=" + processId + ", requestId=" + requestId + ", name=" + name + ", subject="
				+ subject + ", status=" + status + ", startedBy=" + startedBy + ", startedAt=" + startedAt
				+ ", completedAt=" + completedAt + ", startedByUser=" + startedByUser + ", startedAtInString="
				+ startedAtInString + ", completedAtInString=" + completedAtInString + ", startedByDisplayName="
				+ startedByDisplayName + ", processDisplayName=" + processDisplayName + ", processDefinitionId="
				+ processDefinitionId + ", tId=" + tId + "]";
	}

}
