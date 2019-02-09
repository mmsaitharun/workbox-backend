package oneapp.workbox.services.dto;

import java.util.List;

public class ProcessDropdownResponse {

	public ProcessDropdownResponse() {
		super();
	}

	public ProcessDropdownResponse(List<ProcessDropdown> processList, ResponseMessage responseMessage) {
		super();
		this.processList = processList;
		this.responseMessage = responseMessage;
	}

	public class ProcessDropdown {

		public ProcessDropdown() {
			super();
		}

		public ProcessDropdown(String processId, String requestId, String materialId, String processSubject) {
			super();
			this.processId = processId;
			this.requestId = requestId;
			this.materialId = materialId;
			this.processSubject = processSubject;
		}

		private String processId;
		private String requestId;
		private String materialId;
		private String processSubject;

		public String getProcessSubject() {
			return processSubject;
		}

		public void setProcessSubject(String processSubject) {
			this.processSubject = processSubject;
		}

		public String getMaterialId() {
			return materialId;
		}

		public void setMaterialId(String materialId) {
			this.materialId = materialId;
		}

		public String getProcessId() {
			return processId;
		}

		public void setProcessId(String processId) {
			this.processId = processId;
		}

		public String getRequestId() {
			return requestId;
		}

		public void setRequestId(String requestId) {
			this.requestId = requestId;
		}

		@Override
		public String toString() {
			return "ProcessDropdown [processId=" + processId + ", requestId=" + requestId + ", materialId=" + materialId
					+ "]";
		}

	}

	private List<ProcessDropdown> processList;
	private ResponseMessage responseMessage;

	public List<ProcessDropdown> getProcessList() {
		return processList;
	}

	public void setProcessList(List<ProcessDropdown> processList) {
		this.processList = processList;
	}

	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}

	@Override
	public String toString() {
		return "ProcessDropdownResponse [processList=" + processList + ", responseMessage=" + responseMessage + "]";
	}

}
