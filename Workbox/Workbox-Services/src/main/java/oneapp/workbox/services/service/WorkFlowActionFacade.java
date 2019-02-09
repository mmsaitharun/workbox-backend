package oneapp.workbox.services.service;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oneapp.workbox.services.dao.InboxActionsDao;
import oneapp.workbox.services.dao.TaskEventsDao;
import oneapp.workbox.services.dao.TaskOwnersDao;
import oneapp.workbox.services.dto.ActionDto;
import oneapp.workbox.services.dto.ActionRequestContextDto;
import oneapp.workbox.services.dto.ActionRequestDto;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.RestResponse;
import oneapp.workbox.services.entity.InboxActions;
import oneapp.workbox.services.entity.TaskEventsDo;
import oneapp.workbox.services.entity.TaskOwnersDo;
import oneapp.workbox.services.entity.TaskOwnersDoPK;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.RestUtil;
import oneapp.workbox.services.util.ServicesUtil;
import oneapp.workbox.services.util.UpdateWorkflow;

@Service("WorkFlowActionFacade")
public class WorkFlowActionFacade implements WorkFlowActionFacadeLocal {

	@Autowired
	UpdateWorkflow updateWorkflow;
	
	@Autowired
	TaskEventsDao taskEvents;
	
	@Autowired
	TaskOwnersDao taskOwners;
	
	@Autowired
	InboxActionsDao inboxActions;
	
	@Override
	public ResponseMessage taskAction(ActionDto dto) {
		if(!ServicesUtil.isEmpty(dto)) {
			if(ServicesUtil.isEmpty(dto.getOrigin())) {
				dto.setOrigin("SCP");
			}
			switch(dto.getOrigin()) {
			case "SCP" :
				if(!ServicesUtil.isEmpty(dto.isAdmin()) && !dto.isAdmin()) {
					if (dto.getComment() == null)
						dto.setComment("");
					if (dto.getActionType() == null)
						return new ResponseMessage("failed", "", "Action Type is Null");
					if (dto.getActionType().equalsIgnoreCase("Approve")) {
						return approveTask(dto.getInstanceList(), dto.getComment());
					} else if (dto.getActionType().equalsIgnoreCase("Reject")) {
						return rejectTask(dto.getInstanceList(), dto.getComment());
					} else if (dto.getActionType().equalsIgnoreCase("Claim")) {
						return claimTask(dto.getInstanceList(), dto.getUserId());
					} else if (dto.getActionType().equalsIgnoreCase("Release")) {
						return relaseTask(dto.getInstanceList(), dto.getUserId());
					} else if (dto.getActionType().equalsIgnoreCase("AssignProcessor")) {
						return assignProcessor(dto.getInstanceList(), dto.getProcessor());
					} else {
						return new ResponseMessage("failed", "", "Action Type is Not valid");
					}
				} else if(!ServicesUtil.isEmpty(dto.isAdmin()) && dto.isAdmin()) {
					return doAdminAction(dto);
				}
				break;
			case "ECC" : 
				break;
			default : 
				break;
			}
		}
		return null;
	}

	private ResponseMessage doAdminAction(ActionDto dto) {
		switch(dto.getActionType()) {
			case "Claim" :
				return adminClaim(dto.getInstanceList(), dto.getUserId());
			case "Release" : 
				return adminRelease(dto.getInstanceList());
			case "Forward" :
				return adminForward(dto.getInstanceList(), dto.getUserId());
		}
		return null;
	}

	private ResponseMessage adminForward(List<String> instanceList, String userId) {
		return updateWorkflow.adminForward(instanceList, userId);
	}

	private ResponseMessage adminRelease(List<String> instanceList) {
		return updateWorkflow.adminRelease(instanceList);
	}

	private ResponseMessage adminClaim(List<String> instanceList, String processor) {
		return updateWorkflow.adminClaim(instanceList, processor);
	}

	@SuppressWarnings("unused")
	private ResponseMessage assignProcessor(List<String> instanceList, String processor) {
		if (ServicesUtil.isEmpty(processor))
			return new ResponseMessage("failed", "", "Please Specify the Valid processor");
		ResponseMessage responseMessage = new ResponseMessage();

		try {

		} catch (Exception e) {

		}

		return null;
	}

	private ResponseMessage relaseTask(List<String> instanceList, String userId) {
		ResponseMessage response = new ResponseMessage();
		try {
			for (String instanceId : instanceList) {
				String releaseTask = PMCConstant.REQUEST_BASE_URL_TC + "/Release?SAP__Origin=%27NA%27&InstanceID='" + instanceId
						+ "'";
				// String token = getScrfToken(RELEASE_TASK);
				RestResponse restResponse = RestUtil.callRestService(releaseTask, PMCConstant.SAML_HEADER_KEY_TC, null, "POST", "application/json", false,
						"Fetch", PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null, null, null);
				if(!ServicesUtil.isEmpty(restResponse) && !ServicesUtil.isEmpty(restResponse.getResponseCode())) {
					if(restResponse.getResponseCode() == 201) {
//						releaseInstance(instanceId, userId);
						inboxActions.saveOrUpdateAction(new InboxActions(instanceId, new Date(), null, true, userId));
					}
				}
			}
			response.setStatus("Success");
			response.setMessage("Task successfully Released");
		} catch (Exception e) {
			response.setStatus("Fail");
			response.setMessage("Fail to release the task");
		}
		return response;
	}

	private ResponseMessage claimTask(List<String> instanceList, String userId) {
		ResponseMessage response = new ResponseMessage();
		try {
			// System.err.println("token for claim request: "+token);
			for (String instanceId : instanceList) {
				String claimTask = PMCConstant.REQUEST_BASE_URL_TC + "/Claim?SAP__Origin=%27NA%27&InstanceID='" + instanceId
						+ "'";
				// String token = getScrfToken(RELEASE_TASK);
				RestResponse restResponse = RestUtil.callRestService(claimTask, PMCConstant.SAML_HEADER_KEY_TC, null, "POST", "application/json", false,
						"Fetch", PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null, null, null);
				if(!ServicesUtil.isEmpty(restResponse) && !ServicesUtil.isEmpty(restResponse.getResponseCode())) {
					if(restResponse.getResponseCode() == 201) {
//						claimInstance(instanceId, userId);
						inboxActions.saveOrUpdateAction(new InboxActions(instanceId, new Date(), true, null, userId));
					}
				}
			}
			response.setStatus("Success");
			response.setMessage("Task Successfully Claimed");
		} catch (Exception e) {
			response.setStatus("Fail");
			response.setMessage("Failed to Claim the task" + e.toString());
			System.err.println("claim failed:" + e.getStackTrace());
		}
		return response;
	}

	@SuppressWarnings("unused")
	private void claimInstance(String instanceId, String userId) {
		int updateTE;
		int updateTW;
//		String updateTEQuery = "UPDATE TASK_EVENTS SET STATUS = 'RESERVED' WHERE EVENT_ID = '"+instanceId+"'";
//		String updateTWQuery = "UPDATE TASK_OWNERS SET IS_PROCESSED = '1' WHERE EVENT_ID = '"+instanceId+"' AND TASK_OWNER = '"+userId+"'";
		try {
//			updateTE = taskEvents.executeUpdateQuery(updateTEQuery);
//			updateTW = taskOwners.executeUpdateQuery(updateTWQuery);
			
			updateTE = taskEvents.changeTaskStatus(new TaskEventsDo(instanceId, "RESERVED", userId));
			updateTW = taskOwners.changeOwnerStatus(new TaskOwnersDo(new TaskOwnersDoPK(instanceId, userId), true));
			
			System.err.println("[WorkflowAction]Updating Claim in DB Success : "+updateTE + ", " + updateTW);
			
		} catch (Exception ex) {
			System.err.println("[WorkflowAction]Updating Claim in DB Failed : "+ex.getMessage());
		}
	}
	
	@SuppressWarnings("unused")
	private void releaseInstance(String instanceId, String userId) {
		int updateTE;
		int updateTW;
//		String updateTEQuery = "UPDATE TASK_EVENTS SET STATUS = 'READY' WHERE EVENT_ID = '"+instanceId+"'";
//		String updateTWQuery = "UPDATE TASK_OWNERS SET IS_PROCESSED = '0' WHERE EVENT_ID = '"+instanceId+"' AND TASK_OWNER = '"+userId+"'";
		try {
//			updateTE = taskEvents.executeUpdateQuery(updateTEQuery);
//			updateTW = taskOwners.executeUpdateQuery(updateTWQuery);

			updateTE = taskEvents.changeTaskStatus(new TaskEventsDo(instanceId, "READY", null));
			updateTW = taskOwners.changeOwnerStatus(new TaskOwnersDo(new TaskOwnersDoPK(instanceId, userId), false));
			
			System.err.println("[WorkflowAction]Updating Release in DB Success : "+updateTE + ", " + updateTW);
		} catch (Exception ex) {
			System.err.println("[WorkflowAction]Updating Claim in DB Failed : "+ex.getMessage());
		}
	}

	@SuppressWarnings("unused")
	private ResponseMessage rejectTask(List<String> instanceList, String comment) {

		String REJECT_TASK = PMCConstant.REQUEST_URL_INST + "task-instances/";
		ResponseMessage response = new ResponseMessage();
		try {
			for (String instanceId : instanceList) {
				String url = REJECT_TASK + instanceId;
				// String token = getScrfToken(url);
				ActionRequestDto dto = new ActionRequestDto("COMPLETED", new ActionRequestContextDto("false", comment));
				JSONObject entity = new JSONObject(dto);
				Object responseObject = RestUtil.callRestService(url, PMCConstant.SAML_HEADER_KEY_TI, entity.toString(), "PATCH", "application/json", false,
						"Fetch", PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null, null, null).getResponseObject();
				JSONObject obj = ServicesUtil.isEmpty(responseObject) ? null : (JSONObject) responseObject;
			}
			response.setStatus("Success");
			response.setMessage("Tasks Rejected");
		} catch (Exception e) {
			response.setStatus("fail");
			response.setMessage("failed to Reject the task");
		}
		return response;
	}

	@SuppressWarnings("unused")
	private ResponseMessage approveTask(List<String> instanceList, String comment) {
		String APPROVE_TASK = PMCConstant.REQUEST_URL_INST + "task-instances/";
		ResponseMessage response = new ResponseMessage();
		String token = "";
		try {
			for (String instanceId : instanceList) {
				String url = APPROVE_TASK + instanceId;
				// token = getScrfToken(url);
				ActionRequestDto dto = new ActionRequestDto("COMPLETED", new ActionRequestContextDto("true", comment));
				JSONObject entity = new JSONObject(dto);
				Object responseObject = RestUtil.callRestService(url, PMCConstant.SAML_HEADER_KEY_TI, entity.toString(), "PATCH", "application/json", false,
						"Fetch", PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null, null, null).getResponseObject();
				JSONObject obj = ServicesUtil.isEmpty(responseObject) ? null : (JSONObject)responseObject;
			}
			response.setStatus(PMCConstant.SUCCESS);
			response.setStatusCode(PMCConstant.CODE_SUCCESS);
			response.setMessage("Tasks Approved");
		} catch (Exception e) {
			response.setStatus(PMCConstant.FAILURE);
			response.setStatusCode(PMCConstant.CODE_FAILURE);
			response.setMessage("Failed to update the task:" + e.toString() + "token:" + token);
		}

		return response;
	}

	/*@SuppressWarnings("unused")
	private String getScrfToken(String host) {
		String token = "";
		// , GET_TOKEN = "";
		// if (host.equalsIgnoreCase("workflow"))
		// GET_TOKEN = BASE_URL + "/v1/xsrf-token";
		// else
		// GET_TOKEN = FIORI_BASE_URL;
		Object responseObject = SCPRestUtil.callRestService(host, null, "GET", "application/json", true, "Fetch", null, null, null,
				null);
		JSONObject tokenObj = ServicesUtil.isEmpty(responseObject) ? null : (JSONObject) responseObject;
		if (ServicesUtil.isEmpty(tokenObj))
			return "Error";
		else
			return tokenObj.optString("x-csrf-token", "ss");
	}*/

}
