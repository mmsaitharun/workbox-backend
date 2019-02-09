package oneapp.workbox.services.dto;

import java.util.List;

public class StringListResponseDto {

	private List<String> str;
	private ResponseMessage message;

	public List<String> getStr() {
		return str;
	}

	public void setStr(List<String> str) {
		this.str = str;
	}

	public ResponseMessage getMessage() {
		return message;
	}

	public void setMessage(ResponseMessage message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ListStringResponseDto [str=" + str + ", message=" + message + "]";
	}

}
