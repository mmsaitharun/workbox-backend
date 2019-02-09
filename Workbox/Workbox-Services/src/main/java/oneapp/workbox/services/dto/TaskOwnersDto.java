package oneapp.workbox.services.dto;

public class TaskOwnersDto {

	private String eventId;
	private String taskOwner;
	private Boolean isProcessed;
	private Boolean isSubstituted;
	private String taskOwnerDisplayName;
	private String ownerEmail;
	private Boolean enRoute;
	
	
	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getTaskOwner() {
		return taskOwner;
	}

	public void setTaskOwner(String taskOwner) {
		this.taskOwner = taskOwner;
	}

	public Boolean getIsProcessed() {
		return isProcessed;
	}

	public void setIsProcessed(Boolean isProcessed) {
		this.isProcessed = isProcessed;
	}

	public String getTaskOwnerDisplayName() {
		return taskOwnerDisplayName;
	}

	public void setTaskOwnerDisplayName(String taskOwnerDisplayName) {
		this.taskOwnerDisplayName = taskOwnerDisplayName;
	}

	public Boolean getEnRoute() {
		return enRoute;
	}

	public void setEnRoute(Boolean enRoute) {
		this.enRoute = enRoute;
	}

	@Override
	public String toString() {
		return "TaskOwnersDto [eventId=" + eventId + ", taskOwner=" + taskOwner + ", isProcessed=" + isProcessed
				+ ", taskOwnerDisplayName=" + taskOwnerDisplayName + ", ownerEmail=" + ownerEmail + "]";
	}

	public Boolean getIsSubstituted() {
		return isSubstituted;
	}

	public void setIsSubstituted(Boolean isSubstituted) {
		this.isSubstituted = isSubstituted;
	}
	
	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

}
