package oneapp.workbox.services.dto;

import java.util.List;

public class SubstitutionRuleResponseDto {

	private List<SubstitutionRulesDto> dtoList;
	private ResponseMessage message;

	ResponseMessage getMessage() {
		return message;
	}

	public List<SubstitutionRulesDto> getDtoList() {
		return dtoList;
	}

	public void setDtoList(List<SubstitutionRulesDto> dtoList) {
		this.dtoList = dtoList;
	}

	public void setMessage(ResponseMessage message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "SubstitutionRuleResponseDto [dtoList=" + dtoList + ", message=" + message + "]";
	}

}
