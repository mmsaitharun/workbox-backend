package oneapp.workbox.services.entity;

import java.util.Date;
import java.util.List;

public class TaskDetail {

	public TaskDetail() {
		super();
	}

	public TaskDetail(String taskId, String taskStatus, String materialId, String materialUniqueId,
			String materialDescription, String extension, String extensionKey, String taskHealth,
			List<TaskUserDetails> userDetails, String taskName, Date taskCreatedAt, String taskDescription, String taskSubject) {
		super();
		this.taskId = taskId;
		this.taskStatus = taskStatus;
		this.materialId = materialId;
		this.materialUniqueId = materialUniqueId;
		this.materialDescription = materialDescription;
		this.extension = extension;
		this.extensionKey = extensionKey;
		this.taskHealth = taskHealth;
		this.userDetails = userDetails;
		this.taskName = taskName;
		this.taskCreatedAt = taskCreatedAt;
		this.taskDescription = taskDescription;
		this.taskSubject = taskSubject;
	}

	private String taskId;
	private String taskStatus;
	private String materialId;
	private String materialUniqueId;
	private String materialDescription;
	private String extension;
	private String extensionKey;
	private String taskHealth;
	private List<TaskUserDetails> userDetails;

	private String taskName;
	private Date taskCreatedAt;
	private String taskCreatedAtString;
	private String taskDescription;
	private String taskSubject;
	
	public String getTaskSubject() {
		return taskSubject;
	}

	public void setTaskSubject(String taskSubject) {
		this.taskSubject = taskSubject;
	}

	private int taskUsersCount;
	
	public String getTaskCreatedAtString() {
		return taskCreatedAtString;
	}

	public void setTaskCreatedAtString(String taskCreatedAtString) {
		this.taskCreatedAtString = taskCreatedAtString;
	}

	public int getTaskUsersCount() {
		return taskUsersCount;
	}

	public void setTaskUsersCount(int taskUsersCount) {
		this.taskUsersCount = taskUsersCount;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Date getTaskCreatedAt() {
		return taskCreatedAt;
	}

	public void setTaskCreatedAt(Date taskCreatedAt) {
		this.taskCreatedAt = taskCreatedAt;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public List<TaskUserDetails> getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(List<TaskUserDetails> userDetails) {
		this.userDetails = userDetails;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getMaterialUniqueId() {
		return materialUniqueId;
	}

	public void setMaterialUniqueId(String materialUniqueId) {
		this.materialUniqueId = materialUniqueId;
	}

	public String getMaterialDescription() {
		return materialDescription;
	}

	public void setMaterialDescription(String materialDescription) {
		this.materialDescription = materialDescription;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getExtensionKey() {
		return extensionKey;
	}

	public void setExtensionKey(String extensionKey) {
		this.extensionKey = extensionKey;
	}

	public String getTaskHealth() {
		return taskHealth;
	}

	public void setTaskHealth(String taskHealth) {
		this.taskHealth = taskHealth;
	}

	@Override
	public String toString() {
		return "TaskDetail [taskId=" + taskId + ", taskStatus=" + taskStatus + ", materialId=" + materialId
				+ ", materialUniqueId=" + materialUniqueId + ", materialDescription=" + materialDescription
				+ ", extension=" + extension + ", extensionKey=" + extensionKey + ", taskHealth=" + taskHealth
				+ ", userDetails=" + userDetails + ", taskName=" + taskName + ", taskCreatedAt=" + taskCreatedAt
				+ ", taskCreatedAtString=" + taskCreatedAtString + ", taskDescription=" + taskDescription
				+ ", taskUsersCount=" + taskUsersCount + "]";
	}

}
