package oneapp.workbox.services.dto;

import java.util.List;

import oneapp.workbox.services.entity.ProcessDetail;

public class ProcessDetailResponse {

	public ProcessDetailResponse() {
		super();
	}

	public ProcessDetailResponse(List<ProcessDetail> processDetails, ResponseMessage responseMessage) {
		super();
		this.processDetails = processDetails;
		this.responseMessage = responseMessage;
	}

	List<ProcessDetail> processDetails;
	ResponseMessage responseMessage;

	public List<ProcessDetail> getProcessDetails() {
		return processDetails;
	}

	public void setProcessDetails(List<ProcessDetail> processDetails) {
		this.processDetails = processDetails;
	}

	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}

	@Override
	public String toString() {
		return "ProcessDetailsResponse [processDetails=" + processDetails + ", responseMessage=" + responseMessage
				+ "]";
	}

}
