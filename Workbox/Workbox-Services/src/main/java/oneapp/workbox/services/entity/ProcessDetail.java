package oneapp.workbox.services.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "PROCESS_DETAIL")
public class ProcessDetail {

	public ProcessDetail() {
		super();
	}

	public ProcessDetail(String processId, String leadCategory, String materialType, String materialUniqueId,
			String materialId, String materialDescription, String requestedBy, String leadCountry,
			String projectDescription, String requiredTaskCsv, String region, String projectId, String processSubject) {
		super();
		this.processId = processId;
		this.leadCategory = leadCategory;
		this.materialType = materialType;
		this.materialUniqueId = materialUniqueId;
		this.materialId = materialId;
		this.materialDescription = materialDescription;
		this.requestedBy = requestedBy;
		this.leadCountry = leadCountry;
		this.projectDescription = projectDescription;
		this.requiredTaskCsv = requiredTaskCsv;
		this.region = region;
		this.projectId = projectId;
		this.processSubject = processSubject;
	}

	@Id
	@Column(name = "PROCESS_ID", length = 50)
	private String processId;
	@Column(name = "LEAD_CATEGORY", length = 25)
	private String leadCategory;
	@Column(name = "MATERIAL_TYPE", length = 45)
	private String materialType;
	@Column(name = "MATERIAL_UNIQUE_ID", length = 30)
	private String materialUniqueId;
	@Column(name = "MATERIAL_ID", length = 30)
	private String materialId;
	@Column(name = "MATERIAL_DESCRIPTION", length = 60)
	private String materialDescription;
	@Column(name = "REQUESTED_BY", length = 45)
	private String requestedBy;
	@Column(name = "LEAD_COUNTRY", length = 30)
	private String leadCountry;
	@Column(name = "PROJECT_DESCRIPTION", length = 50)
	private String projectDescription;
	@Column(name = "REQUIRED_TASK_CSV", length = 50)
	private String requiredTaskCsv;
	@Column(name = "REGION", length = 30)
	private String region;
	@Column(name = "PROJECT_ID", length = 50)
	private String projectId;
	@Column(name = "NODE_ID", length = 50)
	private String nodeId;
	@Column(name = "KEY", length = 50)
	private String key;
	@Column(name = "SUB_KEY", length = 50)
	private String subKey;
	@Column(name = "COUNTRY", length = 50)
	private String country;
	
	@Transient
	private String requestId;
	
	@Transient
	private String processStatus;
	
	@Transient
	private String processName;
	
	@Transient
	private String processSubject;
	
	public String getProcessSubject() {
		return processSubject;
	}

	public void setProcessSubject(String processSubject) {
		this.processSubject = processSubject;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public ProcessDetail(String processId, String leadCategory, String materialType, String materialUniqueId,
			String materialId, String materialDescription, String requestedBy, String leadCountry,
			String projectDescription, String requiredTaskCsv, String region, String projectId, String requestId,
			String processStatus) {
		super();
		this.processId = processId;
		this.leadCategory = leadCategory;
		this.materialType = materialType;
		this.materialUniqueId = materialUniqueId;
		this.materialId = materialId;
		this.materialDescription = materialDescription;
		this.requestedBy = requestedBy;
		this.leadCountry = leadCountry;
		this.projectDescription = projectDescription;
		this.requiredTaskCsv = requiredTaskCsv;
		this.region = region;
		this.projectId = projectId;
		this.requestId = requestId;
		this.processStatus = processStatus;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getLeadCategory() {
		return leadCategory;
	}

	public void setLeadCategory(String leadCategory) {
		this.leadCategory = leadCategory;
	}

	public String getMaterialType() {
		return materialType;
	}

	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}

	public String getMaterialUniqueId() {
		return materialUniqueId;
	}

	public void setMaterialUniqueId(String materialUniqueId) {
		this.materialUniqueId = materialUniqueId;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getMaterialDescription() {
		return materialDescription;
	}

	public void setMaterialDescription(String materialDescription) {
		this.materialDescription = materialDescription;
	}

	public String getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}

	public String getLeadCountry() {
		return leadCountry;
	}

	public void setLeadCountry(String leadCountry) {
		this.leadCountry = leadCountry;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public String getRequiredTaskCsv() {
		return requiredTaskCsv;
	}

	public void setRequiredTaskCsv(String requiredTaskCsv) {
		this.requiredTaskCsv = requiredTaskCsv;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSubKey() {
		return subKey;
	}

	public void setSubKey(String subKey) {
		this.subKey = subKey;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "ProcessDetail [processId=" + processId + ", leadCategory=" + leadCategory + ", materialType="
				+ materialType + ", materialUniqueId=" + materialUniqueId + ", materialId=" + materialId
				+ ", materialDescription=" + materialDescription + ", requestedBy=" + requestedBy + ", leadCountry="
				+ leadCountry + ", projectDescription=" + projectDescription + ", requiredTaskCsv=" + requiredTaskCsv
				+ ", region=" + region + ", projectId=" + projectId + ", nodeId=" + nodeId + ", key=" + key
				+ ", subKey=" + subKey + ", country=" + country + ", requestId=" + requestId + ", processStatus="
				+ processStatus + ", processName=" + processName + ", processSubject=" + processSubject + "]";
	}

}
