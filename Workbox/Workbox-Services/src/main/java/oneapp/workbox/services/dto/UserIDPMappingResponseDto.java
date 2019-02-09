package oneapp.workbox.services.dto;

import java.util.List;

public class UserIDPMappingResponseDto {

	private List<UserIDPMappingDto> dto;
	private ResponseMessage message;

	public List<UserIDPMappingDto> getDto() {
		return dto;
	}

	public void setDto(List<UserIDPMappingDto> dto) {
		this.dto = dto;
	}

	public ResponseMessage getMessage() {
		return message;
	}

	public void setMessage(ResponseMessage message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "UserIDPMappingResponseDto [dto=" + dto + ", message=" + message + "]";
	}

}
