package oneapp.workbox.services.adapters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jsoniter.JsonIterator;
import com.jsoniter.spi.TypeLiteral;

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
import oneapp.workbox.services.entity.GroupsMapping;
import oneapp.workbox.services.entity.ProcessDetail;
import oneapp.workbox.services.entity.ProcessEventsDo;
import oneapp.workbox.services.entity.ProjectProcessMapping;
import oneapp.workbox.services.entity.TaskEventsDo;
import oneapp.workbox.services.entity.TaskOwnersDo;
import oneapp.workbox.services.entity.TaskOwnersDoPK;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.RestUtil;
import oneapp.workbox.services.util.ServicesUtil;

@Component
public class AdminParse {

	private Map<String, UserDetailsDto> usersMap;
	private Map<String, String> groupMappings;

	@Autowired
	UserDetailsDao userDetails;

	@Autowired
	CustomAttributeDao customDao;

	@Autowired
	ProcessConfigDao processConfigDao;

	@Autowired
	GroupsMappingDao groupsMapping;

	private int callCount = 0;

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
		JSONArray processList = fetchInstances(
				PMCConstant.REQUEST_URL_INST + "workflow-instances?status=RUNNING&status=ERRONEOUS&status=COMPLETED");

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
				// processDetail = new ProcessDetail();

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
				if (!ServicesUtil.isEmpty(uDetails)) {
					process.setStartedByDisplayName(uDetails.getDisplayName());
				}

				if (!ServicesUtil.isEmpty(processDetail)) {
					prjPrcMap.setProcessId(process.getProcessId());
					prjPrcMap.setProjectId(processDetail.getProjectId());

					processDetail.setProcessId(process.getProcessId());
				}

				if (!ServicesUtil.isEmpty(prjPrcMap.getProcessId())
						&& !ServicesUtil.isEmpty(prjPrcMap.getProjectId())) {
					prjPrcMaps.add(prjPrcMap);
				}

				if (!ServicesUtil.isEmpty(processDetail)) {
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

				if (!ServicesUtil.isEmpty(task.getCurrentProcessor())) {
					UserDetailsDto processorDetails = userDetails
							.getUserDetails(new UserDetailsDto(task.getCurrentProcessor(), null));
					if (!ServicesUtil.isEmpty(processorDetails)) {
						task.setCurrentProcessorDisplayName(processorDetails.getDisplayName());
					}
				}

				task.setSubject(taskObject.optString("subject"));
				task.setStatus(taskObject.optString("status"));
				task.setName(taskObject.optString("definitionId"));
				task.setPriority(taskObject.optString("priority"));
				task.setCompletedAt(ServicesUtil.isEmpty(taskObject.optString("completedAt")) ? null
						: ServicesUtil.convertFromStringToDate(taskObject.optString("completedAt")));

				if (ServicesUtil.isEmpty(taskObject.optString("dueDate"))) {
					task.setCompletionDeadLine(new Date(task.getCreatedAt().getTime() + (1000 * 60 * 60 * 24 * 5)));
					task.setSlaDueDate(new Date(task.getCreatedAt().getTime() + (1000 * 60 * 60 * 24 * 5)));
				} else {
					task.setCompletionDeadLine(
							ServicesUtil.convertAdminFromStringToDate(taskObject.optString("dueDate")));
					task.setSlaDueDate(ServicesUtil.convertAdminFromStringToDate(taskObject.optString("dueDate")));
				}

				task.setOrigin("SCP");
				tasks.add(task);

				if (!ServicesUtil.isEmpty(task.getCurrentProcessor())) {
					owner = new TaskOwnersDto();
					owner.setEventId(task.getEventId());
					owner.setTaskOwner(task.getCurrentProcessor());
					owner.setIsProcessed(true);
					UserDetailsDto ownerDetails = userDetails
							.getUserDetails(new UserDetailsDto(owner.getTaskOwner(), null));
					if (!ServicesUtil.isEmpty(ownerDetails)) {
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
					UserDetailsDto ownerDetails = userDetails
							.getUserDetails(new UserDetailsDto(owner.getTaskOwner(), null));
					if (!ServicesUtil.isEmpty(ownerDetails)) {
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
		Object[] objects = fetchTaskInstances(PMCConstant.REQUEST_URL_INST + "task-instances?" + relativeQueryParams);
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
				workbox.setCreatedAt(simpleDateFormat1
						.format(ServicesUtil.convertFromStringToDate(taskObject.optString("createdAt"))));
				workbox.setTaskDescription(taskObject.optString("description"));
				workbox.setStatus(taskObject.optString("status"));
				workbox.setSubject(taskObject.optString("subject"));
				workbox.setStartedBy(taskObject.optString("createdBy"));
				workbox.setSlaDisplayDate(ServicesUtil.isEmpty(taskObject.optString("dueDate")) ? null
						: simpleDateFormat1.format(ServicesUtil.resultAsDate(taskObject.optString("dueDate"))));
				if (!ServicesUtil.isEmpty(taskObject.optString("dueDate"))
						&& !ServicesUtil.isEmpty(taskObject.optString("createdAt"))) {
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
			Object responseObject = RestUtil
					.callRestService(requestUrl, null, null, "POST", "application/json", false, null,
							"e5422b86-1f6f-33a6-a6b6-2bd5cabde2a1", "Workbox@123", null, null, null)
					.getResponseObject();
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
				"application/json", false, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null, null,
				null);
		ret = new Object[2];
		ret[0] = (JSONArray) restResponse.getResponseObject();
		if (!ServicesUtil.isEmpty(restResponse.getHttpResponse())) {
			for (Header header : restResponse.getHttpResponse().getAllHeaders()) {
				if ("X-Total-Count".equalsIgnoreCase(header.getName())) {
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
					"application/json", false, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null, null,
					null);
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
						"application/json", true, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null,
						null, null);
				responseObject = restResponse.getResponseObject();
				jsonArraySkip = ServicesUtil.isEmpty(responseObject) ? null : (JSONArray) responseObject;
				jsonArray = mergeJsonArray(jsonArray, jsonArraySkip);
			}
		}
		return jsonArray;
	}

	@SuppressWarnings("rawtypes")
	private List getInstances(String requestUrl, Class clazz) {

		int taskInstancesCount = -1;
		Object responseObject = null;
		HttpResponse httpResponse = null;
		RestResponse restResponse = null;
		List<Task> taskArray = null;
		List<Task> taskArraySkip = null;

		List<Process> processArray = null;
		List<Process> processArraySkip = null;
		
		int taskArraySize = 0;
		int processArraySize = 0;
		
		if (!ServicesUtil.isEmpty(requestUrl)) {
			requestUrl += "&$top=1000&$inlinecount=allpages";
			restResponse = RestUtil.invokeRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TI, null, "GET",
					"application/json", false, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null, null,
					null);
		}
		responseObject = restResponse.getResponseObject();
		httpResponse = restResponse.getHttpResponse();
		if(!ServicesUtil.isEmpty(httpResponse)) {
			for (Header header : httpResponse.getAllHeaders()) {
				if (header.getName().equalsIgnoreCase("X-Total-Count")) {
					taskInstancesCount = Integer.parseInt(header.getValue());
				}
			}
		} else if(!ServicesUtil.isEmpty(restResponse.getUrlConnection()) && restResponse.getResponseCode() == HttpURLConnection.HTTP_OK) {
			taskInstancesCount = Integer.parseInt(restResponse.getUrlConnection().getHeaderField("X-Total-Count"));
		}
		if (clazz.equals(Task.class)) {
			taskArray = ServicesUtil.isEmpty(responseObject) ? null
					: JsonIterator.deserialize(responseObject.toString(), new TypeLiteral<List<Task>>() {
					});
			if(!ServicesUtil.isEmpty(taskArray))
				taskArraySize = taskArray.size();
			if (taskInstancesCount > taskArraySize) {
				int skip = 1000;
				for (int k = 1; k < taskInstancesCount / skip; k++) {
					requestUrl += "&$skip=" + (skip * k);
					restResponse = RestUtil.invokeRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TI, null, "GET",
							"application/json", true, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null,
							null, null);
					responseObject = restResponse.getResponseObject();
					taskArraySkip = ServicesUtil.isEmpty(responseObject) ? null
							: JsonIterator.deserialize(responseObject.toString(), new TypeLiteral<List<Task>>() {
							});
					if (!ServicesUtil.isEmpty(taskArray) && taskArraySize > 0) {
						taskArray.addAll(taskArraySkip);
					} else {
						taskArray = taskArraySkip;
					}
				}
			}
			return taskArray;
		} else if (clazz.equals(Process.class)) {
			processArray = ServicesUtil.isEmpty(responseObject) ? null
					: JsonIterator.deserialize(responseObject.toString(), new TypeLiteral<List<Process>>() {
					});
			if(!ServicesUtil.isEmpty(processArray))
				processArraySize = processArray.size();
			if (taskInstancesCount > processArraySize) {
				int skip = 1000;
				for (int k = 1; k < taskInstancesCount / skip; k++) {
					requestUrl += "&$skip=" + (skip * k);
					restResponse = RestUtil.invokeRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TI, null, "GET",
							"application/json", true, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null,
							null, null);
					responseObject = restResponse.getResponseObject();
					processArraySkip = ServicesUtil.isEmpty(responseObject) ? null
							: JsonIterator.deserialize(responseObject.toString(), new TypeLiteral<List<Process>>() {
							});
					if (!ServicesUtil.isEmpty(processArray) && processArraySize > 0) {
						processArray.addAll(processArraySkip);
					} else {
						processArray = processArraySkip;
					}
				}
			}
			return processArray;
		}
		return null;

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
		return getUsersUnderGroup(groupName);
	}

	public String getGroupsMapping(String groupName) {
		String groups = null;
		if (ServicesUtil.isEmpty(groupMappings)) {
			refreshAllGroupsMappings();
			groups = getGroupsMapping(groupName);
		} else {
			groups = groupMappings.get(groupName);
		}
		return groups;
	}

	private List<String> getUsersUnderGroup(String groupName) {
		String users = this.getGroupsMapping(groupName);
		List<String> groupUsers = null;
		if (!ServicesUtil.isEmpty(users)) {
			groupUsers = new ArrayList<>();
			if (!ServicesUtil.isEmpty(users) && users.contains(",")) {
				String[] usersArray = users.split(",");
				for (String u : usersArray) {
					groupUsers.add(u.trim());
				}
			} else if (!ServicesUtil.isEmpty(users) && !users.contains(",")) {
				groupUsers.add(users.trim());
			}
		}
		return groupUsers;
	}

	private void emptyGroupsMapping() {
		groupMappings = null;
	}

	private Map<String, String> refreshAllGroupsMappings() {
		Map<String, String> mappings = new HashMap<String, String>();
		List<GroupsMapping> groupMappings = groupsMapping.getGroupMappingsResponse();
		for (GroupsMapping group : groupMappings) {
			mappings.put(group.getGroupName(), group.getUsers());
		}
		this.groupMappings = mappings;
		return mappings;
	}

	private UserDetailsDto getUserDetails(UserDetailsDto userDetail) {
		UserDetailsDto user = null;
		if (ServicesUtil.isEmpty(usersMap)) {
			refreshUserDetailsMap();
			user = getUserDetails(userDetail);
		} else {
			user = usersMap.get(userDetail.getUserId());
		}
		return user;
	}

	private Map<String, UserDetailsDto> refreshUserDetailsMap() {
		Map<String, UserDetailsDto> userDetailsMap = new HashMap<String, UserDetailsDto>();
		UserDetailsDto userDetails = null;
		List<Object[]> resultList = this.userDetails.getUserDetailResponse();
		if (!ServicesUtil.isEmpty(resultList) && resultList.size() > 0) {
			for (Object[] object : resultList) {
				userDetails = new UserDetailsDto();
				userDetails.setUserId(ServicesUtil.isEmpty(object[0]) ? null : (String) object[0]);
				userDetails.setEmailId(ServicesUtil.isEmpty(object[3]) ? null : (String) object[3]);
				userDetails.setDisplayName(ServicesUtil.isEmpty(object[4]) ? null : (String) object[4]);
				userDetailsMap.put(userDetails.getUserId(), userDetails);
			}
		}
		usersMap = userDetailsMap;
		return userDetailsMap;
	}

	private void emptyUsersMap() {
		usersMap = null;
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
			Object responseObject = RestUtil.callRestService(processInstanceURL, PMCConstant.SAML_HEADER_KEY_TC, null,
					"GET", "application/json", false, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null,
					null, null).getResponseObject();
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
		// TaskOwnersDto owner = null;

		JSONArray taskList = fetchInstances(PMCConstant.REQUEST_URL_INST + "task-instances?status=COMPLETED");
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
				// process.setRequestId(fetchRequestId(process.getProcessId()));
				process.setRequestId(processObject.optString("businessKey"));
				process.setStartedAt(ServicesUtil.isEmpty(processObject.optString("startedAt")) ? null
						: ServicesUtil.convertAdminFromStringToDate(processObject.optString("startedAt")));
				process.setCompletedAt(ServicesUtil.isEmpty(processObject.optString("completedAt")) ? null
						: ServicesUtil.convertAdminFromStringToDate(processObject.optString("completedAt")));
				process.setStartedBy(processObject.optString("startedBy"));
				UserDetailsDto uDetails = userDetails.getUserDetails(new UserDetailsDto(process.getStartedBy(), null));
				if (!ServicesUtil.isEmpty(uDetails)) {
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

				if (!ServicesUtil.isEmpty(task.getCurrentProcessor())) {
					UserDetailsDto processorDetails = userDetails
							.getUserDetails(new UserDetailsDto(task.getCurrentProcessor(), null));
					if (!ServicesUtil.isEmpty(processorDetails)) {
						task.setCurrentProcessorDisplayName(processorDetails.getDisplayName());
					}
				}

				task.setSubject(taskObject.optString("subject"));
				task.setStatus(taskObject.optString("status"));
				task.setName(taskObject.optString("definitionId"));
				task.setPriority(taskObject.optString("priority"));
				task.setCompletedAt(ServicesUtil.isEmpty(taskObject.optString("completedAt")) ? null
						: ServicesUtil.convertAdminFromStringToDate(taskObject.optString("completedAt")));

				if (ServicesUtil.isEmpty(taskObject.optString("dueDate"))) {
					task.setCompletionDeadLine(new Date(task.getCreatedAt().getTime() + (1000 * 60 * 60 * 24 * 5)));
					task.setSlaDueDate(new Date(task.getCreatedAt().getTime() + (1000 * 60 * 60 * 24 * 5)));
				} else {
					task.setCompletionDeadLine(
							ServicesUtil.convertAdminFromStringToDate(taskObject.optString("dueDate")));
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
		for (TaskEventsDto task : tasks) {
			Map<String, String> customData = fetchCustomData(task.getEventId());
			if (!ServicesUtil.isEmpty(customTemplateMap)) {
				List<CustomAttributeTemplate> templateList = customTemplateMap.get(task.getProcessName());
				if (!ServicesUtil.isEmpty(templateList) && templateList.size() > 0
						&& !ServicesUtil.isEmpty(customData)) {
					for (CustomAttributeTemplate customTemplate : templateList) {
						customValues.add(new CustomAttributeValue(task.getEventId(), task.getProcessName(),
								customTemplate.getKey(), customData.get(customTemplate.getKey())));
					}
				}
			}
		}
		try {
			customDao.addCustomAttributeValue(customValues);
			return new ResponseMessage(PMCConstant.SUCCESS, PMCConstant.CODE_SUCCESS,
					"Custom Values Inserted Successfully");
		} catch (Exception ex) {
			System.err.println("Exception while inserting Custom Value Data : " + ex.getMessage());
			return new ResponseMessage(PMCConstant.FAILURE, PMCConstant.CODE_FAILURE,
					"Custom Values Not Inserted Successfully, Message : " + ex.getMessage());
		}
	}

	private Map<String, String> fetchCustomData(String taskEventId) {
		JSONObject context = null;
		Map<String, String> contextData = null;
		RestResponse restResponse = RestUtil.callRestService(
				PMCConstant.REQUEST_URL_INST + "task-instances/" + taskEventId + "/context", null, null,
				PMCConstant.HTTP_METHOD_GET, PMCConstant.APPLICATION_JSON, false, null, PMCConstant.WF_BASIC_USER,
				PMCConstant.WF_BASIC_PASS, null, null, null);
		if (!ServicesUtil.isEmpty(restResponse) && !ServicesUtil.isEmpty(restResponse.getResponseObject())) {
			if (restResponse.getResponseObject().toString().startsWith("{")) {
				contextData = new HashMap<String, String>();
				context = (JSONObject) restResponse.getResponseObject();
				Iterator<String> keys = context.keys();
				while (keys.hasNext()) {
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
		if (!ServicesUtil.isEmpty(pcEntry) && pcEntry.size() > 0) {
			customTemplateMap = new HashMap<>();
			for (ProcessConfigDto pcDto : pcEntry) {
				customTemplates = customDao.getCustomAttributeTemplates(pcDto.getProcessName(), null, null);
				customTemplateMap.put(pcDto.getProcessName(), customTemplates);
			}
		}
		return customTemplateMap;
	}

	public AdminParseResponse parseNewDetail() {
		System.err.println("Start : " + new Date());

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

		System.err.println("Fetch API Response Start : " + new Date());

		JSONArray taskList = fetchInstances(PMCConstant.REQUEST_URL_INST
				+ "task-instances?status=READY&status=RESERVED&status=CANCELED&status=COMPLETED&$expand=attributes");
		JSONArray processList = fetchInstances(PMCConstant.REQUEST_URL_INST
				+ "workflow-instances?status=RUNNING&status=ERRONEOUS&status=CANCELED&status=COMPLETED");

		System.err.println("Fetch API Response End : " + new Date());

		System.err.println("Parse Response Start : " + new Date());

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
				// processDetail = new ProcessDetail();

				process.setProcessId(processObject.optString("id"));

				process.setName(processObject.optString("definitionId"));
				process.setSubject(processObject.optString("subject"));
				process.setStatus(processObject.optString("status"));

				process.setRequestId(processObject.optString("businessKey"));
				process.setStartedAt(ServicesUtil.isEmpty(processObject.optString("startedAt")) ? null
						: ServicesUtil.convertAdminFromStringToDate(processObject.optString("startedAt")));
				process.setCompletedAt(ServicesUtil.isEmpty(processObject.optString("completedAt")) ? null
						: ServicesUtil.convertAdminFromStringToDate(processObject.optString("completedAt")));
				process.setStartedBy(processObject.optString("startedBy"));
				UserDetailsDto uDetails = userDetails.getUserDetails(new UserDetailsDto(process.getStartedBy(), null));
				if (!ServicesUtil.isEmpty(uDetails)) {
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

				if (!ServicesUtil.isEmpty(task.getCurrentProcessor())) {
					UserDetailsDto processorDetails = userDetails
							.getUserDetails(new UserDetailsDto(task.getCurrentProcessor(), null));
					if (!ServicesUtil.isEmpty(processorDetails)) {
						task.setCurrentProcessorDisplayName(processorDetails.getDisplayName());
					}
				}

				task.setSubject(taskObject.optString("subject"));
				task.setStatus(taskObject.optString("status"));
				task.setName(taskObject.optString("definitionId"));
				task.setPriority(taskObject.optString("priority"));
				task.setCompletedAt(ServicesUtil.isEmpty(taskObject.optString("completedAt")) ? null
						: ServicesUtil.convertAdminFromStringToDate(taskObject.optString("completedAt")));

				if (ServicesUtil.isEmpty(taskObject.optString("dueDate"))) {
					task.setCompletionDeadLine(new Date(task.getCreatedAt().getTime() + (1000 * 60 * 60 * 24 * 5)));
					task.setSlaDueDate(new Date(task.getCreatedAt().getTime() + (1000 * 60 * 60 * 24 * 5)));
				} else {
					task.setCompletionDeadLine(
							ServicesUtil.convertAdminFromStringToDate(taskObject.optString("dueDate")));
					task.setSlaDueDate(ServicesUtil.convertAdminFromStringToDate(taskObject.optString("dueDate")));
				}

				task.setOrigin("SCP");

				processDetail = fetchProcessContextDetail(taskObject);

				if (!ServicesUtil.isEmpty(processDetail)) {
					prjPrcMap.setProcessId(process.getProcessId());
					prjPrcMap.setProjectId(processDetail.getProjectId());

					processDetail.setProcessId(process.getProcessId());
				}

				if (!ServicesUtil.isEmpty(prjPrcMap.getProcessId())
						&& !ServicesUtil.isEmpty(prjPrcMap.getProjectId())) {
					prjPrcMaps.add(prjPrcMap);
				}

				if (!ServicesUtil.isEmpty(processDetail)) {
					processDetails.add(processDetail);
				}

				tasks.add(task);

				if (!ServicesUtil.isEmpty(task.getCurrentProcessor())) {
					owner = new TaskOwnersDto();
					owner.setEventId(task.getEventId());
					owner.setTaskOwner(task.getCurrentProcessor());
					owner.setIsProcessed(true);
					UserDetailsDto ownerDetails = userDetails
							.getUserDetails(new UserDetailsDto(owner.getTaskOwner(), null));
					if (!ServicesUtil.isEmpty(ownerDetails)) {
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
					UserDetailsDto ownerDetails = userDetails
							.getUserDetails(new UserDetailsDto(owner.getTaskOwner(), null));
					if (!ServicesUtil.isEmpty(ownerDetails)) {
						owner.setOwnerEmail(ownerDetails.getEmailId());
						owner.setTaskOwnerDisplayName(ownerDetails.getDisplayName());
					}
					owners.add(owner);
				}

			}

		}
		System.err.println("Parse Response End : " + new Date());
		System.err.println("End : " + new Date());
		return new AdminParseResponse(tasks, processes, owners, null, 0, prjPrcMaps, processDetails);
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {

		// AdminParseResponse parseAPI = new
		// AnnotationConfigApplicationContext(HibernateConfiguration.class).getBean(AdminParse.class).parseAPI();
		//
		// System.out.println(parseAPI.getProcessDetails());
		// System.out.println(parseAPI.getTasks().size());
		// System.out.println(parseAPI.getProcesses().size());
		// System.out.println(parseAPI.getOwners().size());
		// System.out.println(parseAPI.getProcessDetails().size());

		List<Task> taskList = new AdminParse().getInstances(
				PMCConstant.REQUEST_URL_INST
						+ "task-instances?status=READY&status=RESERVED&status=CANCELED&status=COMPLETED&$expand=attributes",
				Task.class);
		List<Process> processList = new AdminParse().getInstances(
				PMCConstant.REQUEST_URL_INST
						+ "workflow-instances?status=RUNNING&status=ERRONEOUS&status=CANCELED&status=COMPLETED",
				Process.class);

		System.out.println(taskList.get(0));
		System.out.println(processList.get(0));

	}

	@SuppressWarnings("unchecked")
	public AdminParseResponseObject parseAPI() {

		List<TaskEventsDo> tasks = null;
		TaskEventsDo task = null;

		List<TaskOwnersDo> owners = null;
		TaskOwnersDo owner = null;

		List<ProcessEventsDo> processes = null;
		ProcessEventsDo process = null;

		List<ProjectProcessMapping> prjPrcMaps = null;

		List<ProcessDetail> processDetails = null;
		ProcessDetail processDetail = null;
		
		Set<ProjectProcessMapping> prjPrcMapSet = null;
		Set<ProcessDetail> processDetailSet = null;

		System.err.println("[parseAPI]Fetch Start : " + System.currentTimeMillis());

		List<Task> taskList = getInstances(
				PMCConstant.REQUEST_URL_INST
						+ "task-instances?status=READY&status=RESERVED&status=CANCELED&status=COMPLETED&$expand=attributes",
				Task.class);
		List<Process> processList = getInstances(
				PMCConstant.REQUEST_URL_INST
						+ "workflow-instances?status=RUNNING&status=ERRONEOUS&status=CANCELED&status=COMPLETED",
				Process.class);

		System.err.println("[parseAPI]Fetch End : " + System.currentTimeMillis());

		tasks = new ArrayList<TaskEventsDo>();
		owners = new ArrayList<TaskOwnersDo>();
		processes = new ArrayList<ProcessEventsDo>();

		processDetails = new ArrayList<ProcessDetail>();
		prjPrcMaps = new ArrayList<ProjectProcessMapping>();
		
		prjPrcMapSet = new LinkedHashSet<ProjectProcessMapping>();
		processDetailSet = new LinkedHashSet<ProcessDetail>();

		UserDetailsDto processorDetails = null;
		UserDetailsDto ownerDetails = null;

		System.err.println("[parseAPI]Parse Start : " + System.currentTimeMillis());

		if (!ServicesUtil.isEmpty(taskList) && taskList.size() > 0) {
			for (Task parseTask : taskList) {
				if (!ServicesUtil.isEmpty(parseTask)) {

					task = new TaskEventsDo();

					task.setEventId(parseTask.getId());
					task.setProcessId(parseTask.getWorkflowInstanceId());
					task.setProcessName(parseTask.getWorkflowDefinitionId());
					task.setCreatedAt(ServicesUtil.convertAdminFromStringToDate(parseTask.getCreatedAt()));
					task.setDescription(parseTask.getDescription());
					task.setCurrentProcessor(parseTask.getProcessor());

					if (!ServicesUtil.isEmpty(task.getCurrentProcessor())) {
						processorDetails = getUserDetails(new UserDetailsDto(task.getCurrentProcessor(), null));
						if (!ServicesUtil.isEmpty(processorDetails)) {
							task.setCurrentProcessorDisplayName(processorDetails.getDisplayName());
						}
					}

					task.setSubject(parseTask.getSubject());
					task.setStatus(parseTask.getStatus());
					task.setName(parseTask.getDefinitionId());
					task.setPriority(parseTask.getPriority());
					task.setCompletedAt(ServicesUtil.isEmpty(parseTask.getCompletedAt()) ? null
							: ServicesUtil.convertAdminFromStringToDate(parseTask.getCompletedAt()));

					if (ServicesUtil.isEmpty(parseTask.getDueDate())) {
						task.setCompletionDeadLine(new Date(task.getCreatedAt().getTime() + (1000 * 60 * 60 * 24 * 5)));
						task.setSlaDueDate(new Date(task.getCreatedAt().getTime() + (1000 * 60 * 60 * 24 * 5)));
					} else {
						task.setCompletionDeadLine(ServicesUtil.convertAdminFromStringToDate(parseTask.getDueDate()));
						task.setSlaDueDate(ServicesUtil.convertAdminFromStringToDate(parseTask.getDueDate()));
					}

					task.setOrigin("SCP");
					tasks.add(task);

					if (!ServicesUtil.isEmpty(parseTask.getAttributes()) && parseTask.getAttributes().size() > 0) {
						processDetail = new ProcessDetail();
						processDetail.setProcessId(task.getProcessId());
						for (Attribute attribute : parseTask.getAttributes()) {
							setProcessDetail(processDetail, attribute);
						}
					}

//					processDetails.add(processDetail);
					processDetailSet.add(processDetail);

					if (!ServicesUtil.isEmpty(processDetail) && !ServicesUtil.isEmpty(processDetail.getProcessId())
							&& !ServicesUtil.isEmpty(processDetail.getProjectId())) {
//						prjPrcMaps.add(
//								new ProjectProcessMapping(processDetail.getProcessId(), processDetail.getProjectId()));
						prjPrcMapSet.add(
								new ProjectProcessMapping(processDetail.getProcessId(), processDetail.getProjectId()));
					}

					if (!ServicesUtil.isEmpty(task.getCurrentProcessor())) {
						owner = new TaskOwnersDo();
						owner.setTaskOwnersDoPK(new TaskOwnersDoPK(task.getEventId(), task.getCurrentProcessor()));
						owner.setIsProcessed(true);
						if(!ServicesUtil.isEmpty(owner) && !ServicesUtil.isEmpty(owner.getTaskOwnersDoPK()))
							ownerDetails = getUserDetails(new UserDetailsDto(owner.getTaskOwnersDoPK().getTaskOwner(), null));
						if (!ServicesUtil.isEmpty(ownerDetails)) {
							owner.setOwnerEmail(ownerDetails.getEmailId());
							owner.setTaskOwnerDisplayName(ownerDetails.getDisplayName());
						}
						owners.add(owner);
					}

					List<String> recepients = parseTask.getRecipientUsers();
					List<String> recepientGroups = parseTask.getRecipientGroups();
					for (String group : recepientGroups) {
						recepients.addAll(getRecipientUserOfGroup((String) group));
					}

					for (String recepient : recepients) {
						owner = new TaskOwnersDo();
						owner.setTaskOwnersDoPK(new TaskOwnersDoPK(task.getEventId(), recepient));
						owner.setIsProcessed(false);
						if(!ServicesUtil.isEmpty(owner) && !ServicesUtil.isEmpty(owner.getTaskOwnersDoPK()))
							ownerDetails = getUserDetails(new UserDetailsDto(owner.getTaskOwnersDoPK().getTaskOwner(), null));
						if (!ServicesUtil.isEmpty(ownerDetails)) {
							owner.setOwnerEmail(ownerDetails.getEmailId());
							owner.setTaskOwnerDisplayName(ownerDetails.getDisplayName());
						}
						owners.add(owner);
					}

				}
			}
		}

		if (!ServicesUtil.isEmpty(processList) && processList.size() > 0) {
			for (Process parseProcess : processList) {
				if (!ServicesUtil.isEmpty(parseProcess)) {

					process = new ProcessEventsDo();

					process.setProcessId(parseProcess.getId());

					process.setName(parseProcess.getDefinitionId());
					process.setSubject(parseProcess.getSubject());
					process.setStatus(parseProcess.getStatus());

					process.setRequestId(parseProcess.getBusinessKey());
					process.setStartedAt(ServicesUtil.isEmpty(parseProcess.getStartedAt()) ? null
							: ServicesUtil.convertAdminFromStringToDate(parseProcess.getStartedAt()));
					process.setCompletedAt(ServicesUtil.isEmpty(parseProcess.getCompletedAt()) ? null
							: ServicesUtil.convertAdminFromStringToDate(parseProcess.getCompletedAt()));
					process.setStartedBy(parseProcess.getStartedBy());
					UserDetailsDto uDetails = getUserDetails(new UserDetailsDto(process.getStartedBy(), null));
					if (!ServicesUtil.isEmpty(uDetails)) {
						process.setStartedByDisplayName(uDetails.getDisplayName());
					}

					processes.add(process);

				}
			}
		}

		System.err.println("[parseAPI]Parse End : " + System.currentTimeMillis());

		emptyGroupsMapping();
		emptyUsersMap();
		
		processDetails.addAll(processDetailSet);
		prjPrcMaps.addAll(prjPrcMapSet);

		return new AdminParseResponseObject(tasks, processes, owners, null, 0, prjPrcMaps, processDetails);
	}

	/**
	 * @param processId
	 * @return
	 */
	private ProcessDetail fetchProcessContextDetail(JSONObject taskObject) {
		ProcessDetail processContextDetail = null;
		JSONObject taskAttribute = null;
		if (!ServicesUtil.isEmpty(taskObject)) {
			JSONArray taskAttributes = taskObject.optJSONArray("attributes");
			processContextDetail = new ProcessDetail();
			if (!ServicesUtil.isEmpty(taskAttributes) && taskAttributes.length() > 0) {
				for (Object object : taskAttributes) {
					taskAttribute = (JSONObject) object;
					setProcessDetail(processContextDetail, taskAttribute);
				}
			}
			processContextDetail.setProcessId(taskObject.optString("workflowInstanceId"));
			processContextDetail.setProcessName(taskObject.optString("workflowDefinitionId"));
		}
		return processContextDetail;
	}

	private void setProcessDetail(ProcessDetail processContextDetail, JSONObject taskAttribute) {
		if (!ServicesUtil.isEmpty(taskAttribute)) {
			switch (taskAttribute.optString("id")) {
			case "ProjectId":
				processContextDetail.setProjectId(taskAttribute.optString("value"));
				break;
			case "MaterialId":
				processContextDetail.setMaterialId(taskAttribute.optString("value"));
				break;
			case "ProjectDescription":
				processContextDetail.setProjectDescription(taskAttribute.optString("value"));
				break;
			case "MaterialDescription":
				processContextDetail.setMaterialDescription(taskAttribute.optString("value"));
				break;
			case "MaterialType":
				processContextDetail.setMaterialType(taskAttribute.optString("value"));
				break;
			case "NodeId":
				processContextDetail.setNodeId(taskAttribute.optString("value"));
				break;
			case "Key":
				processContextDetail.setKey(taskAttribute.optString("value"));
				break;
			case "SubKey":
				processContextDetail.setSubKey(taskAttribute.optString("value"));
				break;
			case "Country":
				processContextDetail.setCountry(taskAttribute.optString("value"));
				break;
			default:
				break;
			}
		}
	}

	private void setProcessDetail(ProcessDetail processContextDetail, Attribute taskAttribute) {
		if (!ServicesUtil.isEmpty(taskAttribute)) {
			switch (taskAttribute.getId()) {
			case "ProjectId":
				processContextDetail.setProjectId(taskAttribute.getValue());
				break;
			case "MaterialId":
				processContextDetail.setMaterialId(taskAttribute.getValue());
				break;
			case "ProjectDescription":
				processContextDetail.setProjectDescription(taskAttribute.getValue());
				break;
			case "MaterialDescription":
				processContextDetail.setMaterialDescription(taskAttribute.getValue());
				break;
			case "MaterialType":
				processContextDetail.setMaterialType(taskAttribute.getValue());
				break;
			case "NodeId":
				processContextDetail.setNodeId(taskAttribute.getValue());
				break;
			case "Key":
				processContextDetail.setKey(taskAttribute.getValue());
				break;
			case "SubKey":
				processContextDetail.setSubKey(taskAttribute.getValue());
				break;
			case "Country":
				processContextDetail.setCountry(taskAttribute.getValue());
				break;
			default:
				break;
			}
		}
	}
}
