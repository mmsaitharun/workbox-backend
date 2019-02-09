package oneapp.workbox.services.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USER_IDP_MAPPING")
public class UserIDPMappingDo {

	@Column(name = "SERIAL_ID", length = 150)
	private String serialId;

	@Column(name = "USER_ID", length = 100)
	private String userId;

	@Column(name = "USER_FIRST_NAME", length = 100)
	private String userFirstName;

	@Column(name = "USER_LAST_NAME", length = 100)
	private String userLastName;

	@Column(name = "USER_EMAIL", length = 100)
	private String userEmail;

	@Column(name = "USER_ROLE", length = 80)
	private String userRole;

	@Id
	@Column(name = "USER_LOGIN_NAME", length = 100)
	private String userLoginName;

	@Column(name = "TASK_ASSIGNABLE", length = 10)
	private String taskAssignable;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSerialId() {
		return serialId;
	}

	public void setSerialId(String serialId) {
		this.serialId = serialId;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getUserLoginName() {
		return userLoginName;
	}

	public void setUserLoginName(String userLoginName) {
		this.userLoginName = userLoginName;
	}

	public String getTaskAssignable() {
		return taskAssignable;
	}

	public void setTaskAssignable(String taskAssignable) {
		this.taskAssignable = taskAssignable;
	}

	@Override
	public String toString() {
		return "UserIDPMappingDo [serialId=" + serialId + ", userId=" + userId + ", userFirstName=" + userFirstName
				+ ", userLastName=" + userLastName + ", userEmail=" + userEmail + ", userRole=" + userRole
				+ ", userLoginName=" + userLoginName + ", taskAssignable=" + taskAssignable + "]";
	}
}
