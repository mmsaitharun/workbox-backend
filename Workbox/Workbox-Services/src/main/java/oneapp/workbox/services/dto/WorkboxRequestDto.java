package oneapp.workbox.services.dto;

import java.util.List;

import oneapp.workbox.services.entity.CustomAttributeTemplate;

public class WorkboxRequestDto {
	private String requestId;
	private String status;
	private List<String> processName;
	private String createdBy;
	private String createdAt;
	private int skipCount;
	private int  maxCount;
	private int page;
	private String orderBy;
	private String orderType;
	private TaskOwnersDto currentUserInfo;
	private List<CustomAttributeTemplate> customAttributeTemplates;
	private Boolean isAdmin;
	private String inboxType;
	private String reportFilter;
	private Boolean isCritical;
	private String completedDate;
	private String graphName;
	
	public String getGraphName() {
		return graphName;
	}
	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}
	public String getCompletedDate() {
		return completedDate;
	}
	public void setCompletedDate(String completedDate) {
		this.completedDate = completedDate;
	}
	public Boolean getIsCritical() {
		return isCritical;
	}
	public void setIsCritical(Boolean isCritical) {
		this.isCritical = isCritical;
	}
	public String getReportFilter() {
		return reportFilter;
	}
	public void setReportFilter(String reportFilter) {
		this.reportFilter = reportFilter;
	}
	public String getInboxType() {
		return inboxType;
	}
	public void setInboxType(String inboxType) {
		this.inboxType = inboxType;
	}
	public Boolean getIsAdmin() {
		return isAdmin;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public List<CustomAttributeTemplate> getCustomAttributeTemplates() {
		return customAttributeTemplates;
	}
	public void setCustomAttributeTemplates(List<CustomAttributeTemplate> customAttributeTemplates) {
		this.customAttributeTemplates = customAttributeTemplates;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<String> getProcessName() {
		return processName;
	}
	public void setProcessName(List<String> processName) {
		this.processName = processName;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public int getSkipCount() {
		return skipCount;
	}
	public void setSkipCount(int skipCount) {
		this.skipCount = skipCount;
	}
	public int getMaxCount() {
		return maxCount;
	}
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public TaskOwnersDto getCurrentUserInfo() {
		return currentUserInfo;
	}
	public void setCurrentUserInfo(TaskOwnersDto currentUserInfo) {
		this.currentUserInfo = currentUserInfo;
	}
	
	public Boolean isAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	@Override
	public String toString() {
		return "WorkboxRequestDto [requestId=" + requestId + ", status=" + status + ", processName=" + processName
				+ ", createdBy=" + createdBy + ", createdAt=" + createdAt + ", skipCount=" + skipCount + ", maxCount="
				+ maxCount + ", page=" + page + ", orderBy=" + orderBy + ", orderType=" + orderType
				+ ", currentUserInfo=" + currentUserInfo + ", customAttributeTemplates=" + customAttributeTemplates
				+ ", isAdmin=" + isAdmin + ", inboxType=" + inboxType + ", reportFilter=" + reportFilter
				+ ", isCritical=" + isCritical + ", completedDate=" + completedDate + ", graphName=" + graphName + "]";
	}
	
	

}
