package oneapp.workbox.services.dto;

public class ReportAgingDto {

	private String id;
	private String reportName;
	private Integer lowerSegment;
	private Integer higherSegment;
	private String displayName;
	private Integer agingRange;
	private Boolean isDeleted;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public Integer getLowerSegment() {
		return lowerSegment;
	}

	public void setLowerSegment(Integer lowerSegment) {
		this.lowerSegment = lowerSegment;
	}

	public Integer getHigherSegment() {
		return higherSegment;
	}

	public void setHigherSegment(Integer higherSegment) {
		this.higherSegment = higherSegment;
	}

	/**
	 * @return the agingRange
	 */
	public Integer getAgingRange() {
		return agingRange;
	}

	/**
	 * @param agingRange
	 *            the agingRange to set
	 */
	public void setAgingRange(Integer agingRange) {
		this.agingRange = agingRange;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	public String toString() {
		return "ReportAgingDto [id=" + id + ", reportName=" + reportName + ", lowerSegment=" + lowerSegment
				+ ", higherSegment=" + higherSegment + ", displayName=" + displayName + ", agingRange=" + agingRange
				+ ", isDeleted=" + isDeleted + "]";
	}

}
