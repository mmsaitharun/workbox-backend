package oneapp.workbox.services.dto;

import java.util.List;

public class ActionDto {

	public ActionDto() {
		super();
	}

	public ActionDto(String actionType, String userId, List<String> instanceList, String comment) {
		super();
		this.actionType = actionType;
		this.userId = userId;
		this.instanceList = instanceList;
		this.comment = comment;
	}

	private String action;
	private String actionType;
	private String userId;
	private String userDisplay;
	private List<String> instanceList;
	private String comment;
	private String Processor;
	private Boolean isAdmin = false;
	private String origin;
	private String subject;

	public String getProcessor() {
		return Processor;
	}

	public void setProcessor(String processor) {
		Processor = processor;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserDisplay() {
		return userDisplay;
	}

	public void setUserDisplay(String userDisplay) {
		this.userDisplay = userDisplay;
	}

	public List<String> getInstanceList() {
		return instanceList;
	}

	public void setInstanceList(List<String> instanceList) {
		this.instanceList = instanceList;
	}

	public Boolean isAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public String toString() {
		return "ActionDto [action=" + action + ", actionType=" + actionType + ", userId=" + userId + ", userDisplay="
				+ userDisplay + ", instanceList=" + instanceList + ", comment=" + comment + ", Processor=" + Processor
				+ ", isAdmin=" + isAdmin + "]";
	}

}
