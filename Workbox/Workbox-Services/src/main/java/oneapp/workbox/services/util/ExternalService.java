package oneapp.workbox.services.util;

import org.springframework.stereotype.Service;

import oneapp.workbox.services.dto.ResponseMessage;

@Service("ExternalService")
public class ExternalService {

	public ResponseMessage updateTaskDefinition(String taskId, String payload, String token, String tokenType,
			String actionType) {
		ResponseMessage responseMessage = null;
		String message = "";
		if (PMCConstant.ACTION_TYPE_CLAIM.equals(actionType)) {
			message = "Task " + actionType;
		} else if (PMCConstant.ACTION_TYPE_RELEASE.equals(actionType)) {
			message = "Task " + actionType;
		} else if (PMCConstant.ACTION_TYPE_FORWARD.equals(actionType)) {
			message = "Task " + actionType;
		}
		try {
			RestUtil.callRestService(PMCConstant.REQUEST_URL_INST + "/task-instances/" + taskId,
					PMCConstant.SAML_HEADER_KEY_TI, payload, PMCConstant.HTTP_METHOD_PATCH,
					PMCConstant.APPLICATION_JSON, false, "Fetch", null, null, null, token, tokenType);
			if (PMCConstant.ACTION_TYPE_CLAIM.equals(actionType)) {
				message = "Task " + PMCConstant.CLAIM_SUCCESS;
			} else if (PMCConstant.ACTION_TYPE_RELEASE.equals(actionType)) {
				message = "Task " + PMCConstant.RELEASE_SUCCESS;
			} else if (PMCConstant.ACTION_TYPE_FORWARD.equals(actionType)) {
				message = "Task " + PMCConstant.FORWARD_SUCCESS;
			}
			responseMessage = new ResponseMessage(PMCConstant.STATUS_SUCCESS, PMCConstant.CODE_SUCCESS, message);
		} catch (Exception ex) {
			System.err.println("Exception while updating Task : " + ex.getMessage());
			if (PMCConstant.ACTION_TYPE_CLAIM.equals(actionType)) {
				message = "Task " + PMCConstant.CLAIM_FAILURE;
			} else if (PMCConstant.ACTION_TYPE_RELEASE.equals(actionType)) {
				message = "Task " + PMCConstant.RELEASE_FAILURE;
			} else if (PMCConstant.ACTION_TYPE_FORWARD.equals(actionType)) {
				message = "Task " + PMCConstant.FORWARD_FAILURE;
			}
			responseMessage = new ResponseMessage(PMCConstant.STATUS_FAILURE, PMCConstant.CODE_FAILURE, message);
		}
		return responseMessage;
	}
}
