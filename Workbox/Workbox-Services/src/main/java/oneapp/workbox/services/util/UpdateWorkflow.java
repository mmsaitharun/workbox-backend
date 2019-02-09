package oneapp.workbox.services.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oneapp.workbox.services.dao.GroupsMappingDao;
import oneapp.workbox.services.dao.SubstitutionRuleDao;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.RestResponse;

@Service("UpdateWorkflow")
public class UpdateWorkflow {

	@Autowired
	SubstitutionRuleDao substitutionDao;

	@Autowired
	private ExternalService externalService;

	@Autowired
	GroupsMappingDao groupsMapping;

	@Autowired
	OAuth oAuth;

	@SuppressWarnings("unused")
	public List<String> getRecipientUser(String taskId, String type, JSONObject resource) {
		List<String> resultList = new ArrayList<String>();
		JSONObject resources = resource;
		try {
			if (type.equals(PMCConstant.FORWARD_TASK)) {
				String requestUrl = PMCConstant.REQUEST_URL_INST + "task-instances/" + taskId;
				Object responseObject = RestUtil
						.callRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TI, null, "GET", "application/json",
								false, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null, null, null)
						.getResponseObject();
				JSONObject jsonObject = ServicesUtil.isEmpty(responseObject) ? null : (JSONObject) responseObject;
				resources = (JSONObject) jsonObject;
			}
			JSONArray jsonArray = resource.getJSONArray("recipientUsers");
			for (int i = 0; i < jsonArray.length(); i++)
				resultList.add(jsonArray.optString(i));
			String currentProcessor = resource.optString("processor");

			if (!ServicesUtil.isEmpty(currentProcessor))
				resultList.add(currentProcessor);
		} catch (Exception e) {
			System.err.println("[PMC][SubstitutionRuleFacade][getRecipientUser][error]" + e.getMessage());
		}
		return resultList;

	}

	public List<String> getRecipientUserOfGroup(String groupName) {
		return groupsMapping.getUsersUnderGroup(groupName);
	}

	public String getPayloadForSubstitution(List<String> recipientUser) {
		String listToString = "";
		if (!ServicesUtil.isEmpty(recipientUser) && recipientUser.size() > 0) {
			listToString = "{ \"recipientUsers\":";
			listToString += "\"";
			for (String str : recipientUser) {
				listToString += str + ",";
			}
			listToString = listToString.substring(0, listToString.length() - 1);
			listToString += "\"";
			listToString += ",\"processor\":\"\"}";
		} else if (ServicesUtil.isEmpty(recipientUser)) // for RU of group
			listToString = "{ \"recipientUsers\":\"\"}";
		return listToString;
	}

	public String updateRecipientUserInWorkflow(String taskId, String payload, String type) {
		try {
			String requestUrl = PMCConstant.REQUEST_URL_INST + "task-instances/" + taskId;

			RestResponse restResponse = RestUtil.callRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TI, payload,
					"PATCH", "application/json", false, "Fetch", PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS,
					null, null, null);
			if (!ServicesUtil.isEmpty(restResponse) && (restResponse.getResponseCode() >= 200)
					&& (restResponse.getResponseCode() <= 207)) {
				return PMCConstant.SUCCESS;
			}
		} catch (Exception e) {
			System.err.println("[PMC][SubstitutionRuleFacade][updateRecipientUserInWorkflow][error]" + e.getMessage());
			e.printStackTrace();
		}
		return PMCConstant.SUCCESS;

	}

	public ResponseMessage adminClaim(List<String> instanceList, String processor) {
		ResponseMessage responseMessage = null;
		for (String instance : instanceList) {
			String payload = "{\"processor\":\"" + processor + "\"}";
			responseMessage = externalService.updateTaskDefinition(instance, payload, null, null,
					PMCConstant.ACTION_TYPE_CLAIM);
		}
		return responseMessage;
	}

	public ResponseMessage adminRelease(List<String> instanceList) {
		ResponseMessage responseMessage = null;
		for (String instance : instanceList) {
			String payload = "{\"processor\":\"\"}";
			responseMessage = externalService.updateTaskDefinition(instance, payload, null, null,
					PMCConstant.ACTION_TYPE_RELEASE);
		}
		return responseMessage;
	}

	public ResponseMessage adminForward(List<String> instanceList, String userId) {
		ResponseMessage responseMessage = null;
		for (String instance : instanceList) {
			String payload = "{\"processor\":\"" + userId + "\"}";
			responseMessage = externalService.updateTaskDefinition(instance, payload, null, null,
					PMCConstant.ACTION_TYPE_FORWARD);
		}
		return responseMessage;
	}
}
