package oneapp.workbox.services.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import oneapp.workbox.services.dto.ProcessDropdownResponse;
import oneapp.workbox.services.dto.ProcessDropdownResponse.ProcessDropdown;
import oneapp.workbox.services.dto.ProjectDetail;
import oneapp.workbox.services.entity.ProcessDetail;
import oneapp.workbox.services.entity.ProcessEventsDo;
import oneapp.workbox.services.entity.ProjectProcessMapping;
import oneapp.workbox.services.entity.RecentProjectAccess;
import oneapp.workbox.services.entity.TaskDetail;
import oneapp.workbox.services.entity.TaskUserDetails;
import oneapp.workbox.services.util.ServicesUtil;

@Repository
@Transactional
public class ProjectProcessDao {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public ProjectProcessMapping saveProjectProcessMap(ProjectProcessMapping prjPrcMap) {
		try {
			this.getSession().saveOrUpdate(prjPrcMap);
		} catch (Exception ex) {
			System.err.println("Exception saving Project process map : " + ex.getMessage());
		}
		return prjPrcMap;
	}

	public void saveOrUpdatePrjPrcMaps(List<ProjectProcessMapping> prjPrcMaps) {
		Session session = null;
		if (!ServicesUtil.isEmpty(prjPrcMaps) && prjPrcMaps.size() > 0) {
			session = this.getSession();
			for (int i = 0; i < prjPrcMaps.size(); i++) {
				session.saveOrUpdate(prjPrcMaps.get(i));
			}
		}
	}

	public void saveOrUpdateProcessDetails(List<ProcessDetail> processDetails) {
		Session session = null;
		if (!ServicesUtil.isEmpty(processDetails) && processDetails.size() > 0) {
			session = this.getSession();
			for (ProcessDetail processDetail : processDetails) {
				try {
					if(!ServicesUtil.isEmpty(processDetail) && !ServicesUtil.isEmpty(processDetail.getProjectId()))
						session.saveOrUpdate(processDetail);
				} catch (Exception ex) {
					System.err.println("Object Already being persisted : "+ex.getMessage());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<ProcessDetail> getProcessDetails(String projectId) {

		List<ProcessDetail> processDetails = null;
		ProcessDetail processDetail = null;

		if (!ServicesUtil.isEmpty(projectId)) {
			String query = "SELECT PE.PROCESS_ID, PD.PROJECT_ID, PD.LEAD_CATEGORY, PD.MATERIAL_TYPE, PD.MATERIAL_UNIQUE_ID, "
					+ " PD.MATERIAL_ID, PD.MATERIAL_DESCRIPTION, PD.REQUESTED_BY, PD.LEAD_COUNTRY, PD.PROJECT_DESCRIPTION, "
					+ " PD.REQUIRED_TASK_CSV, PD.REGION, PE.STATUS, PE.REQUEST_ID, PE.NAME, PE.SUBJECT FROM \"NPI_WORKBOX_USER\".\"PROCESS_EVENTS\" PE "
					+ " LEFT JOIN \"NPI_WORKBOX_USER\".\"PROCESS_DETAIL\" PD ON PE.PROCESS_ID = PD.PROCESS_ID "
					+ " JOIN \"NPI_WORKBOX_USER\".\"PROJECT_PROCESS_MAP\" PM ON PE.PROCESS_ID = PM.PROCESS_ID "
					+ " WHERE PM.PROJECT_ID = '" + projectId + "'";

			List<Object[]> resultList = this.getSession().createSQLQuery(query).list();
			if (!ServicesUtil.isEmpty(resultList) && resultList.size() > 0) {
				processDetails = new ArrayList<ProcessDetail>();
				for (Object[] object : resultList) {
					processDetail = new ProcessDetail();

					processDetail.setProcessId(ServicesUtil.asString(object[0]));
					processDetail.setProjectId(ServicesUtil.asString(object[1]));
					processDetail.setLeadCategory(ServicesUtil.asString(object[2]));
					processDetail.setMaterialType(ServicesUtil.asString(object[3]));
					processDetail.setMaterialUniqueId(ServicesUtil.asString(object[4]));
					processDetail.setMaterialId(ServicesUtil.asString(object[5]));
					processDetail.setMaterialDescription(ServicesUtil.asString(object[6]));
					processDetail.setRequestedBy(ServicesUtil.asString(object[7]));
					processDetail.setLeadCountry(ServicesUtil.asString(object[8]));
					processDetail.setProjectDescription(ServicesUtil.asString(object[9]));
					processDetail.setRequiredTaskCsv(ServicesUtil.asString(object[10]));
					processDetail.setRegion(ServicesUtil.asString(object[11]));
					processDetail.setProcessStatus(ServicesUtil.asString(object[12]));
					processDetail.setRequestId(ServicesUtil.asString(object[13]));
					processDetail.setProcessName(ServicesUtil.asString(object[14]));
					processDetail.setProcessSubject(ServicesUtil.asString(object[15]));
					
					processDetails.add(processDetail);
				}
			}
		}

		return processDetails;
	}

	@SuppressWarnings("unchecked")
	public List<ProjectDetail> getProjectDetails(String projectId, String userId, String projectCreatedFrom,
			String projectCreatedTo, Boolean isRecent) {

		String query = null;
		List<ProjectDetail> projectDetails = null;
		try {
			if (ServicesUtil.isEmpty(projectId) && ServicesUtil.isEmpty(projectCreatedFrom)
					&& ServicesUtil.isEmpty(projectCreatedFrom) && ServicesUtil.isEmpty(projectCreatedTo)) {
				if (isRecent)
					query = "SELECT TOP 6 ";
				else
					query = "SELECT ";
				query += " PRO_ID, DES, REA, PRO_LEA, REG_COD, DAT_CRE, USE_CRE FROM \"NPI\".\"GWF_PRO\" AS PR "
						+ "JOIN \"NPI_WORKBOX_USER\".\"RECENT_PROJECT_ACCESS\" AS RPA ON PR.PRO_ID = RPA.PROJECT_ID"
						+ " ORDER BY RPA.LAST_ACCESSED DESC ";

			} else if (ServicesUtil.isEmpty(projectId) && ServicesUtil.isEmpty(projectCreatedFrom)) {
				query = "SELECT TOP 6 PRO_ID, DES, REA, PRO_LEA, REG_COD, DAT_CRE, USE_CRE FROM \"NPI\".\"GWF_PRO\" AS PR JOIN \"NPI_WORKBOX_USER\".\"RECENT_PROJECT_ACCESS\" AS RPA ON PR.PRO_ID = RPA.PROJECT_ID ";
				if (!ServicesUtil.isEmpty(userId)) {
					query += " WHERE UPPER(RPA.USER_ACCESSED) = UPPER('" + userId + "')";
				}

				query += " ORDER BY RPA.LAST_ACCESSED ";
			} else {
				query = "SELECT PRO_ID, DES, REA, PRO_LEA, REG_COD, DAT_CRE, USE_CRE FROM \"NPI\".\"GWF_PRO\" WHERE ";
				if (!ServicesUtil.isEmpty(projectId)) {
					query += " PRO_ID = '" + projectId + "'";
				}
				DateFormat newDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if (!ServicesUtil.isEmpty(projectCreatedFrom) && !ServicesUtil.isEmpty(projectCreatedTo)) {
					query += " DAT_CRE BETWEEN '" + newDf.format(projectCreatedFrom) + "' AND '"
							+ newDf.format(projectCreatedTo) + "'";
				}
			}

			SimpleDateFormat dateFormatter = null;
			ProjectDetail prjDetail = null;
			System.err.println("[Colgate][WB][ProjectProcessDao][]getProjectDetails][Query]" + query);
			if (!ServicesUtil.isEmpty(query)) {
				projectDetails = new ArrayList<>();
				List<Object[]> resultList = this.getSession().createSQLQuery(query).list();
				if (!ServicesUtil.isEmpty(resultList) && resultList.size() > 0) {
					for (Object[] object : resultList) {
						dateFormatter = new SimpleDateFormat("dd-MMM-yy hh:mm:ss a");
						prjDetail = new ProjectDetail();
						prjDetail.setProjectId(ServicesUtil.asInteger(object[0]));
						prjDetail.setProjectDescription(ServicesUtil.asString(object[1]));
						prjDetail.setReason(ServicesUtil.asString(object[2]));
						prjDetail.setProjectLead(ServicesUtil.asString(object[3]));
						prjDetail.setRegionCode(ServicesUtil.asString(object[4]));
						prjDetail.setCreatedAt(ServicesUtil.resultAsDate(object[5]));
						prjDetail.setUserCreated(ServicesUtil.asString(object[6]));

						if (!ServicesUtil.isEmpty(prjDetail.getCreatedAt())) {
							prjDetail.setCreatedAtInString(dateFormatter.format(prjDetail.getCreatedAt()));
						}

						projectDetails.add(prjDetail);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("[Colgate][WB][ProjectProcessDao][getProjectDetails][Error]" + e.getLocalizedMessage());
		}
		return projectDetails;
	}

	public String getProcessSubject(String processId) {
		Criteria criteria = this.getSession().createCriteria(ProcessEventsDo.class);
		criteria.setProjection(Projections
				.property("subject"));
		criteria.add(Restrictions.eq("processId", processId));
		return ServicesUtil.asString(criteria.uniqueResult());
	}
	
	public String getProcessStatus(String processId) {
		Criteria criteria = this.getSession().createCriteria(ProcessEventsDo.class);
		criteria.setProjection(Projections
				.property("status"));
		criteria.add(Restrictions.eq("processId", processId));
		return ServicesUtil.asString(criteria.uniqueResult());
	}
	
	@SuppressWarnings("unchecked")
	public ProcessDetail getProcessDetail(String processId) {
		ProcessDetail processDetail = null;
		String query = "SELECT PE.PROCESS_ID, PD.PROJECT_ID, PD.LEAD_CATEGORY, PD.MATERIAL_TYPE, PD.MATERIAL_UNIQUE_ID, "
				+ " PD.MATERIAL_ID, PD.MATERIAL_DESCRIPTION, PD.REQUESTED_BY, PD.LEAD_COUNTRY, PD.PROJECT_DESCRIPTION, "
				+ " PD.REQUIRED_TASK_CSV, PD.REGION, PE.STATUS, PE.REQUEST_ID, PE.NAME FROM \"NPI_WORKBOX_USER\".\"PROCESS_EVENTS\" PE "
				+ " LEFT JOIN \"NPI_WORKBOX_USER\".\"PROCESS_DETAIL\" PD ON PE.PROCESS_ID = PD.PROCESS_ID "
				+ " WHERE PE.PROCESS_ID = '" + processId + "'";
		List<Object[]> list = this.getSession().createSQLQuery(query).list();
		if (!ServicesUtil.isEmpty(list) && list.size() >= 0) {
			processDetail = new ProcessDetail();
			Object[] detail = list.get(0);

			processDetail.setProcessId(ServicesUtil.asString(detail[0]));
			processDetail.setProjectId(ServicesUtil.asString(detail[1]));
			processDetail.setLeadCategory(ServicesUtil.asString(detail[2]));
			processDetail.setMaterialType(ServicesUtil.asString(detail[3]));
			processDetail.setMaterialUniqueId(ServicesUtil.asString(detail[4]));
			processDetail.setMaterialId(ServicesUtil.asString(detail[5]));
			processDetail.setMaterialDescription(ServicesUtil.asString(detail[6]));
			processDetail.setRequestedBy(ServicesUtil.asString(detail[7]));
			processDetail.setLeadCountry(ServicesUtil.asString(detail[8]));
			processDetail.setProjectDescription(ServicesUtil.asString(detail[9]));
			processDetail.setRequiredTaskCsv(ServicesUtil.asString(detail[10]));
			processDetail.setRegion(ServicesUtil.asString(detail[11]));
			processDetail.setProcessStatus(ServicesUtil.asString(detail[12]));
			processDetail.setRequestId(ServicesUtil.asString(detail[13]));
			processDetail.setProcessName(ServicesUtil.asString(detail[14]));
		}
		return processDetail;
	}

	@SuppressWarnings("unchecked")
	public List<TaskDetail> getTaskDetails(String processId) {

		List<TaskDetail> taskDetails = null;
		TaskDetail taskDetail = null;
		String query = "SELECT TE.EVENT_ID, TE.STATUS, PD.MATERIAL_ID, PD.MATERIAL_UNIQUE_ID, PD.MATERIAL_DESCRIPTION, TE.NAME, TE.CREATED_AT, TE.DESCRIPTION, TE.SUBJECT "
				+ "FROM \"NPI_WORKBOX_USER\".\"TASK_EVENTS\" TE JOIN \"NPI_WORKBOX_USER\".\"PROCESS_DETAIL\" PD ON TE.PROCESS_ID = PD.PROCESS_ID "
				+ "WHERE TE.PROCESS_ID = '" + processId + "'";

		SimpleDateFormat dateFormatter = null;
		List<Object[]> list = this.getSession().createSQLQuery(query).list();
		if (!ServicesUtil.isEmpty(list) && list.size() > 0) {
			taskDetails = new ArrayList<>();
			for (Object[] object : list) {
				dateFormatter = new SimpleDateFormat("dd-MMM-yy hh:mm:ss a");
				taskDetail = new TaskDetail();

				taskDetail.setTaskId(ServicesUtil.asString(object[0]));
				taskDetail.setTaskStatus(ServicesUtil.asString(object[1]));
				taskDetail.setMaterialId(ServicesUtil.asString(object[2]));
				taskDetail.setMaterialUniqueId(ServicesUtil.asString(object[3]));
				taskDetail.setMaterialDescription(ServicesUtil.asString(object[4]));
				taskDetail.setTaskName(ServicesUtil.asString(object[5]));
				taskDetail.setTaskCreatedAt(ServicesUtil.resultAsDate(object[6]));
				taskDetail.setTaskDescription(ServicesUtil.asString(object[7]));
				taskDetail.setTaskSubject(ServicesUtil.asString(object[8]));
					
				if (!ServicesUtil.isEmpty(taskDetail.getTaskCreatedAt())) {
					taskDetail.setTaskCreatedAtString(dateFormatter.format(taskDetail.getTaskCreatedAt()));
				}

				List<TaskUserDetails> taskUserDetails = getTaskUserDetails(taskDetail.getTaskId());
				taskDetail.setUserDetails(taskUserDetails);

				int userDetailsCount = 0;
				if (!ServicesUtil.isEmpty(taskUserDetails) && taskUserDetails.size() > 0) {
					userDetailsCount = taskUserDetails.size();
				}
				taskDetail.setTaskUsersCount(userDetailsCount);

				taskDetails.add(taskDetail);
			}
		}
		return taskDetails;
	}

	@SuppressWarnings("unchecked")
	private List<TaskUserDetails> getTaskUserDetails(String taskId) {

		List<TaskUserDetails> userDetails = null;
		TaskUserDetails userDetail = null;
		String query = "SELECT TASK_OWNER, TASK_OWNER_DISP, TASK_OWNER_EMAIL "
				+ " FROM \"NPI_WORKBOX_USER\".\"TASK_OWNERS\" WHERE EVENT_ID = '" + taskId + "'";

		List<Object[]> list = this.getSession().createSQLQuery(query).list();

		if (!ServicesUtil.isEmpty(list) && list.size() > 0) {
			userDetails = new ArrayList<>();
			for (Object[] object : list) {
				userDetail = new TaskUserDetails();

				userDetail.setUserId(ServicesUtil.asString(object[0]));
				userDetail.setUserName(ServicesUtil.asString(object[1]));
				userDetail.setUserEmail(ServicesUtil.asString(object[2]));

				userDetails.add(userDetail);
			}
		}
		return userDetails;
	}

	@SuppressWarnings("unchecked")
	public List<ProcessDropdown> getProcessDropdown(String projectId) {

		List<ProcessDropdown> processDropdown = null;
		ProcessDropdown process = null;
		String query = "SELECT PE.PROCESS_ID, PE.REQUEST_ID, PD.MATERIAL_UNIQUE_ID, PE.SUBJECT "
				+ " FROM \"NPI_WORKBOX_USER\".\"PROCESS_EVENTS\" PE JOIN \"NPI_WORKBOX_USER\".\"PROCESS_DETAIL\" PD "
				+ " ON PE.PROCESS_ID = PD.PROCESS_ID WHERE PD.PROJECT_ID = '" + projectId + "'";

		List<Object[]> resultList = this.getSession().createSQLQuery(query).list();
		if (!ServicesUtil.isEmpty(resultList) && resultList.size() > 0) {
			processDropdown = new ArrayList<>();

			for (Object[] object : resultList) {
				process = new ProcessDropdownResponse().new ProcessDropdown();

				process.setProcessId(ServicesUtil.asString(object[0]));
				process.setRequestId(ServicesUtil.asString(object[1]));
				process.setMaterialId(ServicesUtil.asString(object[2]));
				process.setProcessSubject(ServicesUtil.asString(object[3]));
				
				processDropdown.add(process);
			}
		}

		return processDropdown;
	}

	public void saveRecentProject(String ownerSearched, String projectId) {

		RecentProjectAccess rpa = new RecentProjectAccess(projectId, new Date(), ownerSearched);
		try {
			this.getSession().saveOrUpdate(rpa);
		} catch (Exception ex) {
			System.err.println("Exception saving Recent Project search : " + ex.getMessage());
		}

	}
	
	@SuppressWarnings("unchecked")
	public List<String> getAllProcessForProjectId(String projectId) {
		
		Criteria criteria = this.getSession().createCriteria(ProjectProcessMapping.class);
		criteria.setProjection(Projections.property("processId"));
		criteria.add(Restrictions.eq("projectId", projectId));
		List<String> resultList = criteria.list();
		
		return resultList;
	}
	
}
