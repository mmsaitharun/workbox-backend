package oneapp.workbox.services.util;

/**
 * @author Saurabh
 *
 */
public interface PMCConstant {
	String ORDER_TYPE_CREATED_AT = "createdAt";
	String ORDER_TYPE_SLA_DUE_DATE = "dueDate";
	String ORDER_BY_ASC = "ASC";
	String ORDER_BY_DESC = "DESC";
	String SEARCH_ALL = "ALL";
	String SEARCH_SMALL_ALL = "All";
	String PROCESS_STATUS_IN_PROGRESS = "IN_PROGRESS";
	String GRAPH_TREND_MONTH = "month";
	String GRAPH_TREND_WEEK = "week";
	String WEEK_PREFIX = "Week";
	String STATUS_ALL = "ALL";
	String TASK_STATUS_RESERVED = "RESERVED";
	String TASK_STATUS_READY = "READY";
	String PROCESS_AGING_REPORT = "process aging";
	String TASK_AGING_REPORT = "task aging";
	String USER_NAME = "User Name";
	String PROCESS_NAME_LABEL = "Process Name";
	String PROCESS_TOTAL = "Total";
	int WEEK_RANGE = 7;
	int MONTH_RANGE = 30;
	int MONTH_INTERVAL = 6;
	int COMPLETED_RANGE = 30;
	String SEARCH_RESERVED = "RESERVED";
	String SEARCH_READY = "READY";
	String UESR_PROCESS_GRAPH_MONTH_FORMATE = "dd MMM";
	String PMC_DATE_FORMATE = "dd MMM yyyy";
	int WEEK_INTERVAL = 1;
	String TASK_CREATED_FORMATE = "YYYY-MM-dd hh:mm:ss.SSS";
	String DETAIL_DATE_FORMATE = "dd MMM YYYY hh:mm:ss";
	String DETAILDATE_AMPM_FORMATE = "dd MMM YYYY hh:mm:ss a";
	int PAGE_SIZE = 20;
	String SEARCH_COMPLETED = "COMPLETED";
	String USER_TASK_STATUS_GRAPH = "task Status Graph";
	String TASK_COMPLETED = "COMPLETED";
	String TASK_INPROGRESS = "IN_PROGRESS";
	String UPDATE = "Update";
	String CREATE = "Create";
	String DELETE = "Delete";
	String STATUS_FAILURE = "FAILURE";
	String STATUS_SUCCESS = "SUCCESS";
	String NO_RESULT = "NO RESULT FOUND";
	String INTERNAL_ERROR = "Internal Error";
	String SEND_NOTIFICATION = "Send Notification";
	String REMIND_ME = "Remind Me";
	String USER_TASK_REPORT = "User workload";
	String PROCESS_TRACKER = "Process Aeging";
	String TASK_AEGING = "Task Aeging";
	String REPORT_EXCEL = "Excel";
	String REPORT_PDF = "PDF";
	String REPORT = "PMC Report";
	String PROCESS_BY_DURATION = "Process By Duration";
	String TASK_MANAGER = "Task Manager";
	int ARCHIVE_DAY = 0;
	String DAYS = "days";
	String HOURS = "hours";
	String MINUTES = "minute";

	/* User Id And Password For Wsdl Access */

	String WBuserId = "INC00695";
	String WBpassword = "Password@3";

	/* Add for consuming odata services */

	String HTTP_METHOD_PUT = "PUT";
	String HTTP_METHOD_POST = "POST";
	String HTTP_METHOD_GET = "GET";
	String HTTP_METHOD_PATCH = "PATCH";

	String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	String HTTP_HEADER_ACCEPT = "Accept";

	String APPLICATION_JSON = "application/json";
	String APPLICATION_XML = "application/xml";
	String APPLICATION_ATOM_XML = "application/atom+xml";
	String APPLICATION_FORM = "application/x-www-form-urlencoded";
	String METADATA = "$metadata";
	String COUNT = "$count";

	String SEPARATOR = "/";

	boolean PRINT_RAW_CONTENT = true;

	String ECC_URL = "http://sthcigwdq1.kaust.edu.sa:8005/sap/opu/odata/IWPGW/TASKPROCESSING;mo;v=2/";
	String BPM_URL = "http://sap.bpm.host:80/bpmodata/tasks.svc/";
	// String BPM_URL ="http://10.120.28.214:50000/bpmodata/tasks.svc/";
	String WF_URL = "";

	/* Added when destinations are added */

	String BPM_DEST = "workboxbpm";
	String BPM_LOCATION = "houston";
	String ON_PREMISE_PROXY = "OnPremise";

	/* Added for Task Management */

	String NOT_OWNER = "Not a Owner";

	String READ = "Read";
	String SUCCESS = "SUCCESS";
	String FAILURE = "FAILURE";

	String EXISTS = "EXISTS";
	String NOTEXIST = "NOT_EXIST";

	String CREATED_SUCCESS = "created successfully";
	String UPDATE_SUCCESS = "updated successfully";
	String DELETE_SUCCESS = "deleted successfully";
	String READ_SUCCESS = "Data fetched successfully";

	String CREATE_FAILURE = "creation failed";
	String UPDATE_FAILURE = "updation failed";
	String DELETE_FAILURE = "deletion failed";
	String READ_FAILURE = "Data fetch failed";

	String CODE_FAILURE = "1";
	String CODE_SUCCESS = "0";

	Boolean isEnabled = true;
	Boolean disabled = false;
	String SUBSTITUTING = "SUBSTITUTING";
	String SUBSTITUTED = "SUBSTITUTED";

//	String REQUEST_URL_INST = "https://bpmworkflowruntimea2d6007ea-kbniwmq1aj.hana.ondemand.com/workflow-service/rest/v1/";
//	String REQUEST_BASE_URL_TC = "https://bpmworkflowruntimea2d6007ea-kbniwmq1aj.hana.ondemand.com/workflow-service/odata/tcm/";
 	
	String REQUEST_URL_INST = "https://bpmworkflowruntimea2d6007ea-x5qv5zg6ns.hana.ondemand.com/workflow-service/rest/v1/";
	String REQUEST_BASE_URL_TC = "https://bpmworkflowruntimea2d6007ea-x5qv5zg6ns.hana.ondemand.com/workflow-service/odata/tcm/";
	
	String TASK_COLLECTION_RELATIVE_URL = "TaskCollection?$format=json";

	String SAML_HEADER_KEY_TC = "samlHeaderKeyTC";
	String SAML_HEADER_KEY_TI = "samlHeaderKeyTI";

	/* Added for generating standard header for inbox table */
	String STANDARD_HEADER = "STANDARD";
	String CUSTOM_HEADER = "CUSTOM";

	String FORWARD_TASK = "FORWARD";
	String SUBSTITUTE_TASK = "SUBSTITUTION";
	String GROUP_NAME = "workbox";

	String SEARCH_STATUS = "STATUS";
	String SEARCH_PROCESS = "PROCESS";

	String TOTAL_ACTIVE_TASK = "totalActiveTask";
	String TOTAL_ACTIVE_TASK_GRAPH = "totalActiveTasks";
	String USER_WORK_ITEM_COUNT_GRAPH = "userWorkItemCount";
	String OPEN_TASK = "openTask";
	String PENDING_TASK = "pendingTask";
	String SLA_BREACHED_TASK = "slaBreachedTask";
	String MY_TASK = "myTask";

	String COMPLETED_WITH_SLA = "Completed On Time";
	String COMPLETED_WITHOUT_SLA = "Completed After SLA";
	String RESERVED_WITH_SLA = "In Progress On Time";
	String RESERVED_WITHOUT_SLA = "In Progress After SLA";
	String READY_WITH_SLA = "New On Time";
	String READY_WITHOUT_SLA = "New After SLA";

	String INBOX_TYPE_GROUP_TASK = "groupTask";
	String INBOX_TYPE_MY_TASK = "myTask";

	String REPORT_FILTER_IN_TIME = "inTime";
	String REPORT_FILTER_CRITICAL = "critical";
	String REPORT_FILTER_SLA_BREACHED = "slaBreached";
	String REPORT_FILTER_PENDING = "pending";
	String REPORT_FILTER_WITH_SLA = "withSla";
	String TASK_COMPLETION_TREND_LIST = "taskCompletionTrendList";

	String NEW_TASK = "newTask";
	String ALL_TASK = "allTask";
	String ADD_USER = "addUser";
	String REMOVE_USER = "removeUser";

	String ACTION_TYPE_CLAIM = "claim";
	String ACTION_TYPE_RELEASE = "release";
	String ACTION_TYPE_FORWARD = "forward";
	
	String CLAIM_SUCCESS = "claimed successfully";
	String RELEASE_SUCCESS = "released successfully";
	String FORWARD_SUCCESS = "forwarded successfully";
	
	String CLAIM_FAILURE = "claim failed";
	String RELEASE_FAILURE = "release failed";
	String FORWARD_FAILURE = "forward failed";
	
	String SUBSTITUTION_SCHEDULER_TYPE="Scheduler";
	String SUBSTITUTION_INSTANT_TYPE="instant";
	
	/* Cross Configuration References */

	String ADMIN_GROUP_CONFIG_REF = "1";
	
	String WF_BASIC_USER = "P1099205";
	String WF_BASIC_PASS = "Ranjit30";
}
