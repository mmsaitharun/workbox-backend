package oneapp.workbox.services.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import oneapp.workbox.services.dto.TaskEventsDto;
import oneapp.workbox.services.entity.TaskEventsDo;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;

@Repository("TaskEventsDao")
@Transactional
public class TaskEventsDao {
	
	@Autowired
	SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	private static final Logger logger = LoggerFactory.getLogger(TaskEventsDao.class);
	
	protected List<TaskEventsDto> exportDtoList(List<TaskEventsDo> listDo) {
		List<TaskEventsDto> returnDtos = null;
		if (!ServicesUtil.isEmpty(listDo)) {
			returnDtos = new ArrayList<TaskEventsDto>(listDo.size());
			for (Iterator<TaskEventsDo> iterator = listDo.iterator(); iterator.hasNext();) {
				returnDtos.add(exportDto(iterator.next()));
			}
		}
		return returnDtos;
	}
	
	protected TaskEventsDto exportDto(TaskEventsDo entity) {
		TaskEventsDto taskEventsDto = new TaskEventsDto();
		taskEventsDto.setEventId(entity.getEventId());
		taskEventsDto.setProcessId(entity.getProcessId());
		if (!ServicesUtil.isEmpty(entity.getDescription()))
			taskEventsDto.setDescription(entity.getDescription());
		if (!ServicesUtil.isEmpty(entity.getName()))
			taskEventsDto.setName(entity.getName());
		if (!ServicesUtil.isEmpty(entity.getSubject()))
			taskEventsDto.setSubject(entity.getSubject());
		if (!ServicesUtil.isEmpty(entity.getStatus()))
			taskEventsDto.setStatus(entity.getStatus());
		if (!ServicesUtil.isEmpty(entity.getCurrentProcessor()))
			taskEventsDto.setCurrentProcessor(entity.getCurrentProcessor());
		if (!ServicesUtil.isEmpty(entity.getPriority()))
			taskEventsDto.setPriority(entity.getPriority());
		if (!ServicesUtil.isEmpty(entity.getCreatedAt()))
			taskEventsDto.setCreatedAt(entity.getCreatedAt());
		if (!ServicesUtil.isEmpty(entity.getCompletedAt()))
			taskEventsDto.setCompletedAt(entity.getCompletedAt());
		if (!ServicesUtil.isEmpty(entity.getCompletionDeadLine()))
			taskEventsDto.setCompletionDeadLine(entity.getCompletionDeadLine());
		if (!ServicesUtil.isEmpty(entity.getProcessName()))
			taskEventsDto.setProcessName(entity.getProcessName());
		if (!ServicesUtil.isEmpty(entity.getTaskMode()))
			taskEventsDto.setTaskMode(entity.getTaskMode());
		if (!ServicesUtil.isEmpty(entity.getStatusFlag()))
			taskEventsDto.setStatusFlag(entity.getStatusFlag());
		if (!ServicesUtil.isEmpty(entity.getCurrentProcessorDisplayName()))
			taskEventsDto.setCurrentProcessorDisplayName(entity.getCurrentProcessorDisplayName());
		if (!ServicesUtil.isEmpty(entity.getTaskType()))
			taskEventsDto.setTaskType(entity.getTaskType());
		if (!ServicesUtil.isEmpty(entity.getForwardedBy()))
			taskEventsDto.setForwardedBy(entity.getForwardedBy());
		if (!ServicesUtil.isEmpty(entity.getForwardedAt()))
			taskEventsDto.setForwardedAt(entity.getForwardedAt());
		if(!ServicesUtil.isEmpty(entity.getOrigin()))
			taskEventsDto.setOrigin(entity.getOrigin());
		if(!ServicesUtil.isEmpty(entity.getUrl()))
			taskEventsDto.setDetailUrl(entity.getUrl());
		if(!ServicesUtil.isEmpty(entity.getSlaDueDate()))
			taskEventsDto.setSlaDueDate(entity.getSlaDueDate());
		if(!ServicesUtil.isEmpty(entity.getUpdatedAt()))
			taskEventsDto.setUpdatedAt(entity.getUpdatedAt());
		return taskEventsDto;
	}

	protected TaskEventsDo importDto(TaskEventsDto fromDto) {
		TaskEventsDo entity = new TaskEventsDo();
		if (!ServicesUtil.isEmpty(fromDto.getEventId()))
			entity.setEventId(fromDto.getEventId());
		if (!ServicesUtil.isEmpty(fromDto.getProcessId()))
			entity.setProcessId(fromDto.getProcessId());
		if (!ServicesUtil.isEmpty(fromDto.getStatus()))
			entity.setStatus(fromDto.getStatus());
		if (!ServicesUtil.isEmpty(fromDto.getCurrentProcessor()))
			entity.setCurrentProcessor(fromDto.getCurrentProcessor());
		if (!ServicesUtil.isEmpty(fromDto.getPriority()))
			entity.setPriority(fromDto.getPriority());
		if (!ServicesUtil.isEmpty(fromDto.getCreatedAt()))
			entity.setCreatedAt(fromDto.getCreatedAt());
		if (!ServicesUtil.isEmpty(fromDto.getCompletedAt()))
			entity.setCompletedAt(fromDto.getCompletedAt());
		if (!ServicesUtil.isEmpty(fromDto.getCompletionDeadLine()))
			entity.setCompletionDeadLine(fromDto.getCompletionDeadLine());
		if (!ServicesUtil.isEmpty(fromDto.getCurrentProcessorDisplayName()))
			entity.setCurrentProcessorDisplayName(fromDto.getCurrentProcessorDisplayName());
		if (!ServicesUtil.isEmpty(fromDto.getDescription()))
			entity.setDescription(fromDto.getDescription());
		if (!ServicesUtil.isEmpty(fromDto.getName()))
			entity.setName(fromDto.getName());
		if (!ServicesUtil.isEmpty(fromDto.getSubject()))
			entity.setSubject(fromDto.getSubject());
		if (!ServicesUtil.isEmpty(fromDto.getProcessName()))
			entity.setProcessName(fromDto.getProcessName());
		if (!ServicesUtil.isEmpty(fromDto.getTaskMode()))
			entity.setTaskMode(fromDto.getTaskMode());
		if (!ServicesUtil.isEmpty(fromDto.getStatusFlag()))
			entity.setStatusFlag(fromDto.getStatusFlag());
		if (!ServicesUtil.isEmpty(fromDto.getTaskType()))
			entity.setTaskType(fromDto.getTaskType());
		if (!ServicesUtil.isEmpty(fromDto.getForwardedBy()))
			entity.setForwardedBy(fromDto.getForwardedBy());
		if (!ServicesUtil.isEmpty(fromDto.getForwardedAt()))
			entity.setForwardedAt(fromDto.getForwardedAt());
		if(!ServicesUtil.isEmpty(fromDto.getOrigin()))
			entity.setOrigin(fromDto.getOrigin());
		if(!ServicesUtil.isEmpty(fromDto.getDetailUrl()))
			entity.setUrl(fromDto.getDetailUrl());
		if(!ServicesUtil.isEmpty(fromDto.getSlaDueDate()))
			entity.setSlaDueDate(fromDto.getSlaDueDate());
		if(!ServicesUtil.isEmpty(fromDto.getUpdatedAt()))
			entity.setUpdatedAt(fromDto.getUpdatedAt());
		return entity;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getUserTaskCount(String userId, String processName, String requestId, String labelValue, String status, Date startDate, Date endDate) {
		DateFormat newDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String tempQuery = "";
		String query = "select count(te.CREATED_AT) AS TASK_COUNT, (te.CREATED_AT)"
				//"select count(te.CREATED_AT) AS TASK_COUNT, trunc(te.CREATED_AT)
				+"AS CREATED_DATE from task_events te left join task_owners tw on te.event_id = tw.event_id where tw.task_owner ='"
				+ userId + "'";
		String groupQuery = " group by (te.CREATED_AT) ORDER BY (te.CREATED_AT)";
		//" group by trunc(te.CREATED_AT) ORDER BY trunc(te.CREATED_AT)";
		if (!ServicesUtil.isEmpty(processName) && !processName.equals(PMCConstant.SEARCH_ALL)) {
			tempQuery = tempQuery + " and te.PROCESS_ID IN (select D.process_id from PROCESS_EVENTS D where D.name = '" + processName + "')";
		}
		if (!ServicesUtil.isEmpty(requestId)) {
			tempQuery = tempQuery + " and te.PROCESS_ID IN (select D.process_id from PROCESS_EVENTS D where D.REQUEST_ID = '" + requestId + "')";
		}
		if (!ServicesUtil.isEmpty(labelValue)) {
			tempQuery = tempQuery + " and te.PROCESS_ID IN (select D.process_id from PROCESS_EVENTS D where D.SUBJECT like '%" + labelValue + "%')";
		}
		if (!ServicesUtil.isEmpty(status)) {
			if (PMCConstant.SEARCH_READY.equalsIgnoreCase(status)) {
				tempQuery = tempQuery + " and te.STATUS = '" + status + "'";
			} else if (PMCConstant.SEARCH_RESERVED.equalsIgnoreCase(status)) {
				tempQuery = tempQuery + " and te.STATUS = '" + status + "' and tw.IS_PROCESSED = 1";
			} else {
				tempQuery = tempQuery + " and (te.STATUS = '" + PMCConstant.TASK_STATUS_READY + "' or (te.STATUS = '" + PMCConstant.TASK_STATUS_RESERVED + "' and tw.IS_PROCESSED = 1))";
			}
		}
		if (!ServicesUtil.isEmpty(startDate) && !ServicesUtil.isEmpty(endDate)) {
			tempQuery = tempQuery + " and te.CREATED_AT between "
					+"'"+ newDf.format(startDate)+"' and '" +newDf.format(endDate)+"'";
			//	+ "TO_DATE('" + dateFormatter.format(startDate) + "', 'DD/MM/YY hh:mi:ss AM') and TO_DATE('" + dateFormatter.format(endDate) + "', 'DD/MM/YY hh:mi:ss PM')";
		}
		query = query + tempQuery + groupQuery;
		logger.error("get - " + query);
		Query q = this.getSession().createSQLQuery(query);
		List<Object[]> resultList = q.list();
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getTaskCountByOwner(String userId, String processName, String requestId, String labelValue, String status, Date startDate, Date endDate) {
		String tempQuery = "";
		//DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yy hh:mm:ss a");
		DateFormat newDf = new SimpleDateFormat("yyyy-MM-dd");
		String queryColumn = null;
		if (PMCConstant.SEARCH_COMPLETED.equalsIgnoreCase(status)) {
			queryColumn = "COMPLETED_AT";
			tempQuery = tempQuery + " and te.STATUS = '" + status + "'";
		} else {
			queryColumn = "CREATED_AT";
			if (PMCConstant.SEARCH_READY.equalsIgnoreCase(status)) {
				tempQuery = tempQuery + " and te.STATUS = '" + status + "'";
			} else if (PMCConstant.SEARCH_RESERVED.equalsIgnoreCase(status)) {
				tempQuery = tempQuery + " and te.STATUS = '" + status + "' and tw.IS_PROCESSED = 1";
			} else {
				tempQuery = tempQuery + " and (te.STATUS = '" + PMCConstant.TASK_STATUS_READY + "' or (te.STATUS = '" + PMCConstant.TASK_STATUS_RESERVED + "' and tw.IS_PROCESSED = 1))";
			}
		}
		String query ="SELECT to_date(te." + queryColumn + ") AS TASK_DATE, COUNT(to_date(te." + queryColumn
				//"SELECT TRUNC(te." + queryColumn + ") AS TASK_DATE, COUNT(TRUNC(te." + queryColumn
				+ ")) AS TASK_COUNT from task_owners tw left join task_events te on tw.event_id = te.event_id left join process_events pe on pe.process_id = te.process_id where tw.task_owner='"
				+ userId + "'";
		if (!ServicesUtil.isEmpty(processName) && !processName.equals(PMCConstant.SEARCH_ALL)) {
			tempQuery = tempQuery + " and pe.PROCESS_ID IN (select D.process_id from PROCESS_EVENTS D where D.name IN ('" + processName + "'))";
		}
		if (!ServicesUtil.isEmpty(requestId)) {
			tempQuery = tempQuery + " and pe.REQUEST_ID = '" + requestId + "'";
		}
		if (!ServicesUtil.isEmpty(labelValue)) {
			tempQuery = tempQuery + " and pe.SUBJECT like '%" + labelValue + "%'";
		}
		if (!ServicesUtil.isEmpty(startDate) && !ServicesUtil.isEmpty(endDate)) {
			if (PMCConstant.SEARCH_COMPLETED.equalsIgnoreCase(status))
				tempQuery = tempQuery + " and to_date(te.COMPLETED_AT) between ";
			else
				tempQuery = tempQuery + " and to_date(te.CREATED_AT) between ";
			tempQuery = tempQuery +"TO_DATE('"+ newDf.format(startDate)+"') and TO_DATE('" +newDf.format(endDate)+"')";
			//"TO_DATE('"+dateFormatter.format(startDate) + "', 'DD/MM/YY hh:mi:ss AM') and TO_DATE('" + dateFormatter.format(endDate) + "', 'DD/MM/YY hh:mi:ss PM')";
		}
		if (!PMCConstant.SEARCH_COMPLETED.equalsIgnoreCase(status))
			tempQuery = tempQuery + " and pe.status='" + PMCConstant.PROCESS_STATUS_IN_PROGRESS + "'";
		String groupQuery = " group by to_date(te." + queryColumn + ") ORDER BY to_date(te." + queryColumn + ")";
		//" group by trunc(te." + queryColumn + ") ORDER BY trunc(te." + queryColumn + ")";
		query = query + tempQuery + groupQuery;
		logger.error("get_getTaskCountByOwner - " + query);
		Query q = this.getSession().createSQLQuery(query);
		List<Object[]> resultList = q.list();
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<TaskEventsDo> checkIfTaskInstanceExists(String instanceId) {
		Query query = this.getSession().createSQLQuery("select te from TaskEventsDo te where te.taskEventsDoPK.eventId =:instanceId");
		query.setParameter("instanceId", instanceId);
		List<TaskEventsDo> taskEventsDos = (List<TaskEventsDo>) query.list();
		if (taskEventsDos.size() > 0) {
			return taskEventsDos;

		} else {
			return null;
		}
   
	}
	@SuppressWarnings("unchecked")
	public TaskEventsDto getTaskInstanceIfExists(String instanceId){
		System.err.println("check for instance");
		Query query = this.getSession().createQuery("select te from TaskEventsDo te where te.taskEventsDoPK.eventId =:instanceId");
		query.setParameter("instanceId", instanceId);
		List<TaskEventsDo> taskEventsDos = (List<TaskEventsDo>) query.list();
		System.err.println("Already Preesent task"+taskEventsDos);
		
		if (taskEventsDos.size() > 0) {
			TaskEventsDto taskEventDto =exportDto(taskEventsDos.get(0));
			return taskEventDto;

		} else {
			System.err.println("retruning null");
			return null;
		}
	}
	public TaskEventsDto saveOrUpdateTask(TaskEventsDto dto) throws Exception{
		
			this.getSession().saveOrUpdate(importDto(dto));
			return dto;
		
	}


	public String createTaskInstance(TaskEventsDto dto) {
		//	logger.error("[PMC][TaskEventsDao][createTaskInstance]initiated with " + dto);

		try {
			this.getSession().save(importDto(dto));
			return "SUCCESS";
		} catch (Exception e) {
			logger.error("[PMC][TaskEventsDao][createTaskInstance][error] " + e.getMessage());
		}
		return "FAILURE";

	}

	public String updateTaskInstance(TaskEventsDto dto) {
		//	logger.error("[PMC][TaskEventsDao][updateTaskInstance]initiated with " + dto);
		try {
			this.getSession().update(importDto(dto));
			return "SUCCESS";
		} catch (Exception e) {
			logger.error("[PMC][TaskEventsDao][updateTaskInstance][error] " + e.getMessage());
		}
		return "FAILURE";

	}

	public String updateTaskEventToReady(String instanceId) {
		//	logger.error("[PMC][TaskOwnersDao][setTaskOwnersToReady][instanceId]"+instanceId);
		try{
			String queryString = "Update TaskEventsDo te set te.status = 'READY' , te.currentProcessor = '' , te.currentProcessorDisplayName = '' where te.taskEventsDoPK.eventId = '"+instanceId+"'";
			Query q = this.getSession().createSQLQuery(queryString);
			if(q.executeUpdate()>0){
				return "SUCCESS";
			}
		}
		catch(Exception e){
			logger.error("[PMC][TaskEventsDao][setTaskOwnersToReady][error]"+e.getMessage());
		}
		return "FAILURE";
	}

	public String updateTaskEventToReserved(String instanceId,String user,String userDisplay) {
		try{
			String queryString = "Update TaskEventsDo te set te.status = 'RESERVED' , te.currentProcessor = '"+user+"' , te.currentProcessorDisplayName = '"+userDisplay+"' where te.taskEventsDoPK.eventId = '"+instanceId+"'";
			Query q = this.getSession().createSQLQuery(queryString);
			if(q.executeUpdate()>0){
				return "SUCCESS";
			}
		}
		catch(Exception e){
			logger.error("[PMC][TaskEventsDao][updateTaskEventToReserved][error]"+e.getMessage());
		}
		return "FAILURE";
	}


	public String checkIfTaskIsReserved(String userId ,String instanceId){

		try{
			String queryString = "select count(te) from TaskEventsDo te where te.status = 'RESERVED' and te.taskEventsDoPK.eventId = '"+instanceId+"' and te.currentProcessor = '"+userId+"'";
			Query q = this.getSession().createSQLQuery(queryString);
			Long count =  (Long) q.uniqueResult();
			if(count > 0){
				return "SUCCESS";
			}
		}
		catch(Exception e){
			logger.error("[PMC][TaskEventsDao][checkIfTaskIsReserved][error]"+e.getMessage());
		}
		return "FAILURE";

	}


	public String checkIfTaskIsReady(String instanceId){

		try{
			String queryString = "select count(te) from TaskEventsDo te where te.status = 'READY' and te.taskEventsDoPK.eventId = '"+instanceId+"' ";
			Query q = this.getSession().createSQLQuery(queryString);
			Long count =  (Long)  q.uniqueResult();
			if(count > 0){
				return "SUCCESS";
			}
		}
		catch(Exception e){
			logger.error("[PMC][TaskEventsDao][checkIfTaskIsReady][error]"+e.getMessage());
		}
		return "FAILURE";

	}
	@SuppressWarnings("unchecked")
	public List<TaskEventsDo>	getTaskDetailsByProcessId(String processId){

		Query query = this.getSession()
				.createQuery("select te from TaskEventsDo te where te.processId =:processId");
		query.setParameter("processId", processId);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]>  getSLAByNameAndProcessId(String name,String processId){

		String qry = "select A.SLA AS SLA, A.URGENT_SLA AS URGENT_SLA from TASK_SLA A where TASK_DEF='"
				+ name
				+ "' and PROC_NAME IN( select PROC_NAME from PROCESS_EVENTS where PROCESS_ID = '"
				+ processId+ "')";
		logger.error("Query: " + qry);
		Query query = this.getSession().createSQLQuery(qry.toString());
		return query.list();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<TaskEventsDto> getAllOpenTasks() {
		Criteria criteria = this.getSession().createCriteria(TaskEventsDo.class);
		criteria.add(Restrictions.ne("status", "CANCELED"));
		criteria.add(Restrictions.ne("status", "COMPLETED"));
		return this.exportDtoList(criteria.list());
	}
	
	public void saveOrUpdateTasks(List<TaskEventsDto> tasks) {
		Session session = null;
		if(!ServicesUtil.isEmpty(tasks) && tasks.size() > 0) {
			session = this.getSession();
			for(int i = 0; i < tasks.size(); i++) {
				TaskEventsDto currentTask = tasks.get(i);
				currentTask.setUpdatedAt(new Date());
				session.saveOrUpdate(importDto(currentTask));
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
	
	public int executeUpdateQuery(String query) {
		return this.getSession().createSQLQuery(query).executeUpdate();
	}

	public int changeTaskStatus(TaskEventsDo taskDo) {
		Session session = this.getSession();
		try {
			TaskEventsDo task = (TaskEventsDo) session.load(TaskEventsDo.class, taskDo.getEventId());
			task.setStatus(taskDo.getStatus());
			task.setCurrentProcessor(taskDo.getCurrentProcessor());
			session.saveOrUpdate(task);
			return 1;
		} catch (Exception ex) {
			System.err.println("Exception saving Task to DB : "+ex.getMessage());
			return 0;
		}
	}
}
