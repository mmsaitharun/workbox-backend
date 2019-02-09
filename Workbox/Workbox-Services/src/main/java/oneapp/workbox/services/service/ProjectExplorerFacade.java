package oneapp.workbox.services.service;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sap.security.um.user.User;

import oneapp.workbox.services.dao.ProjectProcessDao;
import oneapp.workbox.services.dto.ProcessDetailMasterDto;
import oneapp.workbox.services.dto.ProcessDetailResponse;
import oneapp.workbox.services.dto.ProcessDropdownResponse;
import oneapp.workbox.services.dto.ProcessDropdownResponse.ProcessDropdown;
import oneapp.workbox.services.dto.ProjectDetail;
import oneapp.workbox.services.dto.ProjectDetailsResponse;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.RestResponse;
import oneapp.workbox.services.dto.TaskDetailStoryBoard;
import oneapp.workbox.services.entity.ProcessDetail;
import oneapp.workbox.services.entity.TaskDetail;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.RestUtil;
import oneapp.workbox.services.util.ServicesUtil;
import oneapp.workbox.services.util.UserManagementUtil;

@Service
public class ProjectExplorerFacade implements ProjectExplorerFacadeLocal {
	
	@Autowired
	private ProjectProcessDao projectDao;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public ProjectDetailsResponse getProjectDetails(String projectId, String projectCreatedFrom, String projectCreatedTo,Boolean isRecent) {
		User loggedInUser = UserManagementUtil.getLoggedInUser();
		String userName = "";
		ResponseMessage responseMessage = new ResponseMessage();
		List<ProjectDetail> projectDetails = null;
		if(!ServicesUtil.isEmpty(loggedInUser)) {
			userName = loggedInUser.getName();
		} else {
			userName = "P1099205";
		}
		if(!ServicesUtil.isEmpty(userName)) {
			projectDetails = projectDao.getProjectDetails(projectId, userName, projectCreatedFrom, projectCreatedTo,isRecent);
		}
		if(!ServicesUtil.isEmpty(projectDetails) && projectDetails.size() > 0) {
			responseMessage.setMessage("Projecct details Fetched successfully");
			responseMessage.setStatus(PMCConstant.SUCCESS);
			responseMessage.setStatusCode(PMCConstant.CODE_SUCCESS);
		} else {
			responseMessage.setMessage("No Projects found");
			responseMessage.setStatus(PMCConstant.FAILURE);
			responseMessage.setStatusCode(PMCConstant.CODE_FAILURE);
		}
		return new ProjectDetailsResponse(projectDetails, responseMessage);
	}
	
	@Override
	public ProcessDetailResponse getProcessDetails(String projectId) {
		List<ProcessDetail> processDetails = projectDao.getProcessDetails(projectId);

		// change later
		if(!ServicesUtil.isEmpty(projectId)){
			String ownerSearched = "P1099205";
			if(!"undefined".equals(projectId)) {
				projectDao.saveRecentProject(ownerSearched, projectId);
			}
		}
		
		ResponseMessage responseMessage = new ResponseMessage();
		if(!ServicesUtil.isEmpty(processDetails) && processDetails.size() > 0) {
			responseMessage.setMessage("Process details fetched successfully");
			responseMessage.setStatus(PMCConstant.SUCCESS);
			responseMessage.setStatusCode(PMCConstant.CODE_SUCCESS);
		} else {
			responseMessage.setMessage("No Processes found");
			responseMessage.setStatus(PMCConstant.FAILURE);
			responseMessage.setStatusCode(PMCConstant.CODE_FAILURE);
		}
		return new ProcessDetailResponse(processDetails, responseMessage);
	}
	
	@Override
	public TaskDetailStoryBoard getProcessFlowDetails(String processId) {
		
//		ProcessDetail processDetail = projectDao.getProcessDetail(processId);

		ProcessDetailMasterDto processDetailMasterDto = null;
		ArrayNode arrayNode = null;
		objectMapper = new ObjectMapper();
		
		{
			arrayNode = objectMapper.createArrayNode();
			ObjectNode objectNode = null;
			JSONObject masterContextObject = null;
			JSONObject jsonObject = null;

			RestResponse restResponse = RestUtil.callRestService(
					PMCConstant.REQUEST_URL_INST + "workflow-instances/" + processId + "/context", null, null, PMCConstant.HTTP_METHOD_GET,
					PMCConstant.APPLICATION_JSON, false, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null, null, null);

			if (!ServicesUtil.isEmpty(restResponse) && !ServicesUtil.isEmpty(restResponse.getResponseCode())
					&& restResponse.getResponseCode() == 200) {
				Object object = restResponse.getResponseObject();
				if(!ServicesUtil.isEmpty(object)) {

					masterContextObject = (JSONObject) object;
					
					jsonObject = masterContextObject.optJSONObject("processAttributes");

					if(!ServicesUtil.isEmpty(jsonObject)) {
						Iterator<String> keys = jsonObject.keys();
						while(keys.hasNext()) {
							objectNode = objectMapper.createObjectNode();
							String key = keys.next();
							objectNode.put("id", key);
							objectNode.put("label", StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(key), ' ')));
							objectNode.put("type", "TEXT");
							objectNode.put("value", jsonObject.optString(key));
							
							arrayNode.add(objectNode);
						}
						processDetailMasterDto = new ProcessDetailMasterDto(arrayNode, projectDao.getProcessSubject(processId), projectDao.getProcessStatus(processId));
					}
				}
			}
		}
	
		
		List<TaskDetail> taskDetails = projectDao.getTaskDetails(processId);
		int taskDetailsCount = 0;
		
		ResponseMessage responseMessage = new ResponseMessage();
		
		if(!ServicesUtil.isEmpty(processDetailMasterDto) && !ServicesUtil.isEmpty(taskDetails) && taskDetails.size() > 0) {
			taskDetailsCount = taskDetails.size();
			responseMessage.setMessage("Task story board details fetched successfully");
			responseMessage.setStatus(PMCConstant.SUCCESS);
			responseMessage.setStatusCode(PMCConstant.CODE_SUCCESS);
		} else {
			responseMessage.setMessage("Task story board details fetch failed");
			responseMessage.setStatus(PMCConstant.FAILURE);
			responseMessage.setStatusCode(PMCConstant.CODE_FAILURE);
		}

		return new TaskDetailStoryBoard(processDetailMasterDto, taskDetails, taskDetailsCount, responseMessage);
	}
	
	@Override
	public ProcessDropdownResponse getProcessDropdown(String projectId) {
		ResponseMessage responseMessage = new ResponseMessage();
		List<ProcessDropdown> processDropdown = projectDao.getProcessDropdown(projectId);
		if(!ServicesUtil.isEmpty(processDropdown) && processDropdown.size() > 0) {
			responseMessage.setMessage("Process dropdown details fetched successfully");
			responseMessage.setStatus(PMCConstant.SUCCESS);
			responseMessage.setStatusCode(PMCConstant.CODE_SUCCESS);
		} else {
			responseMessage.setMessage("Process dropdown details fetch failed");
			responseMessage.setStatus(PMCConstant.FAILURE);
			responseMessage.setStatusCode(PMCConstant.CODE_FAILURE);
		}
		return new ProcessDropdownResponse(processDropdown, responseMessage);
	}
	
}
