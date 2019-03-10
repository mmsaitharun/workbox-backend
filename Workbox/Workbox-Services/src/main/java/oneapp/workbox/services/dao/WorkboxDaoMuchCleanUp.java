package oneapp.workbox.services.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import oneapp.workbox.services.dto.HeaderDto;
import oneapp.workbox.services.dto.InboxTasksHeaderDto;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.RestResponse;
import oneapp.workbox.services.dto.WorkBoxDto;
import oneapp.workbox.services.dto.WorkboxResponseDto;
import oneapp.workbox.services.entity.CustomAttributeTemplate;
import oneapp.workbox.services.entity.CustomAttributeValue;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.RestUtil;
import oneapp.workbox.services.util.ServicesUtil;

@Repository
@Transactional
public class WorkboxDaoMuchCleanUp {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Autowired
	CustomAttributeDao customAttributeDao;

	@Autowired
	ObjectMapper objectMapper;

	public WorkboxResponseDto getFilterData(String requestId, List<String> processName, String createdBy,
			String createdAt, String status, String orderBy, String orderType, int skipCount, int maxCount, int page,
			Boolean isChatBot, List<CustomAttributeTemplate> customDto, String taskOwner, Boolean isAdmin,
			String inboxType, String reportFilter, Boolean isCriticalExist, String completedDate, String graphName) {

		WorkboxResponseDto workboxResponseDto = new WorkboxResponseDto();
		ResponseMessage responseMessage = new ResponseMessage();
		ArrayNode arrayNode = objectMapper.createArrayNode();
		List<WorkBoxDto> workBoxDtos = null;
		HeaderDto headerDto = null;
		InboxTasksHeaderDto inboxHeaderDto = new InboxTasksHeaderDto();
		String reportNavigationQuery = "";
		String orderQuery = "";
		String filterQuery = "";
		String inboxTypeQuery = "";
		
		// Change later
		{
			
			taskOwner = "P1099205";
			
		}

		List<HeaderDto> headers = getStandardHeaders(
				customAttributeDao.getCustomAttributeTemplates(PMCConstant.STANDARD_HEADER, null, true),
				PMCConstant.STANDARD_HEADER);

		String selectQuery = " SELECT DISTINCT PE.REQUEST_ID, PE.NAME AS PROCESS_NAME, TE.EVENT_ID, TE.DESCRIPTION, TE.NAME AS TASK_NAME, TE.SUBJECT, "
				+ " PE.STARTED_BY, TE.CREATED_AT, TE.STATUS, TE.CUR_PROC, "
				+ " TS.SLA, TE.PROCESS_ID, TE.URL AS TASK_URL, TE.COMP_DEADLINE, TE.FORWARDED_BY, TE.FORWARDED_AT, "
				+ " PCT.PROCESS_DISPLAY_NAME, IA.IS_CLAIMED, IA.IS_RELEASED, IA.USER_CLAIMED, IA.MODIFIED_AT, PCT.URL AS DETAIL_URL ";

		String commonJoinQuery = " FROM TASK_EVENTS TE INNER JOIN PROCESS_EVENTS PE ON TE.PROCESS_ID = PE.PROCESS_ID "
				+ " INNER JOIN TASK_OWNERS TW ON TE.EVENT_ID = TW.EVENT_ID "
				+ " INNER JOIN INBOX_ACTIONS IA ON TE.EVENT_ID = IA.TASK_ID "
				+ " INNER JOIN PROCESS_CONFIG_TB PCT ON PE.NAME = PCT.PROCESS_NAME "
				+ " LEFT OUTER JOIN CUSTOM_ATTR_VALUES CAV ON TE.EVENT_ID = CAV.TASK_ID "
				+ " LEFT OUTER JOIN TASK_SLA TS ON TE.NAME = TS.TASK_DEF";

		String commonConditionQuery = " WHERE (PE.STATUS <> 'CANCELED' AND PE.STATUS <> 'COMPLETED') ";

		if (!ServicesUtil.isEmpty(requestId)) {
			filterQuery += " AND PE.REQUEST_ID LIKE '%" + requestId + "%' ";
		}

		if (!ServicesUtil.isEmpty(createdBy)) {
			filterQuery += " AND PE.STARTED_BY = '" + createdBy + "' ";
		}

		if (!ServicesUtil.isEmpty(createdAt)) {
			filterQuery += " AND TO_CHAR(CAST(TE.CREATED_AT AS DATE),'MM/DD/YYYY')= '" + createdAt + "' ";
		}

		if (ServicesUtil.isEmpty(reportFilter)) {
			if (!ServicesUtil.isEmpty(status)) {
				filterQuery += " AND " + getStatusString(status, taskOwner) + " ";
			} else {
				filterQuery += " AND TE.STATUS <> 'COMPLETED' ";
			}

			if (!ServicesUtil.isEmpty(taskOwner) && isAdmin.equals(false))
				filterQuery += " AND (((IA.IS_RELEASED = TRUE AND TW.TASK_OWNER = '" + taskOwner
						+ "')) OR (IA.IS_CLAIMED = TRUE AND UPPER(IA.USER_CLAIMED) = UPPER('" + taskOwner + "')) AND UPPER(TW.TASK_OWNER) = UPPER('"
						+ taskOwner + "') ";
			
			if(!ServicesUtil.isEmpty(status) && PMCConstant.INBOX_TYPE_GROUP_TASK.equals(inboxType)) {
				filterQuery += " OR (UPPER(IA.USER_CLAIMED) = UPPER('"+taskOwner+"') AND IA.IS_CLAIMED = true) ";
			}
			
			filterQuery += " ) ";
		}

		if (!ServicesUtil.isEmpty(processName) && processName.size() > 0
				&& !PMCConstant.SEARCH_ALL.equalsIgnoreCase(processName.get(0))) {
			filterQuery += " AND TE.PROC_NAME IN ( ";
			for (String process : processName) {
				filterQuery += "'" + process + "',";
			}
			filterQuery = filterQuery.substring(0, filterQuery.length() - 1);
			filterQuery += ") ";
		}

		if (!ServicesUtil.isEmpty(inboxType) && !ServicesUtil.isEmpty(isAdmin) && ServicesUtil.isEmpty(customDto))
			inboxTypeQuery = getInboxTypeQuery(inboxType, isAdmin, taskOwner, reportFilter);
		
		if (!ServicesUtil.isEmpty(reportFilter) && !ServicesUtil.isEmpty(isCriticalExist))
			reportNavigationQuery = reportNavigation(reportFilter, taskOwner, isCriticalExist, completedDate,
					graphName);

		if (ServicesUtil.isEmpty(orderType) && ServicesUtil.isEmpty(orderBy))
			orderQuery += " ORDER BY 8 DESC";

		String countQuery = "SELECT COUNT(*) FROM ( " + selectQuery + commonJoinQuery + commonConditionQuery
				+ filterQuery + reportNavigationQuery + inboxTypeQuery + orderQuery + " )";

		Query countQry = this.getSession().createSQLQuery(countQuery.trim());
		BigDecimal taskCount = ServicesUtil.getBigDecimal(countQry.uniqueResult());

		String dataQuery = selectQuery + commonJoinQuery + commonConditionQuery + filterQuery + reportNavigationQuery + inboxTypeQuery
				+ orderQuery;

		System.err.println("DataQuery : " + dataQuery);
		Query dataQry = this.getSession().createSQLQuery(dataQuery.trim());

		if (!ServicesUtil.isEmpty(maxCount) && maxCount > 0 && !ServicesUtil.isEmpty(skipCount) && skipCount >= 0) {
			int first = skipCount;
			int last = maxCount;
			dataQry.setFirstResult(first);
			dataQry.setMaxResults(last);
		}
		if (!ServicesUtil.isEmpty(page) && page > 0) {
			int first = (page - 1) * PMCConstant.PAGE_SIZE;
			int last = PMCConstant.PAGE_SIZE;
			dataQry.setFirstResult(first);
			dataQry.setMaxResults(last);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = dataQry.list();

		if (ServicesUtil.isEmpty(resultList)) {
			responseMessage.setMessage(PMCConstant.NO_RESULT);
			responseMessage.setStatus(PMCConstant.SUCCESS);
			responseMessage.setStatusCode(PMCConstant.CODE_SUCCESS);
		} else {
			workBoxDtos = getWorkBoxDto(resultList, processName, arrayNode);
			if (!ServicesUtil.isEmpty(processName) && processName.size() == 1) {
				List<CustomAttributeTemplate> customHeaders = customAttributeDao
						.getCustomAttributeTemplates(processName.get(0), null, true);
				if (!ServicesUtil.isEmpty(customHeaders) && customHeaders.size() > 0) {
					for (CustomAttributeTemplate customAttributeTemplate : customHeaders) {
						headerDto = new HeaderDto();
						headerDto.setType("CUSTOM");
						headerDto.setKey(customAttributeTemplate.getKey());
						headerDto.setName(customAttributeTemplate.getLabel());
						if (ServicesUtil.isEmpty(headers)) {
							headers = new ArrayList<HeaderDto>();
							headers.add(headerDto);
						} else {
							headers.add(headerDto);
						}
					}
				}
			} else if(ServicesUtil.isEmpty(processName)){
				List<CustomAttributeTemplate> customHeaders = customAttributeDao
						.getCustomAttributeTemplates("material_definition_wf", null, true);
				if (!ServicesUtil.isEmpty(customHeaders) && customHeaders.size() > 0) {
					for (CustomAttributeTemplate customAttributeTemplate : customHeaders) {
						headerDto = new HeaderDto();
						headerDto.setType("CUSTOM");
						headerDto.setKey(customAttributeTemplate.getKey());
						headerDto.setName(customAttributeTemplate.getLabel());
						if (ServicesUtil.isEmpty(headers)) {
							headers = new ArrayList<HeaderDto>();
							headers.add(headerDto);
						} else {
							headers.add(headerDto);
						}
					}
				}
			}
			responseMessage.setStatus("Success");
			responseMessage.setStatusCode("0");
			responseMessage.setMessage("Task Details Fetched Successfully");
		}

		workboxResponseDto.setWorkBoxDtos(workBoxDtos);
		inboxHeaderDto.setHeaders(headers);
		workboxResponseDto.setHeaderDto(inboxHeaderDto);
		workboxResponseDto.setCustomAttributes(arrayNode);
		workboxResponseDto.setPageCount(PMCConstant.PAGE_SIZE);
		workboxResponseDto.setCount(taskCount);

		workboxResponseDto.setResponseMessage(responseMessage);

		return workboxResponseDto;
	}

	private List<HeaderDto> getStandardHeaders(List<CustomAttributeTemplate> standardHeaders, String headerType) {
		HeaderDto headerDto = null;
		List<HeaderDto> headers = null;
		if (!ServicesUtil.isEmpty(standardHeaders) && standardHeaders.size() > 0) {
			headers = new ArrayList<HeaderDto>();
			for (CustomAttributeTemplate customAttributeTemplate : standardHeaders) {
				headerDto = new HeaderDto();
				headerDto.setType("STANDARD");
				headerDto.setKey(customAttributeTemplate.getKey());
				headerDto.setName(customAttributeTemplate.getLabel());
				headers.add(headerDto);
			}
		}
		return headers;
	}

	private List<WorkBoxDto> getWorkBoxDto(List<Object[]> resultList, List<String> processName, ArrayNode arrayNode) {
		ObjectNode objectNode = null;
		SimpleDateFormat dateFormatter = null;
		List<WorkBoxDto> workBoxDtos = null;
		WorkBoxDto workBoxDto = null;
		if (!ServicesUtil.isEmpty(resultList) && resultList.size() > 0) {
			workBoxDtos = new ArrayList<WorkBoxDto>();
			dateFormatter = new SimpleDateFormat("dd-MMM-yy hh:mm:ss a");
			for (Object[] obj : resultList) {
				workBoxDto = new WorkBoxDto();

				workBoxDto.setRequestId(ServicesUtil.asString(obj[0]));
				workBoxDto.setProcessName(ServicesUtil.asString(obj[1]));
				workBoxDto.setTaskId(ServicesUtil.asString(obj[2]));
				workBoxDto.setTaskDescription(ServicesUtil.asString(obj[3]));
				workBoxDto.setName(ServicesUtil.asString(obj[4]));
				workBoxDto.setSubject(ServicesUtil.asString(obj[5]));
				workBoxDto.setStartedBy(ServicesUtil.asString(obj[6]));
				workBoxDto
				.setCreatedAt(obj[7] == null ? null : dateFormatter.format(ServicesUtil.resultAsDate(obj[7])));
				workBoxDto.setSla(ServicesUtil.asString(obj[10]));
				workBoxDto.setProcessId(ServicesUtil.asString(obj[11]));
				workBoxDto.setDetailURL(ServicesUtil.asString(obj[12]));
				workBoxDto.setSlaDisplayDate(
						obj[13] == null ? null : dateFormatter.format(ServicesUtil.resultAsDate(obj[13])));
				if (!ServicesUtil.isEmpty(obj[13]) && !ServicesUtil.isEmpty(obj[7])) {
					Calendar created = ServicesUtil.timeStampToCal(obj[7]);
					Calendar slaDate = ServicesUtil.timeStampToCal(obj[13]);
					String timeLeftString = ServicesUtil.getSLATimeLeft(slaDate);
					if (timeLeftString.equalsIgnoreCase("Breach")) {
						workBoxDto.setBreached(true);
					} else {
						workBoxDto.setBreached(false);
						workBoxDto.setTimeLeftDisplayString(timeLeftString);
						workBoxDto.setTimePercentCompleted(ServicesUtil.getPercntTimeCompleted(created, slaDate));
					}
					Calendar slaActualDate = Calendar.getInstance();
					slaActualDate.setTime(slaDate.getTime());
					slaDate.add(Calendar.HOUR_OF_DAY, 5);
					Calendar currentTime = Calendar.getInstance();
					if (currentTime.before(slaActualDate) && currentTime.after(slaDate)) {
						workBoxDto.setCritical(true);
					} else {
						workBoxDto.setCritical(false);
					}
				}
				workBoxDto.setForwardedBy(ServicesUtil.asString(obj[14]));
				workBoxDto.setForwardedAt(
						obj[15] == null ? null : dateFormatter.format(ServicesUtil.resultAsDate(obj[15])));
				workBoxDto.setProcessDisplayName(obj[16] == null ? (String) obj[1] : (String) obj[16]);
				if (!ServicesUtil.isEmpty(processName) && processName.size() == 1) {
					//					List<CustomAttributeValue> custAttributes = customAttributeDao
					//							.getCustomAttributes(workBoxDto.getTaskId());
					List<CustomAttributeValue> custAttributes = getCustomAttributes(workBoxDto.getTaskId());
					if (!ServicesUtil.isEmpty(custAttributes)) {
						objectNode = objectMapper.createObjectNode();
						for (CustomAttributeValue customAttributeValue : custAttributes) {
							objectNode.put(customAttributeValue.getKey(), customAttributeValue.getAttributeValue());
						}
					}
				} else if(ServicesUtil.isEmpty(processName)) {
					List<CustomAttributeValue> custAttributes = getCustomAttributes(workBoxDto.getTaskId());
					if (!ServicesUtil.isEmpty(custAttributes)) {
						objectNode = objectMapper.createObjectNode();
						for (CustomAttributeValue customAttributeValue : custAttributes) {
							objectNode.put(customAttributeValue.getKey(), customAttributeValue.getAttributeValue());
						}
					}
				}
				workBoxDto.setStatus(getStatus(obj[17], obj[18]));
				workBoxDto.setDetailURL(ServicesUtil.asString(obj[21]) + workBoxDto.getTaskId());

				arrayNode.add(objectNode);
				workBoxDtos.add(workBoxDto);
			}
		}
		return workBoxDtos;
	}

	private List<CustomAttributeValue> getCustomAttributes(String taskId) {
		String baseUrl = "https://bpmworkflowruntimea2d6007ea-x5qv5zg6ns.hana.ondemand.com/workflow-service/rest/v1/task-instances/";
		String relativeUrl = "/attributes";
		
		JSONArray jsonArray = null;
		JSONObject jsonObject = null;
		List<CustomAttributeValue> customValues = null;
		CustomAttributeValue customValue = null;
		
		RestResponse restResponse = RestUtil.callRestService(baseUrl + taskId + relativeUrl, null, null, PMCConstant.HTTP_METHOD_GET, PMCConstant.APPLICATION_JSON, true, null, null, null, null, null, null);
		if(restResponse.getResponseCode() >= 200 && restResponse.getResponseCode() < 400) {
			customValues = new ArrayList<>();
			Object attributeJson = restResponse.getResponseObject();
			jsonArray = (JSONArray) attributeJson;
			
			for(Object object : jsonArray) {
				
				customValue = new CustomAttributeValue();
				jsonObject = (JSONObject) object;
				
				customValue.setKey(jsonObject.optString("id"));
				customValue.setTaskId(taskId);
				customValue.setAttributeValue(jsonObject.optString("value"));
				
				customValues.add(customValue);
			}
		}
		
		return customValues;
	}

	private String getStatus(Object isClaimed, Object isReleased) {
		String taskStatus = null;
		if (Boolean.TRUE.equals(ServicesUtil.asBoolean(isClaimed))
				&& (ServicesUtil.isEmpty(isReleased) || Boolean.FALSE.equals(ServicesUtil.asBoolean(isReleased)))) {
			taskStatus = PMCConstant.TASK_STATUS_RESERVED;
		} else if (Boolean.TRUE.equals(ServicesUtil.asBoolean(isReleased))
				&& (ServicesUtil.isEmpty(isClaimed) || Boolean.FALSE.equals(ServicesUtil.asBoolean(isClaimed)))) {
			taskStatus = PMCConstant.TASK_STATUS_READY;
		}
		return taskStatus;
	}

	private String getStatusString(String status, String taskOwner) {
		String ret = "";
		if (PMCConstant.TASK_STATUS_READY.equalsIgnoreCase(status)) {
			ret = " IA.IS_RELEASED = TRUE";
		} else if (PMCConstant.TASK_STATUS_RESERVED.equalsIgnoreCase(status)) {
			ret = " IA.IS_CLAIMED = TRUE and UPPER(IA.USER_CLAIMED) = UPPER('" + taskOwner + "')";
		}
		return ret;
	}

	private String reportNavigation(String reportType, String userId, Boolean isCriticalExist, String date,
			String graphName) {
		String queryString = "";
		String statusQuery = "  AND TE.STATUS <> 'COMPLETED'  ";

		String slaDueDate = " TE.COMP_DEADLINE ";
		if (isCriticalExist) {
			slaDueDate = " ADD_SECONDS (TE.COMP_DEADLINE, - (60*30)) ";
		}

		switch (reportType) {

		case PMCConstant.REPORT_FILTER_IN_TIME:
			queryString += statusQuery + " AND CURRENT_TIMESTAMP < " + slaDueDate + " ";
			if (!ServicesUtil.isEmpty(userId) && PMCConstant.USER_WORK_ITEM_COUNT_GRAPH.equals(graphName))
				queryString += " AND UPPER(IA.USER_CLAIMED) = UPPER('" + userId + "') AND IA.IS_CLAIMED = TRUE ";
			break;
		case PMCConstant.REPORT_FILTER_CRITICAL:
			queryString += statusQuery + " AND CURRENT_TIMESTAMP > " + slaDueDate
					+ " AND CURRENT_TIMESTAMP < COMP_DEADLINE ";
			if (!ServicesUtil.isEmpty(userId) && PMCConstant.USER_WORK_ITEM_COUNT_GRAPH.equals(graphName))
				queryString += " AND UPPER(IA.USER_CLAIMED) = UPPER('" + userId + "') AND IA.IS_CLAIMED = TRUE ";

			break;
		case PMCConstant.REPORT_FILTER_SLA_BREACHED:
			queryString += statusQuery + " AND CURRENT_TIMESTAMP > TE.COMP_DEADLINE ";
			if (!ServicesUtil.isEmpty(userId) && PMCConstant.USER_WORK_ITEM_COUNT_GRAPH.equals(graphName))
				queryString += " AND UPPER(IA.USER_CLAIMED) = UPPER('" + userId + "') AND IA.IS_CLAIMED = TRUE ";

			break;
		case PMCConstant.REPORT_FILTER_WITH_SLA:
			queryString += statusQuery + " AND CURRENT_TIMESTAMP < TE.COMP_DEADLINE ";
			break;
		case PMCConstant.TOTAL_ACTIVE_TASK:
			queryString += " AND TE.STATUS <> 'COMPLETED' ";
			break;
		case PMCConstant.OPEN_TASK:
			queryString += " AND IA.IS_RELEASED = TRUE ";
			break;
		case PMCConstant.PENDING_TASK:
			queryString += " AND IA.IS_CLAIMED = TRUE ";
			break;
		case PMCConstant.SLA_BREACHED_TASK:
			queryString += " AND (TE.COMP_DEADLINE < TE.COMPLETED_AT AND TE.STATUS = 'COMPLETED')"
					+ " OR (CURRENT_TIMESTAMP > TE.COMP_DEADLINE AND IA.IS_CLAIMED = TRUE)"
					+ " OR (CURRENT_TIMESTAMP > TE.COMP_DEADLINE AND IA.IS_RELEASED = TRUE) ";
			break;
		case PMCConstant.COMPLETED_WITH_SLA:
			queryString += " AND TE.COMP_DEADLINE > TE.COMPLETED_AT AND TE.STATUS = 'COMPLETED'";
			break;
		case PMCConstant.COMPLETED_WITHOUT_SLA:
			queryString += " AND TE.COMP_DEADLINE < TE.COMPLETED_AT AND TE.STATUS = 'COMPLETED'";
			break;
		case PMCConstant.RESERVED_WITH_SLA:
			queryString += " AND CURRENT_TIMESTAMP < TE.COMP_DEADLINE AND IA.IS_CLAIMED = TRUE";
			break;
		case PMCConstant.RESERVED_WITHOUT_SLA:
			queryString += " AND CURRENT_TIMESTAMP > TE.COMP_DEADLINE AND IA.IS_CLAIMED = TRUE";
			break;
		case PMCConstant.READY_WITH_SLA:
			queryString += " AND TE.COMP_DEADLINE > CURRENT_TIMESTAMP AND IA.IS_RELEASED = TRUE ";
			break;
		case PMCConstant.READY_WITHOUT_SLA:
			queryString += " AND TE.COMP_DEADLINE < CURRENT_TIMESTAMP AND IA.IS_RELEASED = TRUE ";
			break;
		case PMCConstant.TASK_COMPLETION_TREND_LIST:
			String dateFrom = "";
			String dateTo = "";
			if (!ServicesUtil.isEmpty(date)) {
				if (date.contains("-")) {
					dateFrom = date;
					dateTo = ServicesUtil.getPreviousDay(dateFrom);
				} else {
					dateFrom = ServicesUtil.monthStartDate(ServicesUtil.getMonthInNumber(date));
					dateTo = ServicesUtil.monthEndDate(ServicesUtil.getMonthInNumber(date));
				}
			}
			queryString += " AND TE.COMPLETED_AT BETWEEN '" + dateFrom + "' AND '" + dateTo
					+ "' AND TE.STATUS = 'COMPLETED' ";
			break;
		default:
		}
		return queryString;
	}
//	
//	private String getInboxTypeQuery(String inboxType, Boolean isAdmin, String taskOwner, String reportFilter) {
//		String inboxTypeQuery = "";
//		if (!ServicesUtil.isEmpty(inboxType) && ServicesUtil.isEmpty(reportFilter)) {
//			if (PMCConstant.INBOX_TYPE_GROUP_TASK.equals(inboxType)) {
//				if (isAdmin.equals(false)) {
//					inboxTypeQuery = " AND TE.EVENT_ID IN (SELECT EVENT_ID FROM TASK_OWNERS WHERE TASK_OWNER='"
//							+ taskOwner + "' AND EVENT_ID IN (SELECT DISTINCT TO1.EVENT_ID FROM TASK_EVENTS TE1 JOIN"
//							+ " PROCESS_CONFIG_TB PC1 ON PC1.PROCESS_NAME=TE1.PROC_NAME JOIN TASK_OWNERS TO1 "
//							+ " ON TE1.EVENT_ID=TO1.EVENT_ID "
//							+ "WHERE ("+getStatusString(PMCConstant.TASK_STATUS_READY, taskOwner)+") "
//									+ "GROUP BY TO1.EVENT_ID HAVING COUNT(TO1.EVENT_ID)>1))";
//				}
//			} else if (PMCConstant.INBOX_TYPE_MY_TASK.equals(inboxType)) {
//				inboxTypeQuery = " AND (( "+getStatusString(PMCConstant.TASK_STATUS_RESERVED, taskOwner)+") "
////						+ "AND te.CUR_PROC = '" + taskOwner
////						+ "'"
//						+ " or te.event_id in (select event_id from task_owners where task_owner='" + taskOwner
//						+ "' and event_id in (select distinct to1.event_id from task_events te1 join PROCESS_CONFIG_TB pc1 on pc1.process_name =te1.proc_name join task_owners to1 on te1.event_id=to1.event_id where ("+getStatusString(PMCConstant.TASK_STATUS_READY, taskOwner)+") group by to1.event_id having count(to1.event_id)=1)))";
//			}
//		}
//		return inboxTypeQuery;
//	}
//	
	private String getInboxTypeQuery(String inboxType, Boolean isAdmin, String taskOwner, String reportFilter) {
		String inboxTypeQuery = "";
		if (!ServicesUtil.isEmpty(inboxType) && ServicesUtil.isEmpty(reportFilter)) {
			if (PMCConstant.INBOX_TYPE_GROUP_TASK.equals(inboxType)) {
				if (isAdmin.equals(false)) {
					inboxTypeQuery = " AND TE.EVENT_ID IN (SELECT EVENT_ID FROM TASK_OWNERS WHERE UPPER(TASK_OWNER)=UPPER('"
							+ taskOwner + "') AND EVENT_ID IN (SELECT DISTINCT TO1.EVENT_ID FROM TASK_EVENTS TE1 JOIN"
							+ " PROCESS_CONFIG_TB PC1 ON PC1.PROCESS_NAME=TE1.PROC_NAME JOIN TASK_OWNERS TO1 "
							+ " ON TE1.EVENT_ID=TO1.EVENT_ID " + "WHERE ("
							+ getStatusString(PMCConstant.TASK_STATUS_READY, taskOwner)
							+ ") GROUP BY TO1.EVENT_ID HAVING COUNT(TO1.EVENT_ID)>1)) " + "and (UPPER(tw.task_owner)=UPPER('"
							+ taskOwner + "') and tw.is_substituted is null ) ";
				}
			} else if (PMCConstant.INBOX_TYPE_MY_TASK.equals(inboxType)) {
				inboxTypeQuery = " AND (( " + getStatusString(PMCConstant.TASK_STATUS_RESERVED, taskOwner) + ") "
				// + "AND te.CUR_PROC = '" + taskOwner
				// + "'"
						+ " or te.event_id in (select event_id from task_owners where task_owner='" + taskOwner
						+ "' and event_id in (select distinct to1.event_id from task_events te1"
						+ " join PROCESS_CONFIG_TB pc1 on pc1.process_name =te1.proc_name "
						+ "join task_owners to1 on te1.event_id=to1.event_id where ("
						+ getStatusString(PMCConstant.TASK_STATUS_READY, taskOwner) + ") "
						+ "group by to1.event_id having count(to1.event_id)=1)))" + " or (UPPER(tw.task_owner)=UPPER('" + taskOwner
						+ "') and tw.is_substituted=1 ) ";
			}
		}
		return inboxTypeQuery;
	}


	public static void main(String[] args) {

//		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
//				SpringConfiguration.class);
//		WorkboxDaoMuchCleanUp workboxDaoMuchCleanUp = applicationContext.getBean(WorkboxDaoMuchCleanUp.class);
//		WorkboxResponseDto workboxResponseDto = workboxDaoMuchCleanUp.getFilterData(null, Arrays.asList("ALL"), null,
//				null, null, null, null, 0, 0, 1, null, null, "P000057", false, null, null, false, null, null);
//		System.out.println(workboxResponseDto);
//		applicationContext.close();
		short a = 0; // false
		short b = 1; // true
		System.out.println(new WorkboxDaoMuchCleanUp().getStatus(a, b));
//		System.out.println(new WorkboxDaoMuchCleanUp().getCustomAttributes("78376777-1e04-11e9-a01b-00163e7f9cbb"));

	}
}
