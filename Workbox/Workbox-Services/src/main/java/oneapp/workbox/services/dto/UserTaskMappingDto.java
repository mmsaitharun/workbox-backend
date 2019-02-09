package oneapp.workbox.services.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserTaskMappingDto {

	private String id;
	private String substitutingUser;
	private String substitutedUser;
	private String taskId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSubstitutingUser() {
		return substitutingUser;
	}
	public void setSubstitutingUser(String substitutingUser) {
		this.substitutingUser = substitutingUser;
	}
	public String getSubstitutedUser() {
		return substitutedUser;
	}
	public void setSubstitutedUser(String substitutedUser) {
		this.substitutedUser = substitutedUser;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	@Override
	public String toString() {
		return "UserTaskMappingDto [id=" + id + ", substitutingUser=" + substitutingUser + ", substitutedUser="
				+ substitutedUser + ", taskId=" + taskId + "]";
	}
	
}
