package oneapp.workbox.services.dto;

import java.util.List;

public class RunningSearchResponseDto {

	List<SingleValueDto> values;
	ResponseMessage message;

	public List<SingleValueDto> getValues() {
		return values;
	}

	public void setValues(List<SingleValueDto> values) {
		this.values = values;
	}

	public ResponseMessage getMessage() {
		return message;
	}

	public void setMessage(ResponseMessage message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "RunningSearchResponseDto [values=" + values + ", message=" + message + "]";
	}

}
