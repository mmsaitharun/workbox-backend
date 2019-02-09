package oneapp.workbox.services.dto;

import java.util.List;

public class SearchListResponseDto {

	private List<SearchListDto> procList;
	private List<SearchListDto> statusList;
	private ResponseMessage message;

	public List<SearchListDto> getProcList() {
		return procList;
	}

	public void setProcList(List<SearchListDto> procList) {
		this.procList = procList;
	}

	public List<SearchListDto> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<SearchListDto> statusList) {
		this.statusList = statusList;
	}

	public ResponseMessage getMessage() {
		return message;
	}

	public void setMessage(ResponseMessage message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "SearchListResponseDto [procList=" + procList + ", statusList=" + statusList + ", message=" + message
				+ "]";
	}

}
