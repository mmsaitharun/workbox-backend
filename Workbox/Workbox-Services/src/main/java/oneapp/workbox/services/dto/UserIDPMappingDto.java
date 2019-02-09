package oneapp.workbox.services.dto;

public class UserIDPMappingDto {

	private String serialId;
	private String userFirstName;
	private String userLastName;
	private String userEmail;
	private String userRole;
	private String userLoginName;
	private String userId;
	

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

	@Override
	public String toString() {
		return "UserIDPMappingDto [serialId=" + serialId + ", userFirstName=" + userFirstName + ", userLastName="
				+ userLastName + ", userEmail=" + userEmail + ", userRole=" + userRole + ", userLoginName="
				+ userLoginName + ", userId=" + userId + "]";
	}
}
