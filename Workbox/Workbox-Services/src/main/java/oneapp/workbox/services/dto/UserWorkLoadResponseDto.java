package oneapp.workbox.services.dto;

import java.util.List;

public class UserWorkLoadResponseDto {

	private List<UserWorkloadDto> userWorkloadDtos;
	private ResponseMessage message;

	public List<UserWorkloadDto> getUserWorkloadDtos() {
		return userWorkloadDtos;
	}

	public void setUserWorkloadDtos(List<UserWorkloadDto> userWorkloadDtos) {
		this.userWorkloadDtos = userWorkloadDtos;
	}

	public ResponseMessage getMessage() {
		return message;
	}

	public void setMessage(ResponseMessage message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "UserWorkLoadResponseDto [userWorkloadDtos=" + userWorkloadDtos + ", message=" + message + "]";
	}
}
