package oneapp.workbox.services.dto;

import java.util.List;

public class SlaProcessNamesResponse {

	private List<SlaProcessNameListDto> slaProcessNames;
	private ResponseMessage responseMessage;

	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(ResponseMessage responseMessage2) {
		this.responseMessage = responseMessage2;
	}

	public List<SlaProcessNameListDto> getSlaProcessNames() {
		return slaProcessNames;
	}

	public void setSlaProcessNames(List<SlaProcessNameListDto> slaProcessNames) {
		this.slaProcessNames = slaProcessNames;
	}

	@Override
	public String toString() {
		return "SlaProcessNamesResponse [slaProcessNames=" + slaProcessNames + "]";
	}

}
