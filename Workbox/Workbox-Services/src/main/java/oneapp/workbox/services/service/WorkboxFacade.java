package oneapp.workbox.services.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oneapp.workbox.services.dao.CustomAttributeDao;
import oneapp.workbox.services.dao.ProcessEventsDao;
import oneapp.workbox.services.dao.WorkboxDaoMuchCleanUp;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.SlaProcessNameListDto;
import oneapp.workbox.services.dto.StringListResponseDto;
import oneapp.workbox.services.dto.TasksCountDTO;
import oneapp.workbox.services.dto.TrackingResponse;
import oneapp.workbox.services.dto.WorkboxRequestDto;
import oneapp.workbox.services.dto.WorkboxResponseDto;
import oneapp.workbox.services.entity.ProcessConfigDo;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;
import oneapp.workbox.services.util.UserManagementUtil;

/**
 * Session Bean implementation class WorkboxFacade
 */
@Service("WorkboxFacade")
public class WorkboxFacade implements WorkboxFacadeLocal {

	@Autowired
	private WorkboxDaoMuchCleanUp workboxdao;

	@Autowired
	private SessionFactory sf;

	@Autowired
	private ProcessEventsDao processDao;

	@Autowired
	CustomAttributeDao customAttributeDao;

	private Session getSession() {
		try {
			return sf.getCurrentSession();
		} catch (HibernateException ex) {
			return sf.openSession();
		}
	}

	public WorkboxFacade() {
	}

	@Override
	public String sayHello() {
		return "Hello From Spring";
	}

	@Override
	public WorkboxResponseDto getWorkboxFilterData(WorkboxRequestDto taskRequest) {
		WorkboxResponseDto responseDto = null;
		if (!ServicesUtil.isEmpty(taskRequest)) {
			if (ServicesUtil.isEmpty(taskRequest.isAdmin())) {
				taskRequest.setIsAdmin(false);
			}
			if (!ServicesUtil.isEmpty(taskRequest.getCurrentUserInfo())) {
				if (!ServicesUtil.isEmpty(taskRequest.getCurrentUserInfo().getTaskOwner())) {
					// persistODataService.persistToDb(taskRequest.getCurrentUserInfo());
					responseDto = workboxdao.getFilterData(taskRequest.getRequestId(), taskRequest.getProcessName(),
							taskRequest.getCreatedBy(), taskRequest.getCreatedAt(), taskRequest.getStatus(),
							taskRequest.getOrderBy(), taskRequest.getOrderType(), taskRequest.getSkipCount(),
							taskRequest.getMaxCount(), taskRequest.getPage(), false,
							taskRequest.getCustomAttributeTemplates(), taskRequest.getCurrentUserInfo().getTaskOwner(),
							taskRequest.getIsAdmin(), taskRequest.getInboxType(), taskRequest.getReportFilter(),
							taskRequest.getIsCritical(), taskRequest.getCompletedDate(), taskRequest.getGraphName());
				}
			}
		}
		return responseDto;
	}

	@Override
	public WorkboxResponseDto getWorkboxFilterData(WorkboxRequestDto taskRequest, Boolean isChatBot) {
		WorkboxResponseDto responseDto = null;
		if (!ServicesUtil.isEmpty(taskRequest)) {
			responseDto = workboxdao.getFilterData(taskRequest.getRequestId(), taskRequest.getProcessName(),
					taskRequest.getCreatedBy(), taskRequest.getCreatedAt(), taskRequest.getStatus(),
					taskRequest.getOrderBy(), taskRequest.getOrderType(), taskRequest.getSkipCount(),
					taskRequest.getMaxCount(), taskRequest.getPage(), true, taskRequest.getCustomAttributeTemplates(),
					"CHAT_BOT", taskRequest.getIsAdmin(), taskRequest.getInboxType(), taskRequest.getReportFilter(),
					taskRequest.getIsCritical(), taskRequest.getCompletedDate(), taskRequest.getGraphName());
		}
		return responseDto;
	}

	@Override
	public WorkboxResponseDto getWorkboxCompletedFilterData(String processName, String requestId, String createdBy,
			String createdAt, String completedAt, String period, Integer skipCount, Integer maxCount, Integer page) {
//		String userId = null;
//		userId = UserManagementUtil.getLoggedInUser().getName();
//		return workboxdao.getWorkboxCompletedFilterData(userId, processName, requestId, createdBy, createdAt,
//				completedAt, period, skipCount, maxCount, page);
		return null;
	}

	@Override
	public TrackingResponse getTrackingResults() {
		TrackingResponse response = new TrackingResponse();
		ResponseMessage respMessage = new ResponseMessage();
		String userId = null;
		userId = UserManagementUtil.getLoggedInUser().getName();
		DateFormat newDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (!ServicesUtil.isEmpty(userId)) {
			BigDecimal compOnSlaCount = null;
			BigDecimal compOffSlaCount = null;
			BigDecimal inProgOnSlaCount = null;
			BigDecimal inProgOffSlaCount = null;
			BigDecimal inProgApproachingCount = null;

			List<TasksCountDTO> responseList = new ArrayList<TasksCountDTO>();

			String completedCountQry1 = "SELECT COUNT(EVENT_ID) AS COUNT FROM TASK_EVENTS WHERE STATUS = 'COMPLETED' AND COMP_DEADLINE <= COMPLETED_AT and CUR_PROC = '"
					+ userId + "'";
			String completedCountQry2 = "SELECT COUNT(EVENT_ID) AS COUNT FROM TASK_EVENTS WHERE STATUS = 'COMPLETED' AND COMP_DEADLINE > COMPLETED_AT and CUR_PROC = '"
					+ userId + "'";

			Query comCountQry1 = this.getSession().createSQLQuery(completedCountQry1);
			compOffSlaCount = ServicesUtil.getBigDecimal(comCountQry1.uniqueResult());

			Query comCountQry2 = this.getSession().createSQLQuery(completedCountQry2);
			compOnSlaCount = ServicesUtil.getBigDecimal(comCountQry2.uniqueResult());

			System.err.println("Completed off Sla Count : " + compOffSlaCount);
			System.err.println("Completed on Sla Count : " + compOnSlaCount);

			Date currDate = new Date();

			String inProgressCountQry1 = "SELECT COUNT(DISTINCT(TE.EVENT_ID)) AS COUNT " + "FROM TASK_EVENTS TE "
					+ "JOIN TASK_OWNERS TW " + "ON TE.EVENT_ID    = TW.EVENT_ID " + "AND ((TE.STATUS   = 'RESERVED' "
					+ "AND TE.CUR_PROC   = '" + userId + "'  AND TW.IS_PROCESSED = '1') "
					+ "OR (TE.STATUS     = 'READY' " + "AND TW.TASK_OWNER ='" + userId + "')) " +
					"AND ('" + newDf.format(currDate) + "' > to_seconddate(TE.COMP_DEADLINE))";

			Query resInProgressCountQry1 = this.getSession().createSQLQuery(inProgressCountQry1);
			inProgOffSlaCount = ServicesUtil.getBigDecimal(resInProgressCountQry1.uniqueResult());
			System.err.println("[pmc][tracking] : " + inProgOnSlaCount);

			String inProgressCountQry2 = "SELECT COUNT(DISTINCT(TE.EVENT_ID)) AS COUNT " + "FROM TASK_EVENTS TE "
					+ "JOIN TASK_OWNERS TW " + "ON TE.EVENT_ID    = TW.EVENT_ID " + "AND ((TE.STATUS   = 'RESERVED' "
					+ "AND TE.CUR_PROC   = '" + userId + "' AND TW.IS_PROCESSED = '1') "
					+ "OR (TE.STATUS     = 'READY' " + "AND TW.TASK_OWNER ='" + userId + "')) " + "AND ('"
					+ newDf.format(currDate) + "'  < add_days(to_seconddate(TE.COMP_DEADLINE), -1))";

			Query resInProgressCountQry2 = this.getSession().createSQLQuery(inProgressCountQry2);
			inProgOnSlaCount = ServicesUtil.getBigDecimal(resInProgressCountQry2.uniqueResult());
			System.err.println("[pmc][tracking] : " + inProgOnSlaCount);

			String inProgressApproching = "SELECT COUNT(DISTINCT(TE.EVENT_ID)) AS COUNT " + "FROM TASK_EVENTS TE "
					+ "JOIN TASK_OWNERS TW " + "ON TE.EVENT_ID    = TW.EVENT_ID " + "AND ((TE.STATUS   = 'RESERVED' "
					+ "AND TE.CUR_PROC   = '" + userId + "' AND TW.IS_PROCESSED = '1') "
					+ "OR (TE.STATUS     = 'READY' " + "AND TW.TASK_OWNER ='" + userId + "')) " + "AND ('"
					+ newDf.format(currDate) + "' > add_days(to_seconddate(TE.COMP_DEADLINE), - 1)) AND ('"
					+ newDf.format(currDate) + "' <= to_seconddate(TE.COMP_DEADLINE))";

			Query resInProgressApproaching = this.getSession().createSQLQuery(inProgressApproching);
			inProgApproachingCount = ServicesUtil.getBigDecimal(resInProgressApproaching.uniqueResult());
			System.err.println("[pmc][tracking] : " + inProgApproachingCount);

			if ((!ServicesUtil.isEmpty(compOnSlaCount) && (!ServicesUtil.isEmpty(compOffSlaCount)
					&& (!ServicesUtil.isEmpty(inProgOnSlaCount) && (!ServicesUtil.isEmpty(inProgOffSlaCount)))))) {

				TasksCountDTO resp = new TasksCountDTO();
				resp.setStatus("Completed - Past Due");
				resp.setCount(compOffSlaCount);
				responseList.add(resp);
				TasksCountDTO resp1 = new TasksCountDTO();
				resp1.setStatus("Completed - On Track");
				resp1.setCount(compOnSlaCount);
				responseList.add(resp1);
				TasksCountDTO resp2 = new TasksCountDTO();
				resp2.setStatus("In Progress - Past Due");
				resp2.setCount(inProgOffSlaCount);
				responseList.add(resp2);
				TasksCountDTO resp3 = new TasksCountDTO();
				resp3.setStatus("In Progress - On Track");
				resp3.setCount(inProgOnSlaCount);
				responseList.add(resp3);
				TasksCountDTO resp4 = new TasksCountDTO();
				resp4.setStatus("In Progress - Approaching");
				resp4.setCount(inProgApproachingCount);
				responseList.add(resp4);

				response.setTasksCount(responseList);
				respMessage.setMessage("Tasks Count Sent Successfully");
				respMessage.setStatus("Success");
				respMessage.setStatusCode("0");
				response.setResponseMessage(respMessage);
				return response;
			} else {
				respMessage.setMessage(PMCConstant.NO_RESULT);
				respMessage.setStatus("Failure");
				respMessage.setStatusCode("1");
				response.setResponseMessage(respMessage);
			}
			respMessage.setMessage("No Logged In User Found");
			respMessage.setStatus("Failure");
			respMessage.setStatusCode("1");
			response.setResponseMessage(respMessage);
		}
		return response;

	}

	public StringListResponseDto getRequestId() {
		StringListResponseDto dto = new StringListResponseDto();
		ResponseMessage responseDto = new ResponseMessage();
		responseDto.setStatus(PMCConstant.STATUS_FAILURE);
		responseDto.setStatusCode(PMCConstant.CODE_FAILURE);
		List<String> requestIds = processDao.getRequestId();
		if (!ServicesUtil.isEmpty(requestIds)) {
			dto.setStr(requestIds);
			responseDto.setMessage("RequestId " + PMCConstant.READ_SUCCESS);
			responseDto.setStatus(PMCConstant.STATUS_SUCCESS);
			responseDto.setStatusCode(PMCConstant.CODE_SUCCESS);
		} else
			responseDto.setMessage("RequestId " + PMCConstant.READ_FAILURE);
		dto.setMessage(responseDto);
		return dto;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<SlaProcessNameListDto> getProcessNames() {
		Criteria criteria = this.getSession().createCriteria(ProcessConfigDo.class);
		ProjectionList p1 = Projections.projectionList();
		p1.add(Projections.property("processName"));
		p1.add(Projections.property("processDisplayName"));
		criteria.setProjection(Projections.distinct(p1));
		List l = criteria.list();
		Iterator it = l.iterator();
		List<SlaProcessNameListDto> response = new ArrayList<>();

		while (it.hasNext()) {
			Object ob[] = (Object[]) it.next();
			SlaProcessNameListDto dto = new SlaProcessNameListDto();
			dto.setProcessName(ob[0].toString());
			dto.setProcessDisplayName(ob[1].toString());
			response.add(dto);
		}
		return response;

	}

	public Statistics getStatistics() {
		return sf.getStatistics();
	}

}
