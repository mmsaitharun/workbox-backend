package oneapp.workbox.services.dto;

public class SubstitutionRulesDto {

	private String ruleId;
	private String substitutedUser;
	private String substitutedUserName;
	private String substitutingUser;
	private String substitutingUserName;
	private String startDate;
	private String endDate;
	private boolean isActive;
	private boolean isEnabled;

	private String updateAt;

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getSubstitutedUser() {
		return substitutedUser;
	}

	public void setSubstitutedUser(String substitutedUser) {
		this.substitutedUser = substitutedUser;
	}

	public String getSubstitutedUserName() {
		return substitutedUserName;
	}

	public void setSubstitutedUserName(String substitutedUserName) {
		this.substitutedUserName = substitutedUserName;
	}

	public String getSubstitutingUser() {
		return substitutingUser;
	}

	public void setSubstitutingUser(String substitutingUser) {
		this.substitutingUser = substitutingUser;
	}

	public String getSubstitutingUserName() {
		return substitutingUserName;
	}

	public void setSubstitutingUserName(String substitutingUserName) {
		this.substitutingUserName = substitutingUserName;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public String getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(String updateAt) {
		this.updateAt = updateAt;
	}

	@Override
	public String toString() {
		return "SubstitutionRulesDto [ruleId=" + ruleId + ", substitutedUser=" + substitutedUser
				+ ", substitutedUserName=" + substitutedUserName + ", substitutingUser=" + substitutingUser
				+ ", substitutingUserName=" + substitutingUserName + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", isActive=" + isActive + ", isEnabled=" + isEnabled + ", updateAt=" + updateAt + "]";
	}

}
