package oneapp.workbox.services.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProcessDetailsResponse extends PMCReportBaseDto {

	private BigDecimal count;
	private List<ProcessEventsDto> processEventsList;
	private ResponseMessage responseMessage;

	public List<ProcessEventsDto> getProcessEventsList() {
		return processEventsList;
	}

	public void setProcessEventsList(List<ProcessEventsDto> processEventsList) {
		this.processEventsList = processEventsList;
	}

	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}

	public BigDecimal getCount() {
		return count;
	}

	public void setCount(BigDecimal count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "ProcessDetailsResponse [count=" + count + ", processEventsList=" + processEventsList + ", responseMessage=" + responseMessage + "]";
	}

}
