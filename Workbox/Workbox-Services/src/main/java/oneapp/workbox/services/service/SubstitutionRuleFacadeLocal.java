package oneapp.workbox.services.service;

import oneapp.workbox.services.dto.RequestDto;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.SubstitutionRuleResponseDto;
import oneapp.workbox.services.dto.SubstitutionRulesDto;

public interface SubstitutionRuleFacadeLocal {

	public ResponseMessage createSubstitutionRule(SubstitutionRulesDto dto);

	public ResponseMessage updateSubstitutionRule(SubstitutionRulesDto dto);

	public SubstitutionRuleResponseDto getSubstituting();

	public SubstitutionRuleResponseDto getSubstituted();

	public ResponseMessage forwardTask(String taskId, String processor);

	public ResponseMessage deleteSubstitutionRule(SubstitutionRulesDto dto);

	public ResponseMessage bulkForward(RequestDto dto);

}
