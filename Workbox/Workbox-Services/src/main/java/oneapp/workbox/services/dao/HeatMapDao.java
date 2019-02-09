package oneapp.workbox.services.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import oneapp.workbox.services.dto.SearchListDto;
import oneapp.workbox.services.dto.UserWorkloadDto;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;

@Repository
@Transactional
public class HeatMapDao {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	public List<SearchListDto> getSearchList(String type) {
		List<SearchListDto> dtos = new ArrayList<>();
		SearchListDto dto = null;
		String queryString = "";
		if (!ServicesUtil.isEmpty(type)) {
			if (PMCConstant.SEARCH_PROCESS.equals(type)) {
				queryString = "select distinct process_name, process_display_name from process_config_tb order by process_display_name asc ";
			} else if (PMCConstant.SEARCH_STATUS.equals(type)) {
				queryString = "select distinct constant_name,constant_value from cross_constants where CONSTANT_ID='"
						+ PMCConstant.SEARCH_STATUS + "'";
			}
		}
		Query query = this.getSession().createSQLQuery(queryString);
		List<Object[]> resultList = query.list();
		for (Object[] obj : resultList) {
			dto = new SearchListDto();
			dto.setKey((String) obj[0]);
			dto.setValue((String) obj[1]);
			dtos.add(dto);
		}

		return dtos;
	}

	@SuppressWarnings("unchecked")
	public List<UserWorkloadDto> getUserWorkload(String processName, String requestId, String taskStatus) {
		List<UserWorkloadDto> dtos = new ArrayList<UserWorkloadDto>();
		UserWorkloadDto dto = null;
		try {
			String searchParameterQuery = "";
			String queryString = "select distinct task_owner_disp,task_owner,count(tw.event_id)"
					+ " from process_events pe join task_events te on te.process_id=pe.process_id "
					+ " join task_owners tw on tw.event_id=te.event_id  ";
			if (!ServicesUtil.isEmpty(taskStatus)) {
				if (PMCConstant.SEARCH_RESERVED.equals(taskStatus))
					queryString += " and tw.task_owner = te.cur_proc ";
				if (!PMCConstant.SEARCH_ALL.equals(taskStatus))
					searchParameterQuery += " te.status='" + taskStatus + "' and ";
			}
			if (!ServicesUtil.isEmpty(processName) && !PMCConstant.SEARCH_ALL.equals(processName))
				searchParameterQuery += " te.proc_name='" + processName + "' and ";
			if (!ServicesUtil.isEmpty(requestId) && !PMCConstant.SEARCH_ALL.equals(requestId))
				searchParameterQuery += " pe.request_id='" + requestId + "' and ";

			if (!ServicesUtil.isEmpty(searchParameterQuery))
				searchParameterQuery = " Where " + searchParameterQuery.substring(0, searchParameterQuery.length() - 4);
			queryString += searchParameterQuery + " group by tw.task_owner,tw.task_owner_disp ";
			
			Query query = this.getSession().createSQLQuery(queryString);
			 List<Object[]> resultList = query.list();
			System.err.println("[PMC][heatMap Query]" + queryString);
			//List<Object[]> resultList = getTaskCountWithOwners(processName, requestId, taskStatus);
			for (Object[] obj : resultList) {
				dto = new UserWorkloadDto();
				dto.setUserName((String) obj[0]);
				dto.setUserId((String) obj[1]);
				dto.setNoOfTask((BigInteger) obj[2]);
				dtos.add(dto);
			}
		} catch (Exception e) {
			System.err.println("[PMC][HeatMapDao][getuserWorkLoad][error]" + e.getMessage());
		}

		return dtos;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getTaskCountWithOwners(String processName, String requestId, String status){
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
		Query q = this.getSession().createSQLQuery(query);
		List<Object[]> resultList = q.list();
		return resultList;
	}

}