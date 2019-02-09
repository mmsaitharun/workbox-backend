package oneapp.workbox.services.dto;

import java.util.List;

public class ProcessConfigResponse {

	private List<ProcessConfigDto> processConfigDtos;
	private ResponseMessage responseMessage;

	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}

	public List<ProcessConfigDto> getProcessConfigDtos() {
		return processConfigDtos;
	}

	public void setProcessConfigDtos(List<ProcessConfigDto> processConfigDtos) {
		this.processConfigDtos = processConfigDtos;
	}

	@Override
	public String toString() {
		return "ProcessConfigResponse [processConfigDtos=" + processConfigDtos + "]";
	}

}
