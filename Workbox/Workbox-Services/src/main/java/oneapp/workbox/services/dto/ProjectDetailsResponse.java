package oneapp.workbox.services.dto;

import java.util.List;

public class ProjectDetailsResponse {

	public ProjectDetailsResponse() {
		super();
	}

	public ProjectDetailsResponse(List<ProjectDetail> projectDetails, ResponseMessage responseMessage) {
		super();
		this.projectDetails = projectDetails;
		this.responseMessage = responseMessage;
	}

	List<ProjectDetail> projectDetails;
	ResponseMessage responseMessage;

	public List<ProjectDetail> getProjectDetails() {
		return projectDetails;
	}

	public void setProjectDetails(List<ProjectDetail> projectDetails) {
		this.projectDetails = projectDetails;
	}

	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}

	@Override
	public String toString() {
		return "ProjectDetailsResponse [projectDetails=" + projectDetails + ", responseMessage=" + responseMessage
				+ "]";
	}

}
