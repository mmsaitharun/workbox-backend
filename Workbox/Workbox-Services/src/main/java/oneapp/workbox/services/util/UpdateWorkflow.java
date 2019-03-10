package oneapp.workbox.services.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oneapp.workbox.services.dao.GroupsMappingDao;
import oneapp.workbox.services.dao.SubstitutionRuleDao;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.TokenDetailsDto;

@Service("UpdateWorkflow")
public class UpdateWorkflow {

	@Autowired
	SubstitutionRuleDao substitutionDao;

	@Autowired
	private ExternalService externalService;

	@Autowired
	GroupsMappingDao groupsMapping;

	public ResponseMessage adminClaim(List<String> instanceList, String processor) {
		ResponseMessage responseMessage = null;
		TokenDetailsDto tokenDetails = OAuth.getToken();
		for (String instance : instanceList) {
			String payload = "{\"processor\":\"" + processor + "\"}";
			responseMessage = externalService.updateTaskDefinition(instance, payload, tokenDetails.getToken(), tokenDetails.getTokenType(),
					PMCConstant.ACTION_TYPE_CLAIM);
		}
		return responseMessage;
	}

	public ResponseMessage adminRelease(List<String> instanceList) {
		ResponseMessage responseMessage = null;
		TokenDetailsDto tokenDetails = OAuth.getToken();
		for (String instance : instanceList) {
			String payload = "{\"processor\":\"\"}";
			responseMessage = externalService.updateTaskDefinition(instance, payload, tokenDetails.getToken(), tokenDetails.getTokenType(),
					PMCConstant.ACTION_TYPE_RELEASE);
		}
		return responseMessage;
	}

	public ResponseMessage adminForward(List<String> instanceList, String userId) {
		ResponseMessage responseMessage = null;
		TokenDetailsDto tokenDetails = OAuth.getToken();
		for (String instance : instanceList) {
			String payload = "{\"processor\":\"" + userId + "\"}";
			responseMessage = externalService.updateTaskDefinition(instance, payload, tokenDetails.getToken(), tokenDetails.getTokenType(),
					PMCConstant.ACTION_TYPE_FORWARD);
		}
		return responseMessage;
	}
}
