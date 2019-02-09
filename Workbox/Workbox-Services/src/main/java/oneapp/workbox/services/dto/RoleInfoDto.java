package oneapp.workbox.services.dto;

public class RoleInfoDto {

	public RoleInfoDto() {
	}

	public RoleInfoDto(String roleUniqName, String roleDescription, Boolean isAdmin) {
		this.roleUniqName = roleUniqName;
		this.roleDescription = roleDescription;
		this.isAdmin = isAdmin;
	}

	private String roleUniqName;
	private String roleDescription;
	private Boolean isAdmin;

	public String getRoleUniqName() {
		return roleUniqName;
	}

	public void setRoleUniqName(String roleUniqName) {
		this.roleUniqName = roleUniqName;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	@Override
	public String toString() {
		return "RoleInfoDto [roleUniqName=" + roleUniqName + ", roleDescription=" + roleDescription + ", isAdmin="
				+ isAdmin + "]";
	}

}
