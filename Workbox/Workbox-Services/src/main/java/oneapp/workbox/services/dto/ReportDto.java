package oneapp.workbox.services.dto;

import java.util.List;

public class ReportDto {
	private String reportName;
	private List<ReportAgingDto> reportDtoList;

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public List<ReportAgingDto> getReportDtoList() {
		return reportDtoList;
	}

	public void setReportDtoList(List<ReportAgingDto> reportDtoList) {
		this.reportDtoList = reportDtoList;
	}

	@Override
	public String toString() {
		return "DifferentReportDto [reportName=" + reportName + ", reportDtoList=" + reportDtoList + "]";
	}

}
