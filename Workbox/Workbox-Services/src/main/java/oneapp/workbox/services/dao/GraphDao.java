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

import oneapp.workbox.services.dto.GraphDto;
import oneapp.workbox.services.dto.TaskNameCountDto;
import oneapp.workbox.services.dto.TotalActiveTaskDto;
import oneapp.workbox.services.dto.UserWorkCountDto;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;

@Repository
@Transactional
public class GraphDao {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public GraphDto getDetails(String processName, String graphType, String duration, String userId) {
		GraphDto dto = new GraphDto();
		try {
			BigInteger completedWithSLA = getCount(PMCConstant.COMPLETED_WITH_SLA);
			BigInteger completedWithoutSLA = getCount(PMCConstant.COMPLETED_WITHOUT_SLA);
			BigInteger reservedWithSLA = getCount(PMCConstant.RESERVED_WITH_SLA);
			BigInteger reservedWithoutSLA = getCount(PMCConstant.RESERVED_WITHOUT_SLA);
			BigInteger readyWithSLA = getCount(PMCConstant.READY_WITH_SLA);
			BigInteger readyWithoutSLA = getCount(PMCConstant.READY_WITHOUT_SLA);

			dto.setTotalActiveTask(readyWithoutSLA.add(readyWithSLA).add(reservedWithoutSLA).add(reservedWithSLA));
			dto.setOpenTask(readyWithoutSLA.add(readyWithSLA));
			dto.setPendingTask((reservedWithoutSLA).add(reservedWithSLA));
			dto.setSlaBreachedTask(readyWithoutSLA.add(reservedWithoutSLA).add(completedWithoutSLA));
			dto.setMyTask(getCount(userId));
			dto.setTotalActiveTaskList(getTotalActiveTaskList());
			dto.setUserWorkCountList(getUserWorkCountList(processName));
			dto.setTaskCompletionTrendList(getTaskCompletionRange(duration));

			List<TaskNameCountDto> list = new ArrayList<TaskNameCountDto>();

			list.add(taskNameCountDto(PMCConstant.COMPLETED_WITH_SLA, completedWithSLA));
			list.add(taskNameCountDto(PMCConstant.COMPLETED_WITHOUT_SLA, completedWithoutSLA));
			list.add(taskNameCountDto(PMCConstant.RESERVED_WITH_SLA, reservedWithSLA));
			list.add(taskNameCountDto(PMCConstant.RESERVED_WITHOUT_SLA, reservedWithoutSLA));
			list.add(taskNameCountDto(PMCConstant.READY_WITH_SLA, readyWithSLA));
			list.add(taskNameCountDto(PMCConstant.READY_WITHOUT_SLA, readyWithoutSLA));

			dto.setTaskDonutList(list);
		} catch (Exception e) {
			System.err.println("[PMC][GRAPH DAO]" + e.getMessage() + e.getLocalizedMessage());
		}
		return dto;
	}

	private List<TaskNameCountDto> getTaskDto(List<String> listOfDates, Object[] obj, int value) {

		List<TaskNameCountDto> list = new ArrayList<TaskNameCountDto>();
		TaskNameCountDto dto = null;
		for (int i = 0; i < value; i++) {
			dto = new TaskNameCountDto();
			dto.setStrName(listOfDates.get(i));
			dto.setTaskCount((BigInteger) obj[i]);
			list.add(dto);
		}
		return list;
	}

	private BigInteger getCount(String taskName) {
		Object resultList = null;

		try {
			String queryString = "select count(*) from (select distinct pe.request_id,te.event_id "
					+ "FROM TASK_EVENTS AS te " + "	LEFT OUTER JOIN PROCESS_CONFIG_TB AS Pct "
					+ "	ON TE.PROC_NAME = PCT.PROCESS_NAME 	LEFT OUTER JOIN PROCESS_EVENTS AS pe "
					+ "	ON pe.process_id = te.process_id join " + "	TASK_OWNERS AS tw on tw.event_id=te.event_id";

			if (PMCConstant.COMPLETED_WITH_SLA.equals(taskName))
				queryString += " where te.comp_deadline>te.completed_at and te.status ='COMPLETED'";
			else if (PMCConstant.COMPLETED_WITHOUT_SLA.equals(taskName))
				queryString += " where te.comp_deadline<te.completed_at and te.status ='COMPLETED'";
			else if (PMCConstant.RESERVED_WITH_SLA.equals(taskName))
				queryString += " where current_timestamp < te.comp_deadline and te.status ='RESERVED'";
			else if (PMCConstant.RESERVED_WITHOUT_SLA.equals(taskName))
				queryString += " where current_timestamp > te.comp_deadline and te.status ='RESERVED' ";
			else if (PMCConstant.READY_WITH_SLA.equals(taskName))
				queryString += " where te.comp_deadline > current_timestamp and te.status ='READY' ";
			else if (PMCConstant.READY_WITHOUT_SLA.equals(taskName))
				queryString += " where te.comp_deadline < current_timestamp and te.status ='READY' ";
			else {
				queryString += " WHERE (te.STATUS = 'RESERVED' AND te.CUR_PROC = '" + taskName
						+ "' or te.event_id in (select event_id from task_owners where task_owner='" + taskName
						+ "' and event_id in (select distinct to1.event_id from task_events te1 join PROCESS_CONFIG_TB pc1 on pc1.process_name =te1.proc_name join task_owners to1 on te1.event_id=to1.event_id where te1.status ='READY' group by to1.event_id having count(to1.event_id)=1)))";
//				 queryString += " where te.status in ('RESERVED') and  te.cur_proc='" + taskName + "'";
			}
			queryString += ")";

			Query q = this.getSession().createSQLQuery(queryString);
			resultList = q.uniqueResult();
		} catch (Exception ex) {
			System.err.println("Exception while graphDao getCount : " + ex.getMessage());
		}
		return (BigInteger) resultList;
	}

	private TaskNameCountDto taskNameCountDto(String taskName, BigInteger count) {
		TaskNameCountDto dto = new TaskNameCountDto();
		dto.setStrName(taskName);
		dto.setTaskCount(count);
		return dto;
	}

	private List<TotalActiveTaskDto> getTotalActiveTaskList() {
		List<TotalActiveTaskDto> list = new ArrayList<TotalActiveTaskDto>();
		TotalActiveTaskDto dto = null;

		String queryString = "SELECT TE.PROC_NAME, " + "	PC.PROCESS_DISPLAY_NAME, " + "	( "
				+ "	select count(*) from ( " + "SELECT DISTINCT pe.REQUEST_ID AS REQUEST_ID,te1.event_id from "
				+ "	TASK_EVENTS te1 " + "	LEFT OUTER JOIN PROCESS_CONFIG_TB AS Pct "
				+ "	ON TE1.PROC_NAME = PCT.PROCESS_NAME " + "	LEFT OUTER JOIN TASK_SLA AS ts "
				+ "	ON te1.NAME = ts.TASK_DEF " + "	LEFT OUTER JOIN PROCESS_EVENTS AS pe "
				+ "	ON pe.process_id = te1.process_id " + "	INNER JOIN TASK_OWNERS AS tw "
				+ "	ON tw.event_id = te1.event_id WHERE TE1.PROC_NAME = TE.PROC_NAME "
				+ "			AND CURRENT_TIMESTAMP < ADD_SECONDS(comp_deadline, - (18000)) "
				+ "			AND te1.STATUS <> 'COMPLETED' " + "	) )AS IN_TIME, " + "	( "
				+ "		select count(*) from ( " + "SELECT DISTINCT pe.REQUEST_ID AS REQUEST_ID,te1.event_id from "
				+ "		TASK_EVENTS te1 " + "		LEFT OUTER JOIN PROCESS_CONFIG_TB AS Pct "
				+ "	ON TE1.PROC_NAME = PCT.PROCESS_NAME " + "	LEFT OUTER JOIN TASK_SLA AS ts "
				+ "	ON te1.NAME = ts.TASK_DEF " + "	LEFT OUTER JOIN PROCESS_EVENTS AS pe "
				+ "	ON pe.process_id = te1.process_id " + "	INNER JOIN TASK_OWNERS AS tw "
				+ "	ON tw.event_id = te1.event_id WHERE TE1.PROC_NAME = TE.PROC_NAME "
				+ "			and		current_timestamp between  comp_deadline and ADD_SECONDS(comp_deadline, - (18000)) "
				+ "			AND te1.STATUS <> 'COMPLETED' " + "	)) AS CRITICAL, " + "	( " + "	select count(*) from ( "
				+ "SELECT DISTINCT pe.REQUEST_ID AS REQUEST_ID,te1.event_id from " + "		TASK_EVENTS te1 "
				+ "		LEFT OUTER JOIN PROCESS_CONFIG_TB AS Pct " + "	ON TE1.PROC_NAME = PCT.PROCESS_NAME "
				+ "	LEFT OUTER JOIN TASK_SLA AS ts " + "	ON te1.NAME = ts.TASK_DEF "
				+ "	LEFT OUTER JOIN PROCESS_EVENTS AS pe " + "	ON pe.process_id = te1.process_id "
				+ "	INNER JOIN TASK_OWNERS AS tw "
				+ "	ON tw.event_id = te1.event_id WHERE TE1.PROC_NAME = TE.PROC_NAME "
				+ "			AND CURRENT_TIMESTAMP > comp_deadline " + "			AND te1.STATUS <> 'COMPLETED' "
				+ "	)) AS SLA_BREACHED " + "FROM TASK_EVENTS AS TE " + "	INNER JOIN PROCESS_CONFIG_TB AS PC "
				+ "	ON PC.PROCESS_NAME = TE.PROC_NAME " + "GROUP BY TE.PROC_NAME, " + "	PC.PROCESS_DISPLAY_NAME";

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = this.getSession().createSQLQuery(queryString).list();
		for (Object[] obj : resultList) {
			dto = new TotalActiveTaskDto();
			dto.setProcessName((String) obj[0]);
			dto.setProcessDisplayName((String) obj[1]);
			dto.setInTime((BigInteger) obj[2]);
			dto.setCritical((BigInteger) obj[3]);
			dto.setSlaBreached((BigInteger) obj[4]);
			list.add(dto);
		}
		return list;
	}

	private List<UserWorkCountDto> getUserWorkCountList(String processName) {
		List<UserWorkCountDto> list = new ArrayList<>();
		UserWorkCountDto dto = null;

		if (PMCConstant.SEARCH_ALL.equals(processName))
			processName = "";
		else
			processName = " AND te.PROC_NAME in( '" + processName + "') ";

		String queryString = "" + "SELECT DISTINCT TW1.task_owner, " + "	TW1.task_owner_disp, " + "	( "
				+ "		SELECT count(*) " + "		FROM ( " + "						SELECT DISTINCT request_id, "
				+ "					te.event_id " + "				FROM TASK_EVENTS AS te "
				+ "					LEFT OUTER JOIN PROCESS_CONFIG_TB AS Pct "
				+ "					ON TE.PROC_NAME = PCT.PROCESS_NAME "
				+ "					LEFT OUTER JOIN TASK_SLA AS ts " + "					ON te.NAME = ts.TASK_DEF "
				+ "					LEFT OUTER JOIN PROCESS_EVENTS AS pe "
				+ "					ON pe.process_id = te.process_id "
				+ "					INNER JOIN TASK_OWNERS AS tw " + "					ON tw.event_id = te.event_id "
				+ "				WHERE TE.status ='RESERVED' " + processName
				+ "					AND te.cur_proc = TW1.task_owner "
				+ "					AND CURRENT_TIMESTAMP < Add_seconds( "
				+ "							TE.comp_deadline, " + "							- (18000) "
				+ "						) " + "			) " + "	) AS in_time, " + "	( " + "		SELECT count(*) "
				+ "		FROM ( " + "						SELECT DISTINCT request_id, "
				+ "					te.event_id " + "				FROM TASK_EVENTS AS te "
				+ "					LEFT OUTER JOIN PROCESS_CONFIG_TB AS Pct "
				+ "					ON TE.PROC_NAME = PCT.PROCESS_NAME "
				+ "					LEFT OUTER JOIN TASK_SLA AS ts " + "					ON te.NAME = ts.TASK_DEF "
				+ "					LEFT OUTER JOIN PROCESS_EVENTS AS pe "
				+ "					ON pe.process_id = te.process_id "
				+ "					INNER JOIN TASK_OWNERS AS tw " + "					ON tw.event_id = te.event_id "
				+ "				WHERE TE.status  ='RESERVED' " + processName
				+ "						AND te.cur_proc = TW1.task_owner "
				+ "					AND CURRENT_TIMESTAMP > Add_seconds( " + "							comp_deadline, "
				+ "							- (18000) " + "						) "
				+ "					AND CURRENT_TIMESTAMP < comp_deadline " + "			) " + "	) AS CRITICAL, "
				+ "	( " + "		SELECT count(*) " + "		FROM ( "
				+ "						SELECT DISTINCT request_id, " + "					te.event_id "
				+ "				FROM TASK_EVENTS AS te "
				+ "					LEFT OUTER JOIN PROCESS_CONFIG_TB AS Pct "
				+ "					ON TE.PROC_NAME = PCT.PROCESS_NAME "
				+ "					LEFT OUTER JOIN TASK_SLA AS ts " + "					ON te.NAME = ts.TASK_DEF "
				+ "					LEFT OUTER JOIN PROCESS_EVENTS AS pe "
				+ "					ON pe.process_id = te.process_id "
				+ "					INNER JOIN TASK_OWNERS AS tw " + "					ON tw.event_id = te.event_id "
				+ "				WHERE TE.status  ='RESERVED' " + processName
				+ "					AND te.cur_proc = TW1.task_owner "
				+ "					AND CURRENT_TIMESTAMP > comp_deadline " + "			) " + "	) AS BREACHED "
				+ "FROM TASK_EVENTS AS te1 " + "	LEFT OUTER JOIN PROCESS_CONFIG_TB AS PC1 "
				+ "	ON TE1.PROC_NAME = PC1.PROCESS_NAME " + "	LEFT OUTER JOIN TASK_SLA AS ts "
				+ "	ON te1.NAME = ts.TASK_DEF " + "	LEFT OUTER JOIN PROCESS_EVENTS AS pe "
				+ "	ON pe.process_id = te1.process_id " + "	INNER JOIN TASK_OWNERS AS TW1 "
				+ "	ON TW1.event_id = te1.event_id " + "WHERE TW1.task_owner_disp IS NOT NULL;";

		System.err.println("user" + queryString);

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = this.getSession().createSQLQuery(queryString).list();
		for (Object[] obj : resultList) {
			dto = new UserWorkCountDto();
			dto.setUserName((String) obj[1]);
			dto.setUserId((String) obj[0]);
			dto.setInTime((BigInteger) obj[2]);
			dto.setCritical((BigInteger) obj[3]);
			dto.setSlaBreached((BigInteger) obj[4]);
			list.add(dto);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private List<TaskNameCountDto> getTaskCompletionRange(String duration) {
		List<TaskNameCountDto> list = null;
		try {
			if (!ServicesUtil.isEmpty(duration)) {

				list = new ArrayList<TaskNameCountDto>();
				List<String> listOfString = null;
				String queryString = "select ";
				String subQuery = "";
				if (PMCConstant.GRAPH_TREND_WEEK.equals(duration)) {

					listOfString = ServicesUtil.getPreviousDates();
					for (int i = 0; i < listOfString.size() - 1; i++) {
						subQuery += "(select count(*) from (SELECT distinct request_id,te.event_id FROM TASK_EVENTS AS te LEFT OUTER JOIN PROCESS_CONFIG_TB AS Pct ON TE.PROC_NAME = PCT.PROCESS_NAME LEFT OUTER JOIN TASK_SLA AS ts ON te.NAME = ts.TASK_DEF LEFT OUTER JOIN PROCESS_EVENTS AS pe ON pe.process_id = te.process_id	LEFT JOIN TASK_OWNERS AS tw ON tw.event_id = te.event_id where te.completed_at between '"
								+ listOfString.get(i) + "' and '" + listOfString.get(i + 1) + "')) as d" + (i + 1)
								+ ",";
					}
				} else if (PMCConstant.GRAPH_TREND_MONTH.equals(duration)) {

					listOfString = ServicesUtil.getPreviousMonths();
					for (int i = 0; i < 3; i++) {
						subQuery += "(select count(*) from (SELECT distinct request_id,te.event_id FROM TASK_EVENTS AS te LEFT OUTER JOIN PROCESS_CONFIG_TB AS Pct ON TE.PROC_NAME = PCT.PROCESS_NAME LEFT OUTER JOIN TASK_SLA AS ts ON te.NAME = ts.TASK_DEF LEFT OUTER JOIN PROCESS_EVENTS AS pe ON pe.process_id = te.process_id LEFT JOIN TASK_OWNERS AS tw ON tw.event_id = te.event_id where te.completed_at between '"
								+ ServicesUtil.monthStartDate(listOfString.get(i)) + "' and '"
								+ ServicesUtil.monthEndDate(listOfString.get(i)) + "')) as m" + (i + 1) + ",";
					}
					listOfString = ServicesUtil.getPreviousMonthsName();
				}

				queryString += subQuery.substring(0, subQuery.length() - 1) + " from dummy";
				System.err.println("month" + queryString);

				List<Object[]> resultList = this.getSession().createSQLQuery(queryString).list();

				list = getTaskDto(listOfString, resultList.get(0), (PMCConstant.GRAPH_TREND_WEEK.equals(duration))
						? listOfString.size() - 1 : listOfString.size());

			}
		} catch (Exception e) {
			System.err.println("error:" + e.getLocalizedMessage() + "," + e.getMessage());
		}
		return list;

	}

}
