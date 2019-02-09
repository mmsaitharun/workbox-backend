package oneapp.workbox.services.entity;

public class TaskUserDetails {

	public TaskUserDetails() {
		super();
	}

	public TaskUserDetails(String userId, String userFirstName, String userLastName, String userName,
			String userEmail) {
		super();
		this.userId = userId;
		this.userFirstName = userFirstName;
		this.userLastName = userLastName;
		this.userName = userName;
		this.userEmail = userEmail;
	}

	private String userId;
	private String userFirstName;
	private String userLastName;
	private String userName;
	private String userEmail;

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "TaskUserDetails [userId=" + userId + ", userFirstName=" + userFirstName + ", userLastName="
				+ userLastName + ", userName=" + userName + "]";
	}

}
