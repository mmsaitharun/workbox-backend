package oneapp.workbox.services.dto;

import java.util.List;

public class UserDetailsDto {
	public UserDetailsDto() {
		super();
	}

	public UserDetailsDto(String userId, String emailId) {
		this.userId = userId;
		this.emailId = emailId;
	}

	private String userId;
	private String emailId;
	private String firstName;
	private String lastName;
	private String mobileNo;
	private String displayName;

	private Boolean isAdmin;
	private List<String> userRoles;
	private List<String> userGroups;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public List<String> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List<String> userRoles) {
		this.userRoles = userRoles;
	}

	public List<String> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<String> userGroups) {
		this.userGroups = userGroups;
	}

	@Override
	public String toString() {
		return "UserDetailsDto [userId=" + userId + ", emailId=" + emailId + ", firstName=" + firstName + ", lastName="
				+ lastName + ", mobileNo=" + mobileNo + ", displayName=" + displayName + ", isAdmin=" + isAdmin
				+ ", userRoles=" + userRoles + ", userGroups=" + userGroups + "]";
	}
	
}
