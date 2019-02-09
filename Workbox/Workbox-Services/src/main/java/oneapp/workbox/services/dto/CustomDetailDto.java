package oneapp.workbox.services.dto;

import java.util.List;

public class CustomDetailDto {

	public CustomDetailDto() {
		super();
	}

	public CustomDetailDto(List<DynamicDetailDto> dynamicDetails, List<DynamicButtonDto> dynamicButtons,
			ResponseMessage responseMessage) {
		super();
		this.dynamicDetails = dynamicDetails;
		this.dynamicButtons = dynamicButtons;
		this.responseMessage = responseMessage;
	}

	private List<DynamicDetailDto> dynamicDetails;
	List<DynamicButtonDto> dynamicButtons;
	private String taskId;
	
	/* To be removed later */
	private String subject;
	
	private ResponseMessage responseMessage;

	public List<DynamicDetailDto> getDynamicDetails() {
		return dynamicDetails;
	}

	public void setDynamicDetails(List<DynamicDetailDto> dynamicDetails) {
		this.dynamicDetails = dynamicDetails;
	}

	public List<DynamicButtonDto> getDynamicButtons() {
		return dynamicButtons;
	}

	public void setDynamicButtons(List<DynamicButtonDto> dynamicButtons) {
		this.dynamicButtons = dynamicButtons;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}

}
