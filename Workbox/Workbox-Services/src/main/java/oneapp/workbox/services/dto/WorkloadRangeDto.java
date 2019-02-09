package oneapp.workbox.services.dto;

public class WorkloadRangeDto {

	private String loadType;
	private Integer lowLimit;
	private Integer highLimit;

	public String getLoadType() {
		return loadType;
	}

	public void setLoadType(String loadType) {
		this.loadType = loadType;
	}

	public Integer getLowLimit() {
		return lowLimit;
	}

	public void setLowLimit(Integer lowLimit) {
		this.lowLimit = lowLimit;
	}

	public Integer getHighLimit() {
		return highLimit;
	}

	public void setHighLimit(Integer highLimit) {
		this.highLimit = highLimit;
	}

	@Override
	public String toString() {
		return "WorkloadRangeDto [loadType=" + loadType + ", lowLimit=" + lowLimit + ", highLimit=" + highLimit + "]";
	}

}
