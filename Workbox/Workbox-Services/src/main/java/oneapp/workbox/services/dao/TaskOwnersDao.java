package oneapp.workbox.services.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import oneapp.workbox.services.dto.TaskOwnersDto;
import oneapp.workbox.services.entity.TaskOwnersDo;
import oneapp.workbox.services.entity.TaskOwnersDoPK;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;

@Repository("TaskOwnersDao")
@Transactional
public class TaskOwnersDao {
	
	@Autowired
	SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	private static final Logger logger = LoggerFactory.getLogger(TaskOwnersDao.class);

	protected TaskOwnersDto exportDto(TaskOwnersDo entity) {
		TaskOwnersDto taskOwnersDto = new TaskOwnersDto();
		taskOwnersDto.setEventId(entity.getTaskOwnersDoPK().getEventId());
		taskOwnersDto.setTaskOwner(entity.getTaskOwnersDoPK().getTaskOwner());
		if (!ServicesUtil.isEmpty(entity.getIsProcessed()))
			taskOwnersDto.setIsProcessed(entity.getIsProcessed());
		if (!ServicesUtil.isEmpty(entity.getTaskOwnerDisplayName()))
			taskOwnersDto.setTaskOwnerDisplayName(entity.getTaskOwnerDisplayName());
		if (!ServicesUtil.isEmpty(entity.getOwnerEmail()))
			taskOwnersDto.setOwnerEmail(entity.getOwnerEmail());
		if (!ServicesUtil.isEmpty(entity.getIsSubstituted()))
			taskOwnersDto.setIsSubstituted(entity.getIsSubstituted());
		return taskOwnersDto;
	}

	protected TaskOwnersDo importDto(TaskOwnersDto fromDto) {
		TaskOwnersDo entity = null;
		if(!ServicesUtil.isEmpty(fromDto) && !ServicesUtil.isEmpty(fromDto.getEventId()) && !ServicesUtil.isEmpty(fromDto.getTaskOwner())){
			entity = new TaskOwnersDo();
			entity.setTaskOwnersDoPK(new TaskOwnersDoPK(fromDto.getEventId(), fromDto.getTaskOwner()));
			if (!ServicesUtil.isEmpty(fromDto.getIsProcessed()))
				entity.setIsProcessed(fromDto.getIsProcessed());
			if (!ServicesUtil.isEmpty(fromDto.getTaskOwnerDisplayName()))
				entity.setTaskOwnerDisplayName(fromDto.getTaskOwnerDisplayName());
			if (!ServicesUtil.isEmpty(fromDto.getOwnerEmail()))
				entity.setOwnerEmail(fromDto.getOwnerEmail());
			if (!ServicesUtil.isEmpty(fromDto.getIsSubstituted()))
				entity.setIsSubstituted(fromDto.getIsSubstituted());
		}
		return entity;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getTaskCountWithOwners(String processName, String requestId, String labelId, String status){
		String tempQuery = "";
		String query = "SELECT C.TASK_OWNER AS OWNER, COUNT(C.TASK_OWNER) AS TASK_COUNT, C.TASK_OWNER_DISP AS OWNER_NAME from PROCESS_EVENTS A, TASK_EVENTS B, TASK_OWNERS C where A.PROCESS_ID = B.PROCESS_ID and B.EVENT_ID = C.EVENT_ID";
		String groupQuery = " group by C.TASK_OWNER, C.TASK_OWNER_DISP";

		if (!ServicesUtil.isEmpty(processName) && !processName.equals(PMCConstant.SEARCH_ALL)) {
			tempQuery = tempQuery
					+ " and A.PROCESS_ID IN (select D.process_id from PROCESS_EVENTS D where D.name IN ( '"
					+ processName + "'))";
		}
		if (!ServicesUtil.isEmpty(requestId)) {
			tempQuery = tempQuery + " and A.REQUEST_ID = '" + requestId + "'";
		}
		if (!ServicesUtil.isEmpty(labelId)) {
			tempQuery = tempQuery + " and A.SUBJECT like '%" + labelId + "%'";
		}
		if (!ServicesUtil.isEmpty(status)) {
			if (PMCConstant.SEARCH_READY.equalsIgnoreCase(status)) {
				tempQuery = tempQuery + " and B.STATUS = '" + status + "'";
			} else if (PMCConstant.SEARCH_RESERVED.equalsIgnoreCase(status)) {
				tempQuery = tempQuery + " and B.STATUS = '" + status + "' and C.IS_PROCESSED = 1";
			} else {
				tempQuery = tempQuery + " and (B.STATUS = '" + PMCConstant.TASK_STATUS_READY + "' or (B.STATUS = '"
						+ PMCConstant.TASK_STATUS_RESERVED + "' and C.IS_PROCESSED = 1))";
			}
		}
		tempQuery = tempQuery + "  and A.status='" + PMCConstant.PROCESS_STATUS_IN_PROGRESS + "'";
		query = query + tempQuery + groupQuery;
		logger.error("getUserList - " + query);
		Query q = this.getSession().createSQLQuery(query);
		List<Object[]> resultList = q.list();
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getTaskCountWithUser(String processName, String status, Map<String, List<Date>> segmentMap,
			List<String> userList, String requestId, String labelValue) {
		StringBuffer userQuery = new StringBuffer();
		if (!ServicesUtil.isEmpty(userList)) {
			for (int i = 0; i < userList.size(); i++) {
				if (i == userList.size() - 1)
					userQuery.append(" '").append(userList.get(i).trim()).append("'");
				else if (i == 0)
					userQuery.append("'").append(userList.get(i).trim()).append("',");
				else
					userQuery.append(" '").append(userList.get(i).trim()).append("',");
			}
		}
		DateFormat newDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Iterator<Entry<String, List<Date>>> it = segmentMap.entrySet().iterator();
		int count = 0;
		String query = "";

		while (it.hasNext()) {
			++count;
			Entry<String, List<Date>> entry = it.next();
			String range = (String) entry.getKey();
			List<Date> dateRange = (List<Date>) entry.getValue();
			String tempQuery = "";

			query = query + " SELECT C.TASK_OWNER AS OWNER,'" + range
					+ "' AS DATE_RANGE, COUNT(C.TASK_OWNER) AS TASK_COUNT, C.TASK_OWNER_DISP AS OWNER_NAME from PROCESS_EVENTS A left join TASK_EVENTS B on A.PROCESS_ID = B.PROCESS_ID left join TASK_OWNERS C on B.EVENT_ID = C.EVENT_ID WHERE B.CREATED_AT BETWEEN '"
					+ newDf.format(dateRange.get(1)) + "' and '" + newDf.format(dateRange.get(0)) + "'";

			if (!ServicesUtil.isEmpty(userList))
				query = query + " and C.TASK_OWNER IN (" + userQuery.toString().trim() + ")";
			String groupQuery = " group by C.TASK_OWNER, C.TASK_OWNER_DISP";
			if (!ServicesUtil.isEmpty(processName) && !processName.equals(PMCConstant.SEARCH_ALL)) {
				tempQuery = tempQuery
						+ " and A.PROCESS_ID IN (select D.process_id from PROCESS_EVENTS D where D.name IN(' "
						+ processName + "'))";
			}
			if (!ServicesUtil.isEmpty(status)) {
				if (PMCConstant.SEARCH_READY.equalsIgnoreCase(status)) {
					tempQuery = tempQuery + " and B.STATUS = '" + status + "'";
				} else if (PMCConstant.SEARCH_RESERVED.equalsIgnoreCase(status)) {
					tempQuery = tempQuery + " and B.STATUS = '" + status + "' and C.IS_PROCESSED = 1";
				} else {
					tempQuery = tempQuery + " and (B.STATUS = '" + PMCConstant.TASK_STATUS_READY + "' or (B.STATUS = '"
							+ PMCConstant.TASK_STATUS_RESERVED + "' and C.IS_PROCESSED = 1))";
				}
			}
			if (!ServicesUtil.isEmpty(requestId)) {
				tempQuery = tempQuery + " and A.REQUEST_ID = '" + requestId + "'";
			}
			if (!ServicesUtil.isEmpty(labelValue)) {
				tempQuery = tempQuery + " and A.SUBJECT like '%" + labelValue + "%'";
			}
			tempQuery = tempQuery + "  and A.status='" + PMCConstant.PROCESS_STATUS_IN_PROGRESS + "'";
			query = query + tempQuery + groupQuery;

			if (count < segmentMap.entrySet().size()) {
				query = query + " UNION";
			}
		}
		logger.error("End Query - " + query);
		Query q = this.getSession().createSQLQuery(query);
		List<Object[]> resultList = q.list();
		return resultList;
	}

	public TaskOwnersDto saveOrUpdateTaskOwner(TaskOwnersDto dto) {
		try {
			this.getSession().saveOrUpdate(importDto(dto));
			return dto;
		} catch (Exception e) {
			logger.error("[PMC] TaskOwnersDao saveOrUpdateTaskOwners():");
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public List<TaskOwnersDto> getTaskOwners(String taskInstanceId) {

		String query = "SELECT C.TASK_OWNER AS OWNER, C.TASK_OWNER_DISP AS OWNER_NAME from TASK_OWNERS C  WHERE  C.EVENT_ID='"
				+ taskInstanceId + "'";
		logger.error("[PMC][ TaskOwnersDao][getTaskOwners][ End Query] - " + query);
		Query q = this.getSession().createSQLQuery(query);
		List<Object[]> resultList = q.list();
		if (!ServicesUtil.isEmpty(resultList)) {
			List<TaskOwnersDto> dtoList = new ArrayList<TaskOwnersDto>();
			for (Object[] obj : resultList) {
				TaskOwnersDto dto = new TaskOwnersDto();
				if (!ServicesUtil.isEmpty(obj[0]))
					dto.setTaskOwner((String) obj[0]);
				if (!ServicesUtil.isEmpty(obj[1]))
					dto.setTaskOwnerDisplayName((String) obj[1]);
				dtoList.add(dto);
			}

			return dtoList;
		}
		return null;
	}

	public String deleteInstance(TaskOwnersDo entity) {
		try {
			this.getSession().delete(exportDto(entity));
			return "SUCCESS";
		} catch (Exception e) {
			logger.error("[PMC][TaskOwnersDao][allOwnersInstance][error] " + e.getMessage());
		}
		return "FAILURE";
	}

	public String createTaskOwnerInstance(TaskOwnersDto dto) {
		try {
			this.getSession().save(dto);
			return "SUCCESS";
		} catch (Exception e) {
			logger.error("[PMC][ConsumeODataFacade][createTaskOwnerInstance][error] " + e.getMessage());
		}
		return "FAILURE";
	}

	public String updateTaskOwnerInstance(TaskOwnersDto dto) {
		try {
			this.getSession().update(dto);
			return "SUCCESS";
		} catch (Exception e) {
			logger.error("[PMC][ConsumeODataFacade][updateTaskOwnerInstance][error] " + e.getMessage());
		}
		return "FAILURE";
	}

	@SuppressWarnings("unchecked")
	public List<TaskOwnersDo> getOwnerInstances(String instanceId) {
		Query query = this.getSession()
				.createSQLQuery("select to from TaskOwnersDo to where to.taskOwnersDoPK.eventId =:instanceId");
		query.setParameter("instanceId", instanceId);

		List<TaskOwnersDo> dos = (List<TaskOwnersDo>) query.list();

		if (dos.size() > 0) {
			return dos;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public String deleteSubstitutedTasks(String owner, List<String> existingList) {
		// System.err.println("[PMC][TaskOwnersDao][getInstanceByOwner]initiated
		// with "+owner +"existing List "+existingList);
		try {
			Query query = this.getSession().createSQLQuery(
					"select te from TaskOwnersDo te where te.taskOwnersDoPK.taskOwner = :owner and te.isSubstituted = :isSubstitutedTrue");
			query.setParameter("owner", owner);
			query.setParameter("isSubstitutedTrue", true);
			List<TaskOwnersDo> ownerdos = (List<TaskOwnersDo>) query.list();
			if (!ServicesUtil.isEmpty(ownerdos)) {
				for (TaskOwnersDo entity : ownerdos) {

					if (!ServicesUtil.isEmpty(existingList)
							&& !existingList.contains(entity.getTaskOwnersDoPK().getEventId()))
						this.getSession().delete(exportDto(entity));
				}
			}
			return "SUCCESS";
		} catch (Exception e) {
			logger.error("[PMC][TaskOwnersDao][deleteSubstitutedTasks][error] " + e.getMessage());
		}
		return "FAILURE";
	}

	public String getOwnerName(String ownerId) {

		try {
			Query query = this.getSession().createQuery(
					"select distinct tw.taskOwnerDisplayName from TaskOwnersDo tw where tw.taskOwnersDoPK.taskOwner =:ownerId");
			query.setParameter("ownerId", ownerId);
			logger.error("Query: " + query);
			return (String) query.uniqueResult();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	public String setTaskOwnersToReady(String instanceId) {
		try {
			String queryString = "Update TaskOwnersDo ts set ts.isProcessed = :isProcessed where ts.taskOwnersDoPK.eventId = '"
					+ instanceId + "' and ts.isProcessed =:isProcessedTrue ";
			Query q = this.getSession().createSQLQuery(queryString);
			q.setParameter("isProcessed", false);
			q.setParameter("isProcessedTrue", true);
			int i = q.executeUpdate();
			return "SUCCESS";
		} catch (Exception e) {
			logger.error("[PMC][TaskOwnersDao][setTaskOwnersToReady][error]" + e.getMessage());
		}
		return "FAILURE";
	}

	public String updateTaskOwnerToReserved(String instanceId, String user, String taskOwnerDisplayName) {
		try {
			String queryString = "Update TaskOwnersDo ts set ts.isProcessed = :isProcessed where ts.taskOwnersDoPK.eventId = '"
					+ instanceId + "' and ts.taskOwnersDoPK.taskOwner = '" + user + "'";
			Query q = this.getSession().createSQLQuery(queryString);
			q.setParameter("isProcessed", true);
			int i = q.executeUpdate();
			if (i > 0)
				return "SUCCESS";
			else {
				TaskOwnersDto dto = new TaskOwnersDto();
				dto.setEventId(instanceId);
				dto.setIsProcessed(true);
				dto.setTaskOwner(user);
				dto.setTaskOwnerDisplayName(taskOwnerDisplayName);
				if (createOwner(dto).equals("SUCCESS")) {
					return "SUCCESS";
				}
			}
		} catch (Exception e) {
			logger.error("[PMC][TaskOwnersDao][updateTaskOwnerToReserved][error]" + e.getMessage());
		}
		return "FAILURE";
	}

	public String createOwner(TaskOwnersDto dto) {
		try {
			this.getSession().save(dto);
			return "SUCCESS";
		} catch (Exception e) {
			logger.error("[PMC][TaskOwnersDao][createOwner][error]" + e.getMessage());
		}
		return "FAILURE";

	}

	public String checkIfUserExists(String userId, String instanceId) {

		try {
			String queryString = "select count(te) from TaskOwnersDo te where te.taskOwnersDoPK.taskOwner ='" + userId
					+ "' and te.taskOwnersDoPK.eventId = '" + instanceId + "'";
			Query q = this.getSession().createSQLQuery(queryString);
			Long count = (Long) q.uniqueResult();
			if (count == 0) {
				return PMCConstant.NOT_OWNER;
			} else if (count > 0) {
				return "SUCCESS";
			}
		} catch (Exception e) {
			logger.error("[PMC][TaskOwnersDao][checkIfUserIsOwner][error]" + e.getMessage());
		}
		return "FAILURE";

	}

	public String checkIfUserIsOwner(String userId, String instanceId) {

		try {
			String queryString = "select count(te) from TaskOwnersDo te where te.taskOwnersDoPK.taskOwner ='" + userId
					+ "' and te.taskOwnersDoPK.eventId = '" + instanceId + "' and  ts.isProcessed = :isProcessed ";
			Query q = this.getSession().createSQLQuery(queryString);
			q.setParameter("isProcessed", true);
			Long count = (Long) q.uniqueResult();
			if (count == 0) {
				return PMCConstant.NOT_OWNER;
			} else if (count > 0) {
				return "SUCCESS";
			}
		} catch (Exception e) {
			logger.error("[PMC][TaskOwnersDao][checkIfUserIsOwner][error]" + e.getMessage());
		}
		return "FAILURE";

	}

	@SuppressWarnings("unchecked")
	public List<TaskOwnersDo> getTaskOwnersDetailsByEventId(String eventId) {
		Query query = this.getSession()
				.createQuery("select tw from TaskOwnersDo tw where tw.taskOwnersDoPK.eventId =:eventId");
		query.setParameter("eventId", eventId);
		logger.error("Query: " + query);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public TaskOwnersDto getTaskOwnersDetailsByOwner(String ownerId) {
		Query query = this.getSession().createQuery("select tw from TaskOwnerDo tw.taskOwnerDoPK.taskOwner=:ownerId");
		logger.error("Query: " + query);
		List<TaskOwnersDo> presentOwners = query.list();
		if (presentOwners.size() != 0) {
			return exportDto(presentOwners.get(0));
		} else
			return null;
	}

	public String deleteUser(String taskId, String owner) {
		try {
			String deleteQuery = "delete from task_owners where event_id='" + taskId + "' ";
			if (!ServicesUtil.isEmpty(owner)) {
				deleteQuery += " and task_owner='" + owner + "'";
			}
			Query q = this.getSession().createSQLQuery(deleteQuery);
			int result = (Integer) q.executeUpdate();
			if (result > 0)
				return PMCConstant.SUCCESS;
		} catch (Exception e) {
			logger.error("[PMC][TaskOwnersDao][deleteUser][error]" + e.getMessage());
		}
		return PMCConstant.FAILURE;
	}
	
	public void saveOrUpdateOwners(List<TaskOwnersDto> owners) {
		Session session = null;
		if(!ServicesUtil.isEmpty(owners) && owners.size() > 0) {
			session = this.getSession();
			for(int i = 0; i < owners.size(); i++) {
				TaskOwnersDo ownersDo = importDto(owners.get(i));
				try {
					if(!ServicesUtil.isEmpty(ownersDo)){
						session.saveOrUpdate(ownersDo);
//						if(i % _HIBERNATE_BATCH_SIZE == 0 && i > 0) {
//							session.flush();
//							session.clear();
//						}
					} else {
						System.err.println("Can not insert as primary key columns are NULL : "+owners.get(i));
					}
				} catch (Exception ex) {
					System.err.println("Can not insert owner : "+owners.get(i) +" With exception : "+ex.getMessage());
				}
			}
			/*if(!session.getTransaction().wasCommitted()) {
				session.flush();
			}*/
//			session.close();
		}
	}
	
	public int executeUpdateQuery(String query) {
		int update = 0;
		update = this.getSession().createSQLQuery(query).executeUpdate();
		return update;
	}
	
	public int changeOwnerStatus(TaskOwnersDo ownersDo) {
		Session session = this.getSession();
		try {
			TaskOwnersDo owner = (TaskOwnersDo) session.load(TaskOwnersDo.class, ownersDo.getTaskOwnersDoPK());
			owner.setIsProcessed(ownersDo.getIsProcessed());
			session.saveOrUpdate(owner);
			return 1;
		} catch (Exception ex) {
			System.err.println("Exception persisting data to DB : "+ex.getMessage());
			return 0;
		}
	}
	
	public int changeSFOwnerStatus(TaskOwnersDo ownersDo) {
		Session session = this.getSession();
		try {
			TaskOwnersDo owner = (TaskOwnersDo) session.load(TaskOwnersDo.class, ownersDo.getTaskOwnersDoPK());
			owner.setIsProcessed(ownersDo.getIsProcessed());
			owner.setEnRoute(ownersDo.getEnRoute());
			session.saveOrUpdate(owner);
			return 1;
		} catch (Exception ex) {
			System.err.println("Exception persisting data to DB : "+ex.getMessage());
			return 0;
		}
	}
	
	public int removeSFOwner(TaskOwnersDo ownersDo) {
		Session session = this.getSession();
		try {
			session.delete(ownersDo);
			return 1;
		} catch (Exception ex) {
			System.err.println("Exception persisting data to DB : "+ex.getMessage());
			return 0;
		}
	}
	
	//newly added from agco demo
	
	@SuppressWarnings("unchecked")
	public List<String> getSubstitutedUserTask(String userId, String substitutingUser) {
		List<String> taskList = null;
		try {
			String queryString = ""
					+ "select distinct tw1.event_id from task_owners tw1 join task_events te1 "
					+ "on te1.event_id=tw1.event_id where task_owner='"+userId+"' "
					+ "AND status in ('READY') and IS_SUBSTITUTED IS NULL and tw1.event_id not in "
					+ "(select distinct tw.event_id from task_owners tw "
					+ "join task_events te "
					+ "on te.event_id=tw.event_id "
					+ "JOIN PROCESS_CONFIG_TB Pct ON TE.PROC_NAME = PCT.PROCESS_NAME "
					+ "where tw.task_owner in ('"+userId+"','"+substitutingUser+"') AND status in ('READY') "
					+ "and (TW.IS_SUBSTITUTED IS NULL) group by tw.event_id having count(*)=2)";
			Query query = this.getSession().createSQLQuery(queryString);
			taskList = query.list();
		} catch (Exception e) {
			System.err
					.println("[Workbox][SubstitutionRuleDao][getSubstitutedUserTask][Error]" + e.getLocalizedMessage());
		}
		return taskList;
	}
	
	public String deleteSubstitutionOwner(String taskOwner, List<String> taskIds) {
		try {
			if (!ServicesUtil.isEmpty(taskIds) && !ServicesUtil.isEmpty(taskOwner)) {
				String queryString = " delete from task_owners where upper(task_owner)=upper('" + taskOwner
						+ "') and event_id in (" + ServicesUtil.getStringFromList(taskIds) + ") and is_substituted=1 ";
				int resultRows = this.getSession().createSQLQuery(queryString).executeUpdate();
				if (resultRows > 0)
					return PMCConstant.SUCCESS;
			}
		} catch (Exception e) {
			System.err.println(
					"[Workbox][SubstitutionRuleDao][deleteSubstitutionOwner][Error]" + e.getLocalizedMessage());
		}
		return PMCConstant.FAILURE;
	}
}