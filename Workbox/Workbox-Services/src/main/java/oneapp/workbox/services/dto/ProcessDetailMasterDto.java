package oneapp.workbox.services.dto;

import com.fasterxml.jackson.databind.node.ArrayNode;

public class ProcessDetailMasterDto {

	public ProcessDetailMasterDto() {
		super();
	}

	public ProcessDetailMasterDto(ArrayNode processDetailData, String header, String status) {
		super();
		this.processDetailData = processDetailData;
		this.header = header;
		this.status = status;
	}

	private ArrayNode processDetailData;
	private String header;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayNode getProcessDetailData() {
		return processDetailData;
	}

	public void setProcessDetailData(ArrayNode processDetailData) {
		this.processDetailData = processDetailData;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	@Override
	public String toString() {
		return "ProcessDetailMasterDto [processDetailData=" + processDetailData + ", header=" + header + "]";
	}

}
