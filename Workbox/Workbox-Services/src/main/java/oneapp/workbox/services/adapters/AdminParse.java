package oneapp.workbox.services.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import oneapp.workbox.services.dao.CustomAttributeDao;
import oneapp.workbox.services.dao.GroupsMappingDao;
import oneapp.workbox.services.dao.ProcessConfigDao;
import oneapp.workbox.services.dao.UserDetailsDao;
import oneapp.workbox.services.dto.ProcessConfigDto;
import oneapp.workbox.services.dto.ProcessEventsDto;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.RestResponse;
import oneapp.workbox.services.dto.TaskEventsDto;
import oneapp.workbox.services.dto.TaskOwnersDto;
import oneapp.workbox.services.dto.UserDetailsDto;
import oneapp.workbox.services.dto.WorkBoxDto;
import oneapp.workbox.services.entity.CustomAttributeTemplate;
import oneapp.workbox.services.entity.CustomAttributeValue;
import oneapp.workbox.services.entity.ProcessDetail;
import oneapp.workbox.services.entity.ProjectProcessMapping;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.RestUtil;
import oneapp.workbox.services.util.ServicesUtil;

@Component
public class AdminParse {
	
	@Autowired
	UserDetailsDao userDetails;
	
	@Autowired
	CustomAttributeDao customDao;
	
	@Autowired
	ProcessConfigDao processConfigDao;
	
	@Autowired
	GroupsMappingDao groupsMapping;
	
	private int callCount = 0;

	public class AdminParseResponse {
		
		public AdminParseResponse(List<TaskEventsDto> tasks, List<ProcessEventsDto> processes,
				List<TaskOwnersDto> owners, List<WorkBoxDto> workbox, int resultCount, 
				List<ProjectProcessMapping> prjPrcMaps, List<ProcessDetail> processDetails) {
			super();
			this.tasks = tasks;
			this.processes = processes;
			this.owners = owners;
			this.workbox = workbox;
			this.resultCount = resultCount;
			this.prjPrcMaps = prjPrcMaps;
			this.processDetails = processDetails;
		}
		
		private List<TaskEventsDto> tasks;
		private List<ProcessEventsDto> processes;
		private List<TaskOwnersDto> owners;
		private List<WorkBoxDto> workbox;
		private int resultCount;
		
		// Added for Colgate
		List<ProjectProcessMapping> prjPrcMaps;
		List<ProcessDetail> processDetails;
		
		public List<ProcessDetail> getProcessDetails() {
			return processDetails;
		}

		public void setProcessDetails(List<ProcessDetail> processDetails) {
			this.processDetails = processDetails;
		}

		public List<ProjectProcessMapping> getPrjPrcMaps() {
			return prjPrcMaps;
		}

		public void setPrjPrcMaps(List<ProjectProcessMapping> prjPrcMaps) {
			this.prjPrcMaps = prjPrcMaps;
		}

		public List<TaskEventsDto> getTasks() {
			return tasks;
		}

		public void setTasks(List<TaskEventsDto> tasks) {
			this.tasks = tasks;
		}

		public List<ProcessEventsDto> getProcesses() {
			return processes;
		}

		public void setProcesses(List<ProcessEventsDto> processes) {
			this.processes = processes;
		}

		public List<TaskOwnersDto> getOwners() {
			return owners;
		}

		public void setOwners(List<TaskOwnersDto> owners) {
			this.owners = owners;
		}
		
		public List<WorkBoxDto> getWorkbox() {
			return workbox;
		}

		public void setWorkbox(List<WorkBoxDto> workbox) {
			this.workbox = workbox;
		}

		public int getResultCount() {
			return resultCount;
		}

		public void setResultCount(int resultCount) {
			this.resultCount = resultCount;
		}

		@Override
		public String toString() {
			return "AdminParseResponse [tasks=" + tasks + ", processes=" + processes + ", owners=" + owners
					+ ", workbox=" + workbox + ", resultCount=" + resultCount + "]";
		}

	}

	public AdminParseResponse parseDetail() {
		System.err.println("Start : " + System.currentTimeMillis());

		List<TaskEventsDto> tasks = null;
		List<ProcessEventsDto> processes = null;
		List<TaskOwnersDto> owners = null;
		List<ProjectProcessMapping> prjPrcMaps = null;
		List<ProcessDetail> processDetails = null;

		JSONObject taskObject = null;
		JSONObject processObject = null;

		TaskEventsDto task = null;
		ProcessEventsDto process = null;
		TaskOwnersDto owner = null;
		ProjectProcessMapping prjPrcMap = null;
		ProcessDetail processDetail = null;

		JSONArray taskList = fetchInstances(
				PMCConstant.REQUEST_URL_INST + "task-instances?status=READY&status=RESERVED");
		JSONArray processList = fetchInstances(PMCConstant.REQUEST_URL_INST + "workflow-instances?status=RUNNING&status=ERRONEOUS&status=COMPLETED");

		if (!ServicesUtil.isEmpty(taskList) && !ServicesUtil.isEmpty(processList) && taskList.length() > 0
				&& processList.length() > 0) {
			tasks = new ArrayList<TaskEventsDto>();
			processes = new ArrayList<ProcessEventsDto>();
			owners = new ArrayList<TaskOwnersDto>();
			prjPrcMaps = new ArrayList<ProjectProcessMapping>();
			processDetails = new ArrayList<ProcessDetail>();

			for (Object object : processList) {
				processObject = (JSONObject) object;
				process = new ProcessEventsDto();
				prjPrcMap = new ProjectProcessMapping();
//				processDetail = new ProcessDetail();
				
				process.setProcessId(processObject.optString("id"));
				
				process.setName(processObject.optString("definitionId"));
				process.setSubject(processObject.optString("subject"));
				process.setStatus(processObject.optString("status"));

				processDetail = fetchProcessContextDetail(process.getProcessId());
				
				process.setRequestId(processObject.optString("businessKey"));
				process.setStartedAt(ServicesUtil.isEmpty(processObject.optString("startedAt")) ? null
						: ServicesUtil.convertAdminFromStringToDate(processObject.optString("startedAt")));
				process.setCompletedAt(ServicesUtil.isEmpty(processObject.optString("completedAt")) ? null
						: ServicesUtil.convertAdminFromStringToDate(processObject.optString("completedAt")));
				process.setStartedBy(processObject.optString("startedBy"));
				UserDetailsDto uDetails = userDetails.getUserDetails(new UserDetailsDto(process.getStartedBy(), null));
				if(!ServicesUtil.isEmpty(uDetails)) {
					process.setStartedByDisplayName(uDetails.getDisplayName());
				}
				
				if(!ServicesUtil.isEmpty(processDetail)) {
					prjPrcMap.setProcessId(process.getProcessId());
					prjPrcMap.setProjectId(processDetail.getProjectId());
					
					processDetail.setProcessId(process.getProcessId());
				}

				if(!ServicesUtil.isEmpty(prjPrcMap.getProcessId()) && !ServicesUtil.isEmpty(prjPrcMap.getProjectId())){
					prjPrcMaps.add(prjPrcMap);
				}
				
				if(!ServicesUtil.isEmpty(processDetail)) {
					processDetails.add(processDetail);
				}
				processes.add(process);
			}

			for (Object object : taskList) {
				taskObject = (JSONObject) object;

				task = new TaskEventsDto();

				task.setEventId(taskObject.optString("id"));
				task.setProcessId(taskObject.optString("workflowInstanceId"));
				
				task.setProcessName(taskObject.optString("workflowDefinitionId"));
				
				task.setCreatedAt(ServicesUtil.convertAdminFromStringToDate(taskObject.optString("createdAt")));
				task.setDescription(taskObject.optString("description"));
				task.setCurrentProcessor(taskObject.optString("processor"));
				
				if(!ServicesUtil.isEmpty(task.getCurrentProcessor())) {
					UserDetailsDto processorDetails = userDetails.getUserDetails(new UserDetailsDto(task.getCurrentProcessor(), null));
					if(!ServicesUtil.isEmpty(processorDetails)) {
						task.setCurrentProcessorDisplayName(processorDetails.getDisplayName());
					}
				}
				
				task.setSubject(taskObject.optString("subject"));
				task.setStatus(taskObject.optString("status"));
				task.setName(taskObject.optString("definitionId"));
				task.setPriority(taskObject.optString("priority"));
				task.setCompletedAt(ServicesUtil.isEmpty(taskObject.optString("completedAt")) ? null
						: ServicesUtil.convertFromStringToDate(taskObject.optString("completedAt")));

				if(ServicesUtil.isEmpty(taskObject.optString("dueDate"))){
					task.setCompletionDeadLine(new Date(task.getCreatedAt().getTime() + (1000 * 60 * 60 * 24 * 5)));
					task.setSlaDueDate(new Date(task.getCreatedAt().getTime() + (1000 * 60 * 60 * 24 * 5)));
				} else {
					task.setCompletionDeadLine(ServicesUtil.convertAdminFromStringToDate(taskObject.optString("dueDate")));
					task.setSlaDueDate(ServicesUtil.convertAdminFromStringToDate(taskObject.optString("dueDate")));
				}
				
				task.setOrigin("SCP");
				tasks.add(task);

				if (!ServicesUtil.isEmpty(task.getCurrentProcessor())) {
					owner = new TaskOwnersDto();
					owner.setEventId(task.getEventId());
					owner.setTaskOwner(task.getCurrentProcessor());
					owner.setIsProcessed(true);
					UserDetailsDto ownerDetails = userDetails.getUserDetails(new UserDetailsDto(owner.getTaskOwner(), null));
					if(!ServicesUtil.isEmpty(ownerDetails)) {
						owner.setOwnerEmail(ownerDetails.getEmailId());
						owner.setTaskOwnerDisplayName(ownerDetails.getDisplayName());
					}
					owners.add(owner);
				}

				JSONArray userArray = taskObject.optJSONArray("recipientUsers");
				List<String> recepients = new ArrayList<String>();
				if (!ServicesUtil.isEmpty(userArray) && userArray.length() > 0) {
					for (Object user : userArray) {
						recepients.add((String) user);
					}
				}
				JSONArray groupArray = taskObject.optJSONArray("recipientGroups");
				for (Object group : groupArray) {
					recepients.addAll(getRecipientUserOfGroup((String) group));
				}

				for (String recepient : recepients) {
					owner = new TaskOwnersDto();
					owner.setEventId(task.getEventId());
					owner.setTaskOwner(recepient);
					owner.setIsProcessed(false);
					UserDetailsDto ownerDetails = userDetails.getUserDetails(new UserDetailsDto(owner.getTaskOwner(), null));
					if(!ServicesUtil.isEmpty(ownerDetails)) {
						owner.setOwnerEmail(ownerDetails.getEmailId());
						owner.setTaskOwnerDisplayName(ownerDetails.getDisplayName());
					}
					owners.add(owner);
				}

			}

		}
		System.err.println("End : " + System.currentTimeMillis());
		return new AdminParseResponse(tasks, processes, owners, null, 0, prjPrcMaps, processDetails);
	}

	public AdminParseResponse parseAdminTasks(String relativeQueryParams) {

		System.err.println("Start : " + System.currentTimeMillis());
		List<WorkBoxDto> workboxes = null;

		JSONObject taskObject = null;
		
		WorkBoxDto workbox = null;
		Object[] objects = fetchTaskInstances(
				PMCConstant.REQUEST_URL_INST + "task-instances?"+relativeQueryParams);
		JSONArray taskList = (JSONArray) objects[0];
		int resultCount = Integer.valueOf((String) objects[1]);

		if (!ServicesUtil.isEmpty(taskList) && taskList.length() > 0) {
			workboxes = new ArrayList<WorkBoxDto>();
			
			for (Object object : taskList) {
				taskObject = (JSONObject) object;

				workbox = new WorkBoxDto();

				SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yy hh:mm:ss a");
				
				workbox.setTaskId(taskObject.optString("id"));
				workbox.setProcessId(taskObject.optString("workflowInstanceId"));
				workbox.setProcessName(taskObject.optString("workflowDefinitionId"));
				workbox.setCreatedAt(simpleDateFormat1.format(ServicesUtil.convertFromStringToDate(taskObject.optString("createdAt"))));
				workbox.setTaskDescription(taskObject.optString("description"));
				workbox.setStatus(taskObject.optString("status"));
				workbox.setSubject(taskObject.optString("subject"));
				workbox.setStartedBy(taskObject.optString("createdBy"));
				workbox.setSlaDisplayDate(
						ServicesUtil.isEmpty(taskObject.optString("dueDate")) ? null : simpleDateFormat1.format(ServicesUtil.resultAsDate(taskObject.optString("dueDate"))));
				if (!ServicesUtil.isEmpty(taskObject.optString("dueDate")) && !ServicesUtil.isEmpty(taskObject.optString("createdAt"))) {
					Calendar created = ServicesUtil.timeStampToCalAdmin(taskObject.optString("createdAt"));
					Calendar slaDate = ServicesUtil.timeStampToCalAdmin(taskObject.optString("dueDate"));
					String timeLeftString = ServicesUtil.getSLATimeLeft(slaDate);
					if (timeLeftString.equalsIgnoreCase("Breach")) {
						workbox.setBreached(true);
					} else {
						workbox.setBreached(false);
						workbox.setTimeLeftDisplayString(timeLeftString);
						workbox.setTimePercentCompleted(ServicesUtil.getPercntTimeCompleted(created, slaDate));
					}
				}
				
				workboxes.add(workbox);
			}

		}
		System.err.println("End : " + System.currentTimeMillis());
		return new AdminParseResponse(null, null, null, workboxes, resultCount, null, null);
	}

	@SuppressWarnings("unused")
	private static String[] getToken() {
		String[] result = new String[2];
		try {
			String requestUrl = "https://oauthasservices-kbniwmq1aj.hana.ondemand.com/oauth2/api/v1/token?grant_type=client_credentials";
			Object responseObject = RestUtil.callRestService(requestUrl, null, null, "POST", "application/json",
					false, null, "e5422b86-1f6f-33a6-a6b6-2bd5cabde2a1", "Workbox@123", null, null, null).getResponseObject();
			JSONObject resources = ServicesUtil.isEmpty(responseObject) ? null : (JSONObject) responseObject;
			result[0] = resources.optString("access_token");
			result[1] = resources.optString("token_type");
		} catch (Exception e) {
			System.err.println("[PMC][SubstitutionRuleFacade][getPrevRecipient][error]" + e.getMessage());
		}
		return result;
	}
	
	private static Object[] fetchTaskInstances(String requestUrl) {
		Object[] ret = null;
		System.out.println(requestUrl);
		RestResponse restResponse = null;
		requestUrl += "&$inlinecount=allpages";
		restResponse = RestUtil.callRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TI, null, "GET",
				"application/json", false, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null, null, null);
		ret = new Object[2];
		ret[0] = (JSONArray) restResponse.getResponseObject();
		if(!ServicesUtil.isEmpty(restResponse.getHttpResponse())) {
			for(Header header : restResponse.getHttpResponse().getAllHeaders()) {
				if("X-Total-Count".equalsIgnoreCase(header.getName())) {
					ret[1] = header.getValue();
				}
			}
		}
		return ret;
	}

	private JSONArray fetchInstances(String requestUrl) {
		
		System.err.println("Service Call Counter : " + callCount);
		callCount++;
		
		int taskInstancesCount = -1;
		Object responseObject = null;
		HttpResponse httpResponse = null;
		RestResponse restResponse = null;
		JSONArray jsonArray = null;
		JSONArray jsonArraySkip = null;

		if (!ServicesUtil.isEmpty(requestUrl)) {
			requestUrl += "&$top=1000&$inlinecount=allpages";
			restResponse = RestUtil.callRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TI, null, "GET",
					"application/json", false, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null, null, null);
		}
		responseObject = restResponse.getResponseObject();
		httpResponse = restResponse.getHttpResponse();
		for (Header header : httpResponse.getAllHeaders()) {
			if (header.getName().equalsIgnoreCase("X-Total-Count")) {
				taskInstancesCount = Integer.parseInt(header.getValue());
			}
		}
		jsonArray = ServicesUtil.isEmpty(responseObject) ? null : (JSONArray) responseObject;
		if (taskInstancesCount > jsonArray.length()) {
			int skip = 1000;
			for (int k = 1; k < taskInstancesCount / skip; k++) {
				requestUrl += "&$skip=" + (skip * k);
				restResponse = RestUtil.callRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TI, null, "GET",
						"application/json", true, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null, null, null);
				responseObject = restResponse.getResponseObject();
				jsonArraySkip = ServicesUtil.isEmpty(responseObject) ? null : (JSONArray) responseObject;
				jsonArray = mergeJsonArray(jsonArray, jsonArraySkip);
			}
		}
		return jsonArray;
	}

	private static JSONArray mergeJsonArray(JSONArray jsonArray, JSONArray jsonArraySkip) {
		List<Object> object = ServicesUtil.isEmpty(jsonArray) ? null : jsonArray.toList();
		List<Object> objectSkip = ServicesUtil.isEmpty(jsonArraySkip) ? null : jsonArraySkip.toList();
		if (ServicesUtil.isEmpty(object)) {
			if (ServicesUtil.isEmpty(objectSkip)) {
				return null;
			} else {
				return new JSONArray(objectSkip);
			}
		} else {
			if (!ServicesUtil.isEmpty(objectSkip)) {
				object.addAll(objectSkip);
			}
			return new JSONArray(object);
		}
	}

	private List<String> getRecipientUserOfGroup(String groupName) {
		return groupsMapping.getUsersUnderGroup(groupName);
	}
	
	/**
	 * @param processId
	 * @return
	 */
	private ProcessDetail fetchProcessContextDetail(String processId) {
		ProcessDetail processContextDetail = null;
		if (!ServicesUtil.isEmpty(processId)) {
			processContextDetail = new ProcessDetail();
			String processInstanceURL = PMCConstant.REQUEST_URL_INST + "workflow-instances/" + processId + "/context";
			Object responseObject = RestUtil.callRestService(processInstanceURL, PMCConstant.SAML_HEADER_KEY_TC,
					null, "GET", "application/json", false, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null, null, null).getResponseObject();
			JSONObject jsonObject = ServicesUtil.isEmpty(responseObject) ? null : (JSONObject) responseObject;
			if (!ServicesUtil.isEmpty(jsonObject) && jsonObject.toString().contains("projectId")) {
				processContextDetail.setProjectId(jsonObject.optString("projectId"));
				processContextDetail.setLeadCategory(jsonObject.optString("leadCategory"));
				processContextDetail.setMaterialType(jsonObject.optString("materialType"));
				processContextDetail.setMaterialUniqueId(jsonObject.optString("materialUniqueId"));
				processContextDetail.setMaterialId(jsonObject.optString("materialId"));
				processContextDetail.setMaterialDescription(jsonObject.optString("materialDescription"));
				processContextDetail.setRequestedBy(jsonObject.optString("requestedBy"));
				processContextDetail.setLeadCountry(jsonObject.optString("leadCountry"));
				processContextDetail.setProjectDescription(jsonObject.optString("projectDescription"));
				processContextDetail.setRequiredTaskCsv(jsonObject.optString("requiredTasksCsV"));
				processContextDetail.setRegion(jsonObject.optString("region"));
				processContextDetail.setProjectId(jsonObject.optString("projectId"));
			}
		}
		return processContextDetail;
	}

	public AdminParseResponse parseCompleteDetail() {
		System.err.println("Start : " + System.currentTimeMillis());

		List<TaskEventsDto> tasks = null;
		List<ProcessEventsDto> processes = null;
		List<TaskOwnersDto> owners = null;

		JSONObject taskObject = null;
		JSONObject processObject = null;

		TaskEventsDto task = null;
		ProcessEventsDto process = null;
//		TaskOwnersDto owner = null;

		JSONArray taskList = fetchInstances(
				PMCConstant.REQUEST_URL_INST + "task-instances?status=COMPLETED");
		JSONArray processList = fetchInstances(PMCConstant.REQUEST_URL_INST + "workflow-instances?");

		if (!ServicesUtil.isEmpty(taskList) && !ServicesUtil.isEmpty(processList) && taskList.length() > 0
				&& processList.length() > 0) {
			tasks = new ArrayList<TaskEventsDto>();
			processes = new ArrayList<ProcessEventsDto>();
			owners = new ArrayList<TaskOwnersDto>();

			for (Object object : processList) {
				processObject = (JSONObject) object;
				process = new ProcessEventsDto();

				process.setProcessId(processObject.optString("id"));
				process.setName(processObject.optString("definitionId"));
				process.setSubject(processObject.optString("subject"));
				process.setStatus(processObject.optString("status"));
//				process.setRequestId(fetchRequestId(process.getProcessId()));
				process.setRequestId(processObject.optString("businessKey"));
				process.setStartedAt(ServicesUtil.isEmpty(processObject.optString("startedAt")) ? null
						: ServicesUtil.convertAdminFromStringToDate(processObject.optString("startedAt")));
				process.setCompletedAt(ServicesUtil.isEmpty(processObject.optString("completedAt")) ? null
						: ServicesUtil.convertAdminFromStringToDate(processObject.optString("completedAt")));
				process.setStartedBy(processObject.optString("startedBy"));
				UserDetailsDto uDetails = userDetails.getUserDetails(new UserDetailsDto(process.getStartedBy(), null));
				if(!ServicesUtil.isEmpty(uDetails)) {
					process.setStartedByDisplayName(uDetails.getDisplayName());
				}

				processes.add(process);
			}

			for (Object object : taskList) {
				taskObject = (JSONObject) object;

				task = new TaskEventsDto();

				task.setEventId(taskObject.optString("id"));
				task.setProcessId(taskObject.optString("workflowInstanceId"));
				
				task.setProcessName(taskObject.optString("workflowDefinitionId"));
				
				task.setCreatedAt(ServicesUtil.convertAdminFromStringToDate(taskObject.optString("createdAt")));
				task.setDescription(taskObject.optString("description"));
				task.setCurrentProcessor(taskObject.optString("processor"));
				
				if(!ServicesUtil.isEmpty(task.getCurrentProcessor())) {
					UserDetailsDto processorDetails = userDetails.getUserDetails(new UserDetailsDto(task.getCurrentProcessor(), null));
					if(!ServicesUtil.isEmpty(processorDetails)) {
						task.setCurrentProcessorDisplayName(processorDetails.getDisplayName());
					}
				}
				
				task.setSubject(taskObject.optString("subject"));
				task.setStatus(taskObject.optString("status"));
				task.setName(taskObject.optString("definitionId"));
				task.setPriority(taskObject.optString("priority"));
				task.setCompletedAt(ServicesUtil.isEmpty(taskObject.optString("completedAt")) ? null
						: ServicesUtil.convertAdminFromStringToDate(taskObject.optString("completedAt")));

				if(ServicesUtil.isEmpty(taskObject.optString("dueDate"))){
					task.setCompletionDeadLine(new Date(task.getCreatedAt().getTime() + (1000 * 60 * 60 * 24 * 5)));
					task.setSlaDueDate(new Date(task.getCreatedAt().getTime() + (1000 * 60 * 60 * 24 * 5)));
				} else {
					task.setCompletionDeadLine(ServicesUtil.convertAdminFromStringToDate(taskObject.optString("dueDate")));
					task.setSlaDueDate(ServicesUtil.convertAdminFromStringToDate(taskObject.optString("dueDate")));
				}
				
				task.setOrigin("SCP");
				tasks.add(task);
			}
		}
		System.err.println("End : " + System.currentTimeMillis());
		return new AdminParseResponse(tasks, processes, owners, null, 0, null, null);
	}

	
	public ResponseMessage updateCustomAttributes(List<TaskEventsDto> tasks) {
		Map<String, List<CustomAttributeTemplate>> customTemplateMap = null;
		customTemplateMap = getCustomTemplateMap();
		List<CustomAttributeValue> customValues = new ArrayList<CustomAttributeValue>();
		for(TaskEventsDto task : tasks) {
			Map<String, String> customData = fetchCustomData(task.getEventId());
			if(!ServicesUtil.isEmpty(customTemplateMap)) {
				List<CustomAttributeTemplate> templateList = customTemplateMap.get(task.getProcessName());
				if(!ServicesUtil.isEmpty(templateList) && templateList.size() > 0 && !ServicesUtil.isEmpty(customData)) {
					for(CustomAttributeTemplate customTemplate : templateList) {
						customValues.add(new CustomAttributeValue(task.getEventId(), task.getProcessName(), customTemplate.getKey(), customData.get(customTemplate.getKey())));
					}
				}
			}
		}
		try {
			customDao.addCustomAttributeValue(customValues);
			return new ResponseMessage(PMCConstant.SUCCESS, PMCConstant.CODE_SUCCESS, "Custom Values Inserted Successfully");
		} catch (Exception ex) {
			System.err.println("Exception while inserting Custom Value Data : " + ex.getMessage());
			return new ResponseMessage(PMCConstant.FAILURE, PMCConstant.CODE_FAILURE, "Custom Values Not Inserted Successfully, Message : "+ex.getMessage());
		}
	}
	
	private Map<String, String> fetchCustomData(String taskEventId) {
		JSONObject context = null;
		Map<String, String> contextData = null;
		RestResponse restResponse = RestUtil.callRestService(
				PMCConstant.REQUEST_URL_INST + "task-instances/" + taskEventId + "/context", null, null, PMCConstant.HTTP_METHOD_GET,
				PMCConstant.APPLICATION_JSON, false, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null, null, null);
		if(!ServicesUtil.isEmpty(restResponse) && !ServicesUtil.isEmpty(restResponse.getResponseObject())) {
			if(restResponse.getResponseObject().toString().startsWith("{")) {
				contextData = new HashMap<String, String>();
				context = (JSONObject) restResponse.getResponseObject();
				Iterator<String> keys = context.keys();
				while(keys.hasNext()) {
					String key = keys.next();
					contextData.put(key, context.optString(key));
				}
			}
		}
		return contextData;
	}
	
	private Map<String, List<CustomAttributeTemplate>> getCustomTemplateMap() {
		List<ProcessConfigDto> pcEntry = processConfigDao.getAllProcessConfigEntry();
		List<CustomAttributeTemplate> customTemplates = null;
		Map<String, List<CustomAttributeTemplate>> customTemplateMap = null;
		if(!ServicesUtil.isEmpty(pcEntry) && pcEntry.size() > 0) {
			customTemplateMap = new HashMap<>();
			for(ProcessConfigDto pcDto : pcEntry) {
				customTemplates = customDao.getCustomAttributeTemplates(pcDto.getProcessName(), null, null);
				customTemplateMap.put(pcDto.getProcessName(), customTemplates);
			}
		}
		return customTemplateMap;
	}
}
