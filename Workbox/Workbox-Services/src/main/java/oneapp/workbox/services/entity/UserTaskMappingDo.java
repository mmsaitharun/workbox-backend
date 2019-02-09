package oneapp.workbox.services.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USER_TASK_MAPPING")
public class UserTaskMappingDo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", length = 100)
	private String id = UUID.randomUUID().toString().replaceAll("-", "");

	@Column(name = "SUBSTITUTING_USER", length = 100)
	private String substitutingUser;

	@Column(name = "SUBSTITUTED_USER", length = 100)
	private String substitutedUser;

	@Column(name = "TASK_ID", length = 200)
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
		return "UserTaskMappingDo [id=" + id + ", substitutingUser=" + substitutingUser + ", substitutedUser="
				+ substitutedUser + ", taskId=" + taskId + "]";
	}

}