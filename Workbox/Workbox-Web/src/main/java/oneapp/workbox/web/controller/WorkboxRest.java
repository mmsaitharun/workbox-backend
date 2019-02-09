package oneapp.workbox.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import oneapp.workbox.services.adapters.AdminParse;
import oneapp.workbox.services.adapters.AdminParse.AdminParseResponse;
import oneapp.workbox.services.dao.ProcessEventsDao;
import oneapp.workbox.services.dao.TaskEventsDao;
import oneapp.workbox.services.dao.TaskOwnersDao;
import oneapp.workbox.services.dto.ActionDto;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.SlaProcessNameListDto;
import oneapp.workbox.services.dto.TaskOwnersDto;
import oneapp.workbox.services.dto.TrackingResponse;
import oneapp.workbox.services.dto.WorkboxRequestDto;
import oneapp.workbox.services.dto.WorkboxResponseDto;
import oneapp.workbox.services.scheduler.EventsUpdateScheduler;
import oneapp.workbox.services.service.WorkFlowActionFacadeLocal;
import oneapp.workbox.services.service.WorkboxFacade;

@RestController
@CrossOrigin
@ComponentScan("oneapp.incture.workbox")
@RequestMapping(value = "/inbox")
public class WorkboxRest {

	@Autowired
	WorkboxFacade workbox;

	@Autowired
	WorkFlowActionFacadeLocal workFlowAction;

	@Autowired
	EventsUpdateScheduler eventsUpdate;

	@RequestMapping(value = "/sayHello", method = RequestMethod.GET, produces = "application/json")
	public WorkboxRequestDto sayHello() {
		// WorkboxFacadeLocal workbox = new WorkboxFacade();
		WorkboxRequestDto dto = new WorkboxRequestDto();
		List<String> processName = new ArrayList<>();
		processName.add("leaveapprovalprocess");
		processName.add("testworkflow");
		dto.setRequestId("RequestID");
		dto.setStatus("READY");
		dto.setProcessName(processName);
		dto.setCreatedAt("09/19/2018");
		dto.setMaxCount(5);
		dto.setOrderBy("ASC/DESC");
		dto.setOrderType("createdAt/dueDate");
		dto.setCreatedBy("created by ID");
		dto.setSkipCount(0);
		dto.setPage(1);

		TaskOwnersDto userdto = new TaskOwnersDto();
		userdto.setTaskOwner("P000035");
		userdto.setOwnerEmail("rtwk1001@gmail.com");
		userdto.setTaskOwnerDisplayName("Ritwik Jain");
		dto.setCurrentUserInfo(userdto);
		return dto;
		// return "Hello From Inbox!";

	}

	@RequestMapping(value = "/actions", method = RequestMethod.POST, produces = "application/json")
	public ResponseMessage taskActions(@RequestBody ActionDto dto) {
		return workFlowAction.taskAction(dto);
	}

	@RequestMapping(value = "/filterdetail", method = RequestMethod.POST, produces = "application/json")
	public WorkboxResponseDto getWorkboxFilterData(@RequestBody WorkboxRequestDto taskRequest) {
		return workbox.getWorkboxFilterData(taskRequest);

	}

	// @RequestMapping(value = "/getProcess", method = RequestMethod.GET,
	// produces = "application/json")
	// public ProcessEventsDto getProcessById(@RequestParam("processID") String
	// id) {
	// try {
	// return persistDataService.getProcessById(id);
	// } catch (ParseException e) {
	//
	// e.printStackTrace();
	// return new ProcessEventsDto();
	// }
	// }
	@RequestMapping(value = "/processNames")
	public List<SlaProcessNameListDto> getProcessNames() {
		List<SlaProcessNameListDto> list = new ArrayList<>();
		list = workbox.getProcessNames();
		// StringListResponseDto dto = new StringListResponseDto();

		return list;

	}

	@RequestMapping(value = "/completed", method = RequestMethod.GET)
	public WorkboxResponseDto getWorkboxCompletedFilterData(
			@RequestParam(value = "processName", defaultValue = "") String processName,
			@RequestParam(value = "period", defaultValue = "") String period,
			@RequestParam(value = "requestId", defaultValue = "") String requestId,
			@RequestParam(value = "createdBy", defaultValue = "") String createdBy,
			@RequestParam(value = "createdAt", defaultValue = "") String createdAt,
			@RequestParam(value = "completedAt", defaultValue = "") String completedAt,
			@RequestParam(value = "skipCount", defaultValue = "0") Integer skipCount,
			@RequestParam(value = "maxCount", defaultValue = "5") Integer maxCount,
			@RequestParam(value = "page", defaultValue = "1") Integer page) {

		return workbox.getWorkboxCompletedFilterData(processName, requestId, createdBy, createdAt, completedAt, period,
				skipCount, maxCount, page);
	}

	@RequestMapping(value = "/tracking", method = RequestMethod.GET)
	public TrackingResponse getTrackingResults() {
		return workbox.getTrackingResults();
	}

	@Autowired
	ProcessEventsDao processEvents;
	@Autowired
	TaskEventsDao taskEvents;
	@Autowired
	TaskOwnersDao ownerEvents;

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String adminParsing() {
		AdminParseResponse adminParseResponse = new AdminParse().parseAdminTasks("");
		System.err.println("Persisting Start : " + System.currentTimeMillis());
		processEvents.saveOrUpdateProcesses(adminParseResponse.getProcesses());
		taskEvents.saveOrUpdateTasks(adminParseResponse.getTasks());
		ownerEvents.saveOrUpdateOwners(adminParseResponse.getOwners());
		System.err.println("Persisting End : " + System.currentTimeMillis());
		return "Success";
	}

	@RequestMapping(value = "/updateData", method = RequestMethod.GET)
	public void updateData() {
		eventsUpdate.updateEvents();
	}

	@RequestMapping(value = "/getStatistics", method = RequestMethod.GET)
	public Statistics getStatistics() {
		return new Statistics(workbox.getStatistics().getSessionOpenCount(),
				workbox.getStatistics().getSessionCloseCount());
	}

	class Statistics {

		public Statistics(long openSessions, long closedSessions) {
			super();
			this.openSessions = openSessions;
			this.closedSessions = closedSessions;
		}

		private long openSessions;
		private long closedSessions;

		public long getOpenSessions() {
			return openSessions;
		}

		public void setOpenSessions(long openSessions) {
			this.openSessions = openSessions;
		}

		public long getClosedSessions() {
			return closedSessions;
		}

		public void setClosedSessions(long closedSessions) {
			this.closedSessions = closedSessions;
		}

		@Override
		public String toString() {
			return "Statistics [openSessions=" + openSessions + ", closedSessions=" + closedSessions + "]";
		}
	}
	//
	// public static void main(String[] args) {
	//// ActionDto actionDto = new ActionDto();
	//// actionDto.setActionType("Claim");
	//// actionDto.setIsAdmin(true);
	//// actionDto.setProcessor("P000035");
	//// actionDto.setInstanceList(Arrays.asList("39cf3df1-bfe1-11e8-88e9-00163e643e48"));
	//
	// WorkboxRequestDto req = new WorkboxRequestDto();
	// TaskOwnersDto owner = new TaskOwnersDto();
	// owner.setTaskOwner("P000035");
	// req.setCurrentUserInfo(owner);
	//
	// ApplicationContext applicationContext = new
	// AnnotationConfigApplicationContext(SpringConfiguration.class);
	// WorkboxRest workboxRest = applicationContext.getBean(WorkboxRest.class);
	// System.out.println(workboxRest.getWorkboxFilterData(req));
	// ((ConfigurableApplicationContext) applicationContext).close();
	// }

	/*
	 * @RequestMapping(value = "/saveData", method = RequestMethod.GET) public
	 * ResponseMessage persistDb() { return persistDataService.PeristToDb(); }
	 */

	// @RequestMapping(value = "/getUserDetails", method = RequestMethod.GET)
	// public Map<String, TaskOwnersDto> getUser(@RequestParam("groupName")
	// String grpName) {
	// return persistDataService.getGroupUserDetails(grpName, null);
	// }

	// Parsing from SCP odata url
	/*
	 * List<TaskEventsDto> taskList=persistODataService.getTaskList();
	 * WorkboxResponseDto response=new WorkboxResponseDto(); List<WorkBoxDto>
	 * tasks=new ArrayList<>(); for(TaskEventsDto task:taskList){ WorkBoxDto
	 * workboxdto=new WorkBoxDto(); if(task.getEventId()!=null)
	 * workboxdto.setTaskId(task.getEventId()); if(task.getProcessId()!=null)
	 * workboxdto.setProcessId(task.getProcessId());
	 * if(task.getDescription()!=null)
	 * workboxdto.setTaskDescription(task.getDescription());
	 * if(task.getSubject()!=null) workboxdto.setSubject(task.getSubject());
	 * if(task.getName()!=null) workboxdto.setName(task.getName());
	 * if(task.getStatus()!=null) workboxdto.setStatus(task.getStatus());
	 * if(task.getCurrentProcessor()!=null)
	 * workboxdto.setTaskOwner(task.getCurrentProcessor());
	 * if(task.getCreatedAt()!=null)
	 * workboxdto.setCreatedAt(task.getCreatedAt().toString());
	 * if(task.getCompletedAt()!=null)
	 * workboxdto.setCompletedAt(task.getCompletedAt().toString());
	 * if(task.getCompletionDeadLine()!=null)
	 * workboxdto.setSla(task.getCompletionDeadLine().toString());
	 * if(task.getProcessName()!=null)
	 * workboxdto.setProcessName(task.getProcessName());
	 * 
	 * tasks.add(workboxdto);
	 * 
	 * } response.setWorkBoxDtos(tasks); return response;
	 */
	/*
	 * String url=
	 * "https://bpmworkflowruntimea2d6007ea-kbniwmq1aj.hana.ondemand.com/workflow-service/rest/v1/task-instances?";
	 * if(status.equalsIgnoreCase("COMPLETED")) url+="status=COMPLETED"; else
	 * url+="status=RESERVED&&status=READY";
	 * 
	 * org.json.JSONArray json= util.callRestServiceForArray(url,null,"GET",
	 * "application/json", true); WorkboxResponseDto response=new
	 * WorkboxResponseDto(); List<WorkBoxDto> tasks=new ArrayList<>();
	 * if(json!=null&&json.length()!=0) for(int i=0;i<json.length();i++){
	 * JSONObject task=json.getJSONObject(i); WorkBoxDto dto=new WorkBoxDto();
	 * dto.setCompletedAt(task.optString("completedAt"));
	 * dto.setCreatedAt(task.optString("createdAt"));
	 * dto.setTaskDescription(task.optString("description"));
	 * dto.setTaskId(task.optString("id"));
	 * 
	 * dto.setTaskOwner(task.optString("processor"));
	 * dto.setStatus(task.optString("status"));
	 * dto.setSubject(task.optString("subject"));
	 * dto.setStartedBy(task.optString("createdBy")); tasks.add(dto); }
	 * response.setResponseMessage(new ResponseMessage("SUCESS","200",""));
	 * response.setWorkBoxDtos(tasks); return response;
	 */

	// @RequestMapping(value = "/taskdetail/{taskId}", method =
	// RequestMethod.GET, produces = "application/json")
	// public String getWorkboxTaskDetails(@PathVariable() String taskId) {
	// // WorkboxResponseDto response = new WorkboxResponseDto();
	// String url = PMCConstant.REQUEST_URL_INST +
	// "workflow-service/rest/v1/task-instances/";
	// url += taskId + "/context";
	// JSONObject jsonresponse = util.callRestService(url, null, "GET",
	// "application/json", true);
	// return jsonresponse.toString();
	// }
}
