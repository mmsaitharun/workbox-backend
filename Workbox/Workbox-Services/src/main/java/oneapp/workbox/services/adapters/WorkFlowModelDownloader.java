package oneapp.workbox.services.adapters;

import org.json.JSONObject;

import oneapp.workbox.services.dto.RestResponse;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.RestUtil;
import oneapp.workbox.services.util.ServicesUtil;

public class WorkFlowModelDownloader {

	public static JSONObject getWorkflowModel(String workflowDefinitionId) {
		String url = PMCConstant.REQUEST_URL_INST + "workflow-definitions/" + workflowDefinitionId + "/model";
		RestResponse restResponse = RestUtil.callRestService(url, null, null, PMCConstant.HTTP_METHOD_GET,
				PMCConstant.APPLICATION_JSON, false, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null,
				null, null);
		if (!ServicesUtil.isEmpty(restResponse) && restResponse.getResponseCode() == 200
				&& restResponse.getResponseObject() instanceof org.json.JSONObject) {
			return (JSONObject) restResponse.getResponseObject();
		}
		return null;
	}
}
