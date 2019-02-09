package oneapp.workbox.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import oneapp.workbox.services.dto.RequestDto;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.SubstitutionRuleResponseDto;
import oneapp.workbox.services.dto.SubstitutionRulesDto;
import oneapp.workbox.services.service.SubstitutionRuleFacadeLocal;

@RestController
@ComponentScan("oneapp.incture.workbox")
@RequestMapping(value = "/substitutionRule", produces = "application/json")
public class SubstitutionRuleRest {

	@Autowired
	private SubstitutionRuleFacadeLocal substitutionRuleFacadeLocal;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseMessage createSubstitution(@RequestBody SubstitutionRulesDto dto) {
		return substitutionRuleFacadeLocal.createSubstitutionRule(dto);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseMessage createUpdateDataAdmin(@RequestBody SubstitutionRulesDto dto) {
		return substitutionRuleFacadeLocal.updateSubstitutionRule(dto);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseMessage deleteProcessConfig(@RequestBody SubstitutionRulesDto dto) {
		return substitutionRuleFacadeLocal.deleteSubstitutionRule(dto);
	}

	@RequestMapping(value = "/substituted", method = RequestMethod.GET)
	public SubstitutionRuleResponseDto getUserSubstituted() {
		return substitutionRuleFacadeLocal.getSubstituted();
	}

	@RequestMapping(value = "/substituting", method = RequestMethod.GET)
	public SubstitutionRuleResponseDto getUserSubstituting() {
		return substitutionRuleFacadeLocal.getSubstituting();
	}
//
//	@RequestMapping(value = "/substitution", method = RequestMethod.GET)
//	public String substitution() {
//		return substitutionRuleFacadeLocal.substitution();
//	}

//	@RequestMapping(value = "/updateWorkFlow", method = RequestMethod.GET)
//	public String updateWorkFlow() {
//		return substitutionRuleFacadeLocal.updateTaskList();
//	}
//
//	@RequestMapping(value = "/changeRecipientUser", method = RequestMethod.GET)
//	public String changeRecipientUser() {
//		return substitutionRuleFacadeLocal.updateTaskListInFlow();
//	}
//	
	@RequestMapping(value = "/forwardTask", method = RequestMethod.GET)
	public ResponseMessage forwarding(@RequestParam(value = "taskId") String taskId,
			@RequestParam(value = "recipientUser") String recipientUser) {
		return substitutionRuleFacadeLocal.forwardTask(taskId, recipientUser);
	}

	@RequestMapping(value = "/bulkForwardTask", method = RequestMethod.POST)
	public ResponseMessage forwardedByAdmin(@RequestBody RequestDto dto) {
		return substitutionRuleFacadeLocal.bulkForward(dto);
	}

//	@RequestMapping(value = "/getGroupUser", method = RequestMethod.GET)
//	public List<String> getGroupUser() {
//		return substitutionRuleFacadeLocal.getRecipientUserOfGroup();
//	}
	
//	@RequestMapping(value = "/changeRecipient", method = RequestMethod.GET)
//	public String changeRecipient() {
//		return substitutionRuleFacadeLocal.iterateTaskCollection();
//	}

	
//	@RequestMapping(value = "/releaseTask", method = RequestMethod.GET)
//	public ResponseMessage forwarding(@RequestParam(value = "taskId") String taskId) {
//		return substitutionRuleFacadeLocal.releaseTask(taskId);
//	}

	

}
