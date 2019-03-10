package oneapp.workbox.services.adapters;

import org.json.JSONObject;

import oneapp.workbox.services.dto.RestResponse;
import oneapp.workbox.services.dto.TokenDetailsDto;
import oneapp.workbox.services.util.OAuth;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.RestUtil;
import oneapp.workbox.services.util.ServicesUtil;

public class WorkFlowModelDownloader {
	
	public static JSONObject getWorkflowModel(String workflowDefinitionId) {
		String url = PMCConstant.REQUEST_URL_INST + "workflow-definitions/" + workflowDefinitionId + "/model";
		TokenDetailsDto token = OAuth.getToken();
		RestResponse restResponse = RestUtil.callRestService(url, null, null, PMCConstant.HTTP_METHOD_GET,
				PMCConstant.APPLICATION_JSON, false, null, null, null, null,
				token.getToken(), token.getTokenType());
		if (!ServicesUtil.isEmpty(restResponse) && restResponse.getResponseCode() == 200
				&& restResponse.getResponseObject() instanceof org.json.JSONObject) {
			return (JSONObject) restResponse.getResponseObject();
		}
		return null;
	}
}
