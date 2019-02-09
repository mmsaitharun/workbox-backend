package oneapp.workbox.services.dao;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import oneapp.workbox.services.dto.ManageTasksRequestDto;
import oneapp.workbox.services.dto.ProcessDetailsDto;
import oneapp.workbox.services.dto.ProcessDetailsResponse;
import oneapp.workbox.services.dto.ProcessEventsDto;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.UserDetailsDto;
import oneapp.workbox.services.dto.UserProcessDetailRequestDto;
import oneapp.workbox.services.entity.ProcessConfigDo;
import oneapp.workbox.services.entity.ProcessEventsDo;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;

@Repository("ProcessEventsDao")
@Transactional
public class ProcessEventsDao {
	
	@Autowired
	SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ProcessEventsDao.class);

	protected ProcessEventsDo importDto(ProcessEventsDto fromDto) {
		ProcessEventsDo entity = new ProcessEventsDo();
		if (!ServicesUtil.isEmpty(fromDto.getProcessId()))
			entity.setProcessId(fromDto.getProcessId());
		if (!ServicesUtil.isEmpty(fromDto.getName()))
			entity.setName(fromDto.getName());
		if (!ServicesUtil.isEmpty(fromDto.getStartedBy()))
			entity.setStartedBy(fromDto.getStartedBy());
		if (!ServicesUtil.isEmpty(fromDto.getStatus()))
			entity.setStatus(fromDto.getStatus());
		if (!ServicesUtil.isEmpty(fromDto.getSubject()))
			entity.setSubject(fromDto.getSubject());
		if (!ServicesUtil.isEmpty(fromDto.getCompletedAt()))
			entity.setCompletedAt(fromDto.getCompletedAt());
		if (!ServicesUtil.isEmpty(fromDto.getStartedAt()))
			entity.setStartedAt(fromDto.getStartedAt());
		if (!ServicesUtil.isEmpty(fromDto.getRequestId()))
			entity.setRequestId(fromDto.getRequestId());
		if (!ServicesUtil.isEmpty(fromDto.getStartedByDisplayName()))
			entity.setStartedByDisplayName(fromDto.getStartedByDisplayName());
		return entity;
	}

	protected ProcessEventsDto exportDto(ProcessEventsDo entity) {
		ProcessEventsDto processEventsDto = new ProcessEventsDto();
		if (!ServicesUtil.isEmpty(entity.getProcessId()))
			processEventsDto.setProcessId(entity.getProcessId());
		if (!ServicesUtil.isEmpty(entity.getName()))
			processEventsDto.setName(entity.getName());
		if (!ServicesUtil.isEmpty(entity.getStartedBy()))
			processEventsDto.setStartedBy(entity.getStartedBy());
		if (!ServicesUtil.isEmpty(entity.getStatus()))
			processEventsDto.setStatus(entity.getStatus());
		if (!ServicesUtil.isEmpty(entity.getSubject()))
			processEventsDto.setSubject(entity.getSubject());
		if (!ServicesUtil.isEmpty(entity.getCompletedAt()))
			processEventsDto.setCompletedAt(entity.getCompletedAt());
		if (!ServicesUtil.isEmpty(entity.getStartedAt()))
			processEventsDto.setStartedAt(entity.getStartedAt());
		if (!ServicesUtil.isEmpty(entity.getRequestId()))
			processEventsDto.setRequestId(entity.getRequestId());
		if (!ServicesUtil.isEmpty(entity.getStartedByDisplayName()))
			processEventsDto.setStartedByDisplayName(entity.getStartedByDisplayName());
		return processEventsDto;
	}

	public ProcessEventsDto getProcessDetail(String processId) {
		ProcessEventsDto processEventsDto = null;
		if (!ServicesUtil.isEmpty(processId)) {
			Query query = this.getSession()
					.createQuery("select pe from ProcessEventsDo pe where pe.processId =:processId");
			query.setParameter("processId", processId);
			ProcessEventsDo processEventsDo = (ProcessEventsDo) query.uniqueResult();
			if (!ServicesUtil.isEmpty(processEventsDo)) {
				query = this.getSession()
						.createQuery("select pe from ProcessConfigDo pe where pe.processName =:processName");
				query.setParameter("processName", processEventsDo.getName());
				processEventsDto = exportDto(processEventsDo);
				ProcessConfigDo processConfigDo = null;
				try {
					Object obj = query.uniqueResult();
					processConfigDo = (ProcessConfigDo) obj;
					if (!ServicesUtil.isEmpty(processConfigDo)
							&& !ServicesUtil.isEmpty(processConfigDo.getProcessDisplayName()))
						processEventsDto.setProcessDisplayName(processConfigDo.getProcessDisplayName());

				} catch (Exception e) {
					processEventsDto.setProcessDisplayName(processEventsDto.getName());
				}
			}
		}
		return processEventsDto;
	}
	
	public ProcessEventsDto getProcessByRequestAndName(String requestId,String processName){
		ProcessEventsDto processEventsDto=null;
		try{
			Criteria criteria=this.getSession().createCriteria(ProcessEventsDo.class);
			System.err.println("getProcessByRequestAndName---RequestId:"+requestId+"and process Name:"+processName);
			criteria.add(Restrictions.eq("requestId", requestId));
			criteria.add(Restrictions.eq("name",processName).ignoreCase());
			ProcessEventsDo processEventsDo=(ProcessEventsDo)criteria.uniqueResult();
			if (!ServicesUtil.isEmpty(processEventsDo)) {
				Query query = this.getSession()
						.createQuery("select pe from ProcessConfigDo pe where pe.processName =:processName");
				query.setParameter("processName", processEventsDo.getName());
				processEventsDto = exportDto(processEventsDo);
				ProcessConfigDo processConfigDo = null;
				try {
					Object obj = query.uniqueResult();
					processConfigDo = (ProcessConfigDo) obj;
					if (!ServicesUtil.isEmpty(processConfigDo)
							&& !ServicesUtil.isEmpty(processConfigDo.getProcessDisplayName()))
						processEventsDto.setProcessDisplayName(processConfigDo.getProcessDisplayName());

				} catch (Exception e) {
					processEventsDto.setProcessDisplayName(processEventsDto.getName());
				}
			}
			
			return processEventsDto;
		}
		catch(Exception e){
			System.err.println(e.getStackTrace().toString());
			return null;
			
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> getAllProcessName(){
		Criteria criteria = this.getSession().createCriteria(ProcessConfigDo.class);
		criteria.setProjection(Projections.property("processDisplayName"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<String> processNameList = (List<String>) criteria.list();
		return processNameList;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getProcessDetail(String userId, String processName, String requestId, String labelValue,
			String status, Integer page) {
		logger.error("[PMC] ProcessEventsDao getProcessDetail Started - with request - " + userId);
		String tempQuery = "";
		// int firstIndex = 0;
		// int lastIndex = 0;
		/*
		 * if (!ServicesUtil.isEmpty(page)) { firstIndex = PMCConstant.PAGE_SIZE
		 * * (page - 1) + 1; lastIndex = page * PMCConstant.PAGE_SIZE; }
		 */

		String paginationQuery = "";
		// "SELECT * FROM (SELECT a.*, rownum R_NUM FROM (";
		String query = paginationQuery
				+ "select DISTINCT(pe.PROCESS_ID) AS PROCESS_ID, pe.STARTED_AT AS STARTED_AT, pe.STARTED_BY AS STARTED_BY, pe.SUBJECT as SUBJECT, pe.REQUEST_ID As REQUEST_ID , pe.NAME AS PROCESS_NAME, pe.STARTED_BY_DISP AS STARTED_BY_DISP, pc.PROCESS_DISPLAY_NAME AS PROCESS_DISPLAY_NAME from task_owners tw left join task_events te on tw.event_id = te.event_id left join process_events pe on pe.process_id = te.process_id LEFT JOIN PROCESS_CONFIG_TB pc ON pc.PROCESS_NAME   = pe.NAME where tw.task_owner='"
				+ userId + "'";
		if (!ServicesUtil.isEmpty(processName) && !processName.equals(PMCConstant.SEARCH_ALL)) {
			tempQuery = tempQuery
					+ " and pe.PROCESS_ID IN (select D.process_id from PROCESS_EVENTS D where D.name IN(' "
					+ processName + "'))";
		}
		if (!ServicesUtil.isEmpty(requestId)) {
			tempQuery = tempQuery + " and pe.REQUEST_ID = '" + requestId + "'";
		}
		if (!ServicesUtil.isEmpty(labelValue)) {
			tempQuery = tempQuery + " and pe.SUBJECT like '%" + labelValue + "%'";
		}
		if (!ServicesUtil.isEmpty(status)) {
			if (PMCConstant.SEARCH_READY.equalsIgnoreCase(status)) {
				tempQuery = tempQuery + " and te.STATUS = '" + status + "'";
			} else if (PMCConstant.SEARCH_RESERVED.equalsIgnoreCase(status)) {
				tempQuery = tempQuery + " and te.STATUS = '" + status + "' and tw.IS_PROCESSED = 1";
			} else {
				tempQuery = tempQuery + " and (te.STATUS = '" + PMCConstant.TASK_STATUS_READY + "' or (te.STATUS = '"
						+ PMCConstant.TASK_STATUS_RESERVED + "' and tw.IS_PROCESSED = 1))";
			}
		}
		tempQuery = tempQuery + " and pe.status='" + PMCConstant.PROCESS_STATUS_IN_PROGRESS + "'";
		query = query + tempQuery + " order by 2 desc";

		/*
		 * if(lastIndex != 0){ paginationQuery = ")a WHERE ROWNUM <= " +
		 * lastIndex + ") WHERE R_NUM >= " + firstIndex; }else{ paginationQuery
		 * = ")a)"; }
		 */
		query = query + paginationQuery;

		if (!ServicesUtil.isEmpty(page) && page > 0) {
			int first = (page - 1) * PMCConstant.PAGE_SIZE;
			int last = PMCConstant.PAGE_SIZE;

			/* Commented for Pagination in HANA */
			// q.setFirstResult(first);
			// q.setMaxResults(last);

			query += " LIMIT " + last + " OFFSET " + first + "";
		}

		logger.error("get - " + query);
		Query q = this.getSession().createSQLQuery(query);

		List<Object[]> resultList = q.list();
		logger.error("[PMC] ProcessEventsDao getProcessDetail Ended - with resultList - " + resultList);

		return resultList;
	}

	public ProcessEventsDto saveOrUpdateProcessEvent(ProcessEventsDto dto) {
		try {
			this.getSession().saveOrUpdate(importDto(dto));
			return dto;
		} catch (Exception e) {
			logger.error("[pmc][processEventsDao][saveOrUpdateProcessEvent]:");
			return null;
		}

	}

	public void saveOrUpdateProcessList(ProcessEventsDto process) {
		if (!ServicesUtil.isEmpty(process) && !ServicesUtil.isEmpty(
				process.getProcessId())/* && processEvents.size() > 0 */) {
			// for(ProcessEventsDto process : processEvents) {
			Session session = this.getSession();
			try {
				// System.err.println("process persisting : "+process);
				if (!session.getStatistics().getEntityKeys().contains(process.getProcessId())) {
					this.getSession().saveOrUpdate(this.importDto(process));
				}
			} catch (Exception e) {
				System.err.println("Exception while converting dto to entity : " + e.getMessage());
			}
			// }
			// this.getSession().flush();
		}
	}

	@SuppressWarnings("unused")
	public void updateTaskEventWithProcessID(ProcessEventsDto process) {
		if (!ServicesUtil.isEmpty(process) && !ServicesUtil.isEmpty(process.gettId()) && !ServicesUtil
				.isEmpty(process.getProcessId())/* && processDtos.size() > 0 */) {
			// for(ProcessEventsDto process : processDtos) {
			try {
				String sql = "UPDATE TASK_EVENTS SET PROCESS_ID = '" + process.getProcessId() + "', PROC_NAME = '"
						+ process.getName() + "'  WHERE EVENT_ID = '" + process.gettId() + "'";
				int update = this.getSession().createSQLQuery(sql).executeUpdate();
				// System.err.println("task, process id persisting : "+process
				// +" update: "+update);
			} catch (Exception ex) {
				System.err.println("Exception while updating task events : " + ex.getMessage());
				System.err.println("problem process : " + process);
			}
			// }
			// this.getSession().flush();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getProcessAgingCountByDatesRange(Map<String, List<Date>> segmentMap, String processName) {

		logger.error("[pmc][processEventsDao][getProcessAgingCountByDatesRange] : ");
		DateFormat newDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yy hh:mm:ss
		// a");
		Iterator<Entry<String, List<Date>>> it = segmentMap.entrySet().iterator();
		String query = "";
		int count = 0;
		while (it.hasNext()) {
			++count;
			Entry<String, List<Date>> entry = it.next();
			String range = (String) entry.getKey();
			List<Date> dateRange = (List<Date>) entry.getValue();
			query = query + " Select '" + range
					+ "' AS DATE_RANGE, COUNT(*) AS PROCESS_COUNT, pe.NAME AS PROCESS_NAME, pc.PROCESS_DISPLAY_NAME AS PROCESS_DISPLAY_NAME FROM PROCESS_EVENTS pe LEFT JOIN PROCESS_CONFIG_TB pc ON pc.PROCESS_NAME = pe.NAME WHERE STARTED_AT BETWEEN "
					+ "'" + newDf.format(dateRange.get(1)) + "' and '" + newDf.format(dateRange.get(0)) + "'"
					// + " TO_DATE('" + dateFormatter.format(dateRange.get(1)) +
					// "', 'DD/MM/YY hh:mi:ss AM') and TO_DATE('" +
					// dateFormatter.format(dateRange.get(0)) + "', 'DD/MM/YY
					// hh:mi:ss PM')
					+ "and STATUS = '" + PMCConstant.PROCESS_STATUS_IN_PROGRESS + "'";
			if (!ServicesUtil.isEmpty(processName) && !processName.equals(PMCConstant.SEARCH_ALL)) {
				query = query + " and pe.PROCESS_ID IN (select D.process_id from PROCESS_EVENTS D where D.name IN ("
						+ processName + "))";
			}
			query = query + "  group by pe.NAME, pc.PROCESS_DISPLAY_NAME";
			if (count < segmentMap.entrySet().size()) {
				query = query + " UNION";
			}
		}

		logger.error("get - " + query);
		Query q = this.getSession().createSQLQuery(query.trim());
		List<Object[]> resultList = q.list();
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getProcessAgingCountByDates(Date startDate, Date endDate, String processName) {

		DateFormat newDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String tempQuery = "";
		String query = "select pe.NAME AS PROCESS_NAME, count(*) AS PROCESS_COUNT,"
				// +" trunc(pe.STARTED_AT)"
				+ "to_date(pe.STARTED_AT)"
				+ " AS STARTED_DATE, pc.PROCESS_DISPLAY_NAME AS PROCESS_DISPLAY_NAME from PROCESS_EVENTS pe LEFT JOIN PROCESS_CONFIG_TB pc ON pc.PROCESS_NAME = pe.NAME where pe.STATUS='IN_PROGRESS' and to_date(pe.STARTED_AT) between to_date('"
				+ newDf.format(startDate) + "') and to_date('" + newDf.format(endDate) + "')";
		if (!ServicesUtil.isEmpty(processName) && !processName.equals(PMCConstant.SEARCH_ALL)) {
			tempQuery = tempQuery + " and pe.PROCESS_ID IN (select D.process_id from PROCESS_EVENTS D where D.name IN ("
					+ processName + "))";
		}
		String groupQuery = " group by to_date(pe.STARTED_AT), pe.NAME, pc.PROCESS_DISPLAY_NAME ORDER BY to_date(pe.STARTED_AT)";
		// " group by trunc(pe.STARTED_AT), pe.NAME, pc.PROCESS_DISPLAY_NAME
		// ORDER BY trunc(pe.STARTED_AT)";
		query = query + tempQuery + groupQuery;
		logger.error("get - " + query);
		Query q = this.getSession().createSQLQuery(query);
		List<Object[]> resultList = q.list();
		return resultList;
	}

	public Object getProcessCount(String userId, String processName, String requestId, String labelValue, String status) {

		String tempQuery = "";
		String query = "select COUNT(DISTINCT(pe.PROCESS_ID)) AS PROCESS_COUNT from task_owners tw left join task_events te on tw.event_id = te.event_id left join process_events pe on pe.process_id = te.process_id where tw.task_owner='"
				+ userId + "'";
		if (!ServicesUtil.isEmpty(processName) && !processName.equals(PMCConstant.SEARCH_ALL)) {
			tempQuery = tempQuery
					+ " and pe.PROCESS_ID IN (select D.process_id from PROCESS_EVENTS D where D.name IN ('"
					+ processName + "'))";
		}
		if (!ServicesUtil.isEmpty(requestId)) {
			tempQuery = tempQuery + " and pe.REQUEST_ID = '" + requestId + "'";
		}
		if (!ServicesUtil.isEmpty(labelValue)) {
			tempQuery = tempQuery + " and pe.SUBJECT like '%" + labelValue + "%'";
		}
		if (!ServicesUtil.isEmpty(status)) {
			if (PMCConstant.SEARCH_READY.equalsIgnoreCase(status)) {
				tempQuery = tempQuery + " and te.STATUS = '" + status + "'";
			} else if (PMCConstant.SEARCH_RESERVED.equalsIgnoreCase(status)) {
				tempQuery = tempQuery + " and te.STATUS = '" + status + "' and tw.IS_PROCESSED = 1";
			} else {
				tempQuery = tempQuery + " and (te.STATUS = '" + PMCConstant.TASK_STATUS_READY + "' or (te.STATUS = '"
						+ PMCConstant.TASK_STATUS_RESERVED + "' and tw.IS_PROCESSED = 1))";
			}
		}
		tempQuery = tempQuery + " and pe.status='" + PMCConstant.PROCESS_STATUS_IN_PROGRESS + "'";
		query = query + tempQuery;
		logger.error("get - " + query);
		Query q = this.getSession().createSQLQuery(query);
		Object resultList = q.uniqueResult();
		// Object resultList = q.list();
		return resultList;

	}

	public ProcessDetailsResponse getProcessesByTaskOwner(UserProcessDetailRequestDto request) {
		logger.error("[PMC] ProcessEventsDao getProcessesByTaskOwner Started - with request - " + request);
		ProcessDetailsResponse detailResponse = null;
		List<ProcessEventsDto> processEventsDtos = null;
		ResponseMessage message = new ResponseMessage();
		if (!ServicesUtil.isEmpty(request.getUserId())) {
			detailResponse = new ProcessDetailsResponse();
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			List<Object[]> resultList = null;
			BigDecimal count = null;
			try {
				count = ServicesUtil
						.getBigDecimal(/* (Long) */ this.getProcessCount(request.getUserId(), request.getProcessName(),
								request.getRequestId(), request.getLabelValue(), request.getStatus()));
				logger.error("[PMC] ProcessEventsDao getProcessesByTaskOwner ProcessCount - " + count);
				resultList = this.getProcessDetail(request.getUserId(), request.getProcessName(),
						request.getRequestId(), request.getLabelValue(), request.getStatus(),
						ServicesUtil.isEmpty(request.getPage()) ? null : request.getPage());
				processEventsDtos = new ArrayList<ProcessEventsDto>();

				for (Object[] obj : resultList) {
					ProcessEventsDto processEventsDto = new ProcessEventsDto();
					processEventsDto.setProcessId(obj[0] == null ? null : (String) obj[0]);
					processEventsDto.setStartedAt(obj[1] == null ? null : ServicesUtil.resultAsDate(obj[1]));
					processEventsDto.setStartedAtInString(
							obj[1] == null ? null : formatter.format(ServicesUtil.resultAsDate(obj[1])));
					processEventsDto.setStartedBy(obj[2] == null ? null : (String) obj[2]);
					processEventsDto.setSubject(obj[3] == null ? null : (String) obj[3]);
					processEventsDto.setRequestId(obj[4] == null ? null : (String) obj[4]);
					processEventsDto.setName(obj[5] == null ? null : (String) obj[5]);
					processEventsDto.setStartedByDisplayName(obj[6] == null ? null : (String) obj[6]);
					processEventsDto.setProcessDisplayName(
							obj[7] == null ? obj[5] == null ? null : (String) obj[5] : (String) obj[7]);
					processEventsDtos.add(processEventsDto);
				}
				detailResponse.setProcessEventsList(processEventsDtos);
				detailResponse.setCount(count);
				message.setStatus("Success");
				message.setStatusCode("0");
				message.setMessage("Process Details Fetched Successfully");
				detailResponse.setResponseMessage(message);
			} catch (Exception e) {
				message.setStatus("Failed");
				message.setStatusCode("1");
				message.setMessage("Parse Exception :" + e.getMessage());
				detailResponse.setResponseMessage(message);
			}
		}
		logger.error("[PMC] ProcessEventsDao getProcessesByTaskOwner Ended - with ProcessDetailsResponse - "
				+ detailResponse);
		return detailResponse;
	}

	@SuppressWarnings("unchecked")
	public ProcessDetailsResponse getProcessByDuration(ProcessDetailsDto processDetailsDto) {
		ProcessDetailsResponse detailResponse = new ProcessDetailsResponse();
		ResponseMessage message = new ResponseMessage();
		Date startDateFrom = null;
		Date startDateTo = null;
		if (!ServicesUtil.isEmpty(processDetailsDto.getStartDayFrom())
				&& !ServicesUtil.isEmpty(processDetailsDto.getStartDayTo())) {
			try {
				startDateFrom = ServicesUtil.getDate(processDetailsDto.getStartDayFrom());
				startDateTo = ServicesUtil.getDate(processDetailsDto.getStartDayTo());
				startDateTo = ServicesUtil.setEndTime(startDateTo);
				logger.error("startDate  - " + startDateFrom + " [] endDate  - " + startDateTo);
				StringBuffer processQuery = new StringBuffer(
						"select p.REQUEST_ID AS REQUEST_ID, p.PROCESS_ID AS PROCESS_ID, p.NAME AS NAME, p.SUBJECT AS SUBJECT, p.STARTED_AT AS STARTED_AT, p.STARTED_BY AS STARTED_BY, p.STARTED_BY_DISP AS STARTED_BY_DISP, c.PROCESS_DISPLAY_NAME AS PROCESS_DISPLAY_NAME FROM PROCESS_EVENTS p LEFT JOIN PROCESS_CONFIG_TB c ON p.NAME = c.PROCESS_NAME where p.STATUS = \'IN_PROGRESS\'");
				if (!ServicesUtil.isEmpty(processDetailsDto.getProcessName())) {
					// processQuery.append(" and p.NAME = ('" +
					// processDetailsDto.getProcessName() + "')");
					processQuery.append(" and p.NAME = '" + processDetailsDto.getProcessName() + "'");
				}
				if (!ServicesUtil.isEmpty(processDetailsDto.getStartDayFrom())
						&& !ServicesUtil.isEmpty(processDetailsDto.getStartDayTo())) {
					// DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yy
					// hh:mm:ss a");
					DateFormat newDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					processQuery.append(" and p.STARTED_AT between" + "'" + newDf.format(startDateFrom) + "' and '"
							+ newDf.format(startDateTo) + "'");
					// + " TO_DATE('" + dateFormatter.format(startDateFrom) +
					// "', 'DD/MM/YY hh:mi:ss AM') and TO_DATE('" +
					// dateFormatter.format(startDateTo) + "', 'DD/MM/YY
					// hh:mi:ss PM')");
				}

				if (!ServicesUtil.isEmpty(processDetailsDto.getPage())) {
					int first = (processDetailsDto.getPage() - 1) * PMCConstant.PAGE_SIZE;
					int last = PMCConstant.PAGE_SIZE;

					/* Commented for Pagination in HANA */
					// query.setFirstResult(first);
					// query.setMaxResults(last);

					processQuery = processQuery.append(" LIMIT " + last + " OFFSET " + first + "");
				}

				Query query = this.getSession().createSQLQuery(processQuery.toString());

				logger.error("processQuery - " + processQuery.toString());
				List<Object[]> resultList = query.list();
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
				if (resultList != null) {
					List<ProcessEventsDto> processEventsList = new ArrayList<ProcessEventsDto>();
					for (Object[] obj : resultList) {
						ProcessEventsDto processEventsDto = new ProcessEventsDto();
						processEventsDto.setRequestId(obj[0] == null ? null : (String) obj[0]);
						processEventsDto.setProcessId(obj[1] == null ? null : (String) obj[1]);
						processEventsDto.setName(obj[2] == null ? null : (String) obj[2]);
						processEventsDto.setSubject(obj[3] == null ? null : (String) obj[3]);
						processEventsDto.setStartedAt(obj[4] == null ? null : ServicesUtil.resultAsDate(obj[4]));
						processEventsDto.setStartedAtInString(
								obj[4] == null ? null : formatter.format(ServicesUtil.resultAsDate(obj[4])));
						processEventsDto.setStartedByDisplayName(obj[6] == null ? null : (String) obj[6]);
						processEventsDto.setStartedBy(obj[5] == null ? null : (String) obj[5]);
						processEventsDto.setProcessDisplayName(
								obj[7] == null ? obj[2] == null ? null : (String) obj[2] : (String) obj[7]);
						processEventsList.add(processEventsDto);
					}
					Comparator<ProcessEventsDto> sortByStartedAt = new Comparator<ProcessEventsDto>() {
						@Override
						public int compare(ProcessEventsDto o1, ProcessEventsDto o2) {
							return o2.getStartedAt().compareTo(o1.getStartedAt());
						}
					};
					Collections.sort(processEventsList, sortByStartedAt);

					logger.error("**** " + processEventsList);
					detailResponse.setProcessEventsList(processEventsList);
					message.setStatus("Success");
					message.setStatusCode("0");
					message.setMessage("Process Details Fetched Successfully");
					detailResponse.setResponseMessage(message);
				} else {
					message.setStatus("Success");
					message.setStatusCode("1");
					message.setMessage("No Results Found for the requested query");
					detailResponse.setResponseMessage(message);
				}
			} catch (ParseException e) {
				message.setStatus("Failed");
				message.setStatusCode("1");
				message.setMessage("Parse Exception :" + e.getMessage());
				detailResponse.setResponseMessage(message);
			}
		}
		return detailResponse;
	}

	@SuppressWarnings("unchecked")
	public List<UserDetailsDto> getCreatedByList(String inputValue) {
		if (!ServicesUtil.isEmpty(inputValue)) {
			inputValue = inputValue.toLowerCase();
			String queryString = "SELECT DISTINCT(pe.STARTED_BY_DISP) AS DISPLAY_NAME, pe.STARTED_BY AS ID FROM PROCESS_EVENTS pe WHERE   lower (pe.STARTED_BY_DISP) LIKE '%"
					+ inputValue + "%' OR   lower(pe.STARTED_BY) LIKE '%" + inputValue + "%'";
			Query query = this.getSession().createSQLQuery(queryString.trim());
			List<Object[]> objList = query.list();
			if (!ServicesUtil.isEmpty(objList)) {
				List<UserDetailsDto> dtoList = new ArrayList<UserDetailsDto>();
				for (Object[] obj : objList) {
					UserDetailsDto dto = new UserDetailsDto();
					dto.setDisplayName(obj[0] == null ? null : (String) obj[0]);
					dto.setUserId(obj[1] == null ? null : (String) obj[1]);
					dtoList.add(dto);
				}
				return dtoList;
			}
		}
		return null;
	}

	public String createProcessInstance(ProcessEventsDto dto) {
		// logger.error("[PMC][ProcessEventsDao][createProcessInstance]initiated
		// with " + dto);
		try {
			// this.getEntityManager().getTransaction().begin();
			this.getSession().save(dto);
			// this.getEntityManager().getTransaction().commit();
			return "SUCCESS";
		} catch (Exception e) {
			logger.error("[PMC][ProcessEventsDao][createProcessInstance][error] " + e.getMessage());
		}
		return "FAILURE";
	}

	public String updateProcessInstance(ProcessEventsDto dto) {
		// logger.error("[PMC][ProcessEventsDao][updateProcessInstance]initiated
		// with " + dto);
		try {
			this.getSession().update(dto);
			return "SUCCESS";
		} catch (Exception e) {
			logger.error("[PMC][ProcessEventsDao][updateProcessInstance][error] " + e.getMessage());
		}
		return "FAILURE";
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getTasksDetailsByUserAndDuration(ManageTasksRequestDto request) {
		Date startDateFrom = null;
		Date startDateTo = null;
		List<Object[]> resultList = null;
		try {
			startDateFrom = ServicesUtil.getDate(request.getStartDayFrom());
			startDateTo = ServicesUtil.getDate(request.getStartDayTo());
			startDateTo = ServicesUtil.setEndTime(startDateTo);
			logger.error("startDate  - " + startDateFrom);
			logger.error("endDate  - " + startDateTo);
			StringBuffer taskStr = new StringBuffer(
					"SELECT p.REQUEST_ID AS REQUEST_ID, p.NAME AS NAME, t.EVENT_ID AS EVENT_ID, t.SUBJECT AS SUBJECT, t.CREATED_AT AS CREATED_AT, t.STATUS AS STATUS, v.TASK_OWNER AS TASK_OWNER, v.TASK_OWNER_DISP AS TASK_OWNER_DISP, c.PROCESS_DISPLAY_NAME AS PROCESS_DISP_NAME FROM PROCESS_EVENTS p LEFT OUTER JOIN PROCESS_CONFIG_TB c ON p.NAME = c.PROCESS_NAME, TASK_EVENTS t, TASK_OWNERS v where p.PROCESS_ID=t.PROCESS_ID and v.EVENT_ID=t.EVENT_ID and p.STATUS = \'IN_PROGRESS\'");
			if (!ServicesUtil.isEmpty(request.getProcessName()) || !ServicesUtil.isEmpty(request.getLabelValue())
					|| !ServicesUtil.isEmpty(request.getRequestId())) {
				taskStr.append(" and t.PROCESS_ID IN (select D.PROCESS_ID from PROCESS_EVENTS D where 1=1");
				if (!request.getProcessName().equals(PMCConstant.SEARCH_ALL))
					taskStr.append(" and D.NAME IN ('" + request.getProcessName() + "')");
				if (!ServicesUtil.isEmpty(request.getLabelValue()))
					taskStr.append(" and D.SUBJECT like '%" + request.getLabelValue() + "%'");
				if (!ServicesUtil.isEmpty(request.getRequestId()))
					taskStr.append(" and D.REQUEST_ID = '" + request.getRequestId() + "'");
			}
			if (request.getStartDayFrom() >= 0 && request.getStartDayTo() >= 0) {
				// DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yy
				// hh:mm:ss a");
				DateFormat newDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				// taskStr.append(" and t.CREATED_AT between TO_DATE('" +
				// dateFormatter.format(startDateFrom) + "', 'DD/MM/YY hh:mi:ss
				// AM') and TO_DATE('" + dateFormatter.format(startDateTo) + "',
				// 'DD/MM/YY hh:mi:ss PM')");

				taskStr.append(" and t.CREATED_AT between '" + newDf.format(startDateFrom) + "' and '"
						+ newDf.format(startDateTo) + "'");

				// + "'" + dateFormatter.format(startDateFrom) + "' and '" +
				// dateFormatter.format(startDateTo) + "'");
			}
			taskStr.append(")");
			if (!ServicesUtil.isEmpty(request.getOwner())) {
				if (!ServicesUtil.isEmpty(request.getTaskStatus())) {
					if (PMCConstant.SEARCH_RESERVED.equalsIgnoreCase(request.getTaskStatus())) {
						taskStr.append(" AND t.STATUS = '" + request.getTaskStatus() + "' AND t.CUR_PROC = '"
								+ request.getOwner() + "' AND v.IS_PROCESSED = \'1\'");
					} else if (PMCConstant.SEARCH_READY.equalsIgnoreCase(request.getTaskStatus())) {
						taskStr.append(" AND t.STATUS = '" + PMCConstant.TASK_STATUS_READY
								+ "' AND t.EVENT_ID IN (SELECT t.EVENT_ID FROM TASK_OWNERS v WHERE t.EVENT_ID = v.EVENT_ID AND v.TASK_OWNER = '"
								+ request.getOwner() + "')");
					} else {
						taskStr.append(" AND (t.STATUS = '" + PMCConstant.TASK_STATUS_READY
								+ "' AND t.EVENT_ID IN (SELECT t.EVENT_ID FROM TASK_OWNERS v WHERE t.EVENT_ID = v.EVENT_ID AND v.TASK_OWNER = '"
								+ request.getOwner() + "')  OR (t.STATUS = '" + PMCConstant.TASK_STATUS_RESERVED
								+ "' AND v.IS_PROCESSED = \'1\' AND t.CUR_PROC = '" + request.getOwner() + "'))");
					}
				}
			}
			Query query = this.getSession().createSQLQuery(taskStr.toString());
			logger.error("Query: " + taskStr);
			/*
			 * if (!ServicesUtil.isEmpty(request.getPage())) { int first =
			 * (request.getPage() - 1) * PMCConstant.PAGE_SIZE; int last =
			 * PMCConstant.PAGE_SIZE; query.setFirstResult(first);
			 * query.setMaxResults(last); }
			 */
			resultList = query.list();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<String> getRequestId() {

		String queryString = "select distinct request_Id from process_events where request_id is not null";
		Query query = this.getSession().createSQLQuery(queryString);
		return query.list();
	}
	
	public void saveOrUpdateProcesses(List<ProcessEventsDto> processes) {
		Session session = null;
		if(!ServicesUtil.isEmpty(processes) && processes.size() > 0) {
			session = this.getSession();
			for(int i = 0; i < processes.size(); i++) {
				session.saveOrUpdate(importDto(processes.get(i)));
//				if(i % _HIBERNATE_BATCH_SIZE == 0 && i > 0) {
//					session.flush();
//					session.clear();
//				}
			}
			/*if(!session.getTransaction().wasCommitted()) {
				session.flush();
			}*/
//			session.close();
		}
	}
	

	// public void closeSession() {
	// try {
	// if(!this.getSession().getTransaction().wasCommitted()) {
	// this.getSession().flush();
	// }
	// this.getSession().close();
	// } catch (Exception ex) {
	// System.err.println("Exception while closing session : "+ex.getMessage());
	// }
	// }
}
 