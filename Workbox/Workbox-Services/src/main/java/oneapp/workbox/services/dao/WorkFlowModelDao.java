package oneapp.workbox.services.dao;

import static oneapp.workbox.services.adapters.WorkFlowModelParser.WFS_END_EVENT_CLASS;
import static oneapp.workbox.services.adapters.WorkFlowModelParser.WFS_SCRIPT_TASK_CLASS;
import static oneapp.workbox.services.adapters.WorkFlowModelParser.WFS_SERVICE_TASK_CLASS;
import static oneapp.workbox.services.adapters.WorkFlowModelParser.WFS_START_EVENT_CLASS;
import static oneapp.workbox.services.adapters.WorkFlowModelParser.WFS_USER_TASK_CLASS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import oneapp.workbox.services.adapters.WorkFlowMasterModelGroup;
import oneapp.workbox.services.adapters.WorkFlowModelMaster;
import oneapp.workbox.services.adapters.WorkFlowModelParser;
import oneapp.workbox.services.dto.ExecutionLog;
import oneapp.workbox.services.dto.ProjectDetail;
import oneapp.workbox.services.dto.RestResponse;
import oneapp.workbox.services.dto.WorkFlowArtifactSequence;
import oneapp.workbox.services.dto.WorkFlowDetailAttribute;
import oneapp.workbox.services.entity.ProjectProcessRanking;
import oneapp.workbox.services.entity.WorkFlowActivity;
import oneapp.workbox.services.entity.WorkFlowEvent;
import oneapp.workbox.services.entity.WorkFlowExclusiveGateway;
import oneapp.workbox.services.entity.WorkFlowSequenceFlow;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.RestUtil;
import oneapp.workbox.services.util.ServicesUtil;
import oneapp.workbox.services.util.WorkFlowTaskStatus;

@Repository
@Transactional
public class WorkFlowModelDao {

	private static final int _HIBERNATE_BATCH_SIZE = 300;

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private ProjectProcessDao projectProcessDao;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	public void getProjectModel(String projectId) {
		String getProcessDetailsQuery = ""
				+ " SELECT PM.PROCESS_ID, PE.STARTED_AT, PE.NAME "
				+ " FROM \"NPI_WORKBOX_USER\".\"PROJECT_PROCESS_MAP\" PM JOIN PROCESS_EVENTS PE ON PM.PROCESS_ID = PE.PROCESS_ID "
				+ " WHERE PROJECT_ID = '"+projectId+"' "
				+ " ORDER BY PE.STARTED_AT ASC ";
		List<Object[]> resultList = getSession().createSQLQuery(getProcessDetailsQuery).list();
		if(!ServicesUtil.isEmpty(resultList) && resultList.size() > 0) {
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public WorkFlowModelMaster getWorkFlowModel(String workFlowDefinitionId, String processId) {
		WorkFlowModelMaster workflowModel = null;
		Criteria criteria = null;
		if (!ServicesUtil.isEmpty(workFlowDefinitionId)) {
			workflowModel = new WorkFlowModelMaster();
			criteria = getSession().createCriteria(WorkFlowEvent.class);
			criteria.add(Restrictions.eq("workFlowDefId", workFlowDefinitionId));
			workflowModel.setEvents(criteria.list());
			criteria = getSession().createCriteria(WorkFlowActivity.class);
			criteria.add(Restrictions.eq("workFlowDefId", workFlowDefinitionId));
			workflowModel.setActivities(criteria.list());
			criteria = getSession().createCriteria(WorkFlowExclusiveGateway.class);
			criteria.add(Restrictions.eq("workFlowDefId", workFlowDefinitionId));
			workflowModel.setExclusiveGateways(criteria.list());
			criteria = getSession().createCriteria(WorkFlowSequenceFlow.class);
			criteria.add(Restrictions.eq("workFlowDefId", workFlowDefinitionId));
			workflowModel.setSequences(criteria.list());
		}

		// Forming Network Sequence
		List<WorkFlowArtifactSequence> networkSequence = null;
		WorkFlowArtifactSequence artifactSeq = null;
		MutableGraph<Object> graph = GraphBuilder.directed().build();
		Set<String> activityKeys = null;
		Set<String> allKeys = null;
		criteria = getSession().createCriteria(WorkFlowActivity.class);
		criteria.setProjection(Projections.property("id"));
		criteria.add(Restrictions.eq("workFlowDefId", workFlowDefinitionId));
		activityKeys = new HashSet<>(criteria.list());

		criteria = getSession().createCriteria(WorkFlowSequenceFlow.class);
		criteria.setProjection(Projections.property("sourceRef"));
		criteria.add(Restrictions.eq("workFlowDefId", workFlowDefinitionId));

		allKeys = new HashSet<>(criteria.list());

		criteria = getSession().createCriteria(WorkFlowSequenceFlow.class);
		criteria.setProjection(Projections.property("targetRef"));
		criteria.add(Restrictions.eq("workFlowDefId", workFlowDefinitionId));

		if (!ServicesUtil.isEmpty(allKeys) && allKeys.size() > 0) {
			allKeys.addAll(criteria.list());
		}

		for (WorkFlowSequenceFlow sequence : workflowModel.getSequences()) {
			graph.putEdge(sequence.getSourceRef(), sequence.getTargetRef());
		}

		allKeys.removeAll(activityKeys);

		for (String removalKey : allKeys) {
			removeNode(removalKey, graph);
		}

		Set<EndpointPair<Object>> edges = graph.edges();
		if (!ServicesUtil.isEmpty(edges) && edges.size() > 0) {
			networkSequence = new ArrayList<WorkFlowArtifactSequence>();
			for (EndpointPair<Object> edge : graph.edges()) {
				artifactSeq = new WorkFlowArtifactSequence(edge.source().toString(), edge.target().toString(), null);
				if(!ServicesUtil.isEmpty(processId)) {
					artifactSeq.setFrom(processId + "||" + artifactSeq.getFrom());
					artifactSeq.setTo(processId + "||" + artifactSeq.getTo());
				}
				networkSequence.add(artifactSeq);
			}
		}
		workflowModel.setNetworkSequence(networkSequence);

		// Forming Status of WF
		if (!ServicesUtil.isEmpty(processId)) {
			MultiValueMap<String, ExecutionLog> executionLogs = getExecutionLogs(processId);
			for (WorkFlowActivity activity : workflowModel.getActivities()) {

				activity.setId(processId + "||" + activity.getId());
				activity.setWorkFlowDefId(processId + "||" + activity.getWorkFlowDefId());

				List<ExecutionLog> activityStatus = executionLogs.get(activity.getArtifactId());
				if (!ServicesUtil.isEmpty(activityStatus) && activityStatus.size() > 0) {
					setActivityStatus(activity, activityStatus);
				}
				if (!ServicesUtil.isEmpty(activity) && !ServicesUtil.isEmpty(activity.getArtifactClassDefinition())
						&& (activity.getArtifactClassDefinition().equals(WFS_START_EVENT_CLASS)
								|| activity.getArtifactClassDefinition().equals(WFS_END_EVENT_CLASS))) {
					List<ExecutionLog> eventStatus = executionLogs.get("workFlowEvent");
					if (!ServicesUtil.isEmpty(eventStatus) && eventStatus.size() > 0) {
						setActivityStatus(activity, eventStatus);
					}
				}
			}
		}

		return workflowModel;
	}

	private void setActivityStatus(WorkFlowActivity activity, List<ExecutionLog> activityStatus) {
		if (!ServicesUtil.isEmpty(activityStatus) && activityStatus.size() > 0) {
			Collections.sort(activityStatus, new ExecutionLog());
			ExecutionLog executionLog = activityStatus.get(0);
			if (!ServicesUtil.isEmpty(activity) && !ServicesUtil.isEmpty(activity.getArtifactClassDefinition())) {
				switch (activity.getArtifactClassDefinition()) {
				case WFS_USER_TASK_CLASS:
					if (executionLog.getType().endsWith("COMPLETED")) {
						activity.setActivityStatus(WorkFlowTaskStatus.Success);
					} else if (executionLog.getType().endsWith("CREATED") || executionLog.getType().endsWith("CLAIMED")
							|| executionLog.getType().endsWith("RELEASED")) {
						activity.setActivityStatus(WorkFlowTaskStatus.Warning);
					}
				case WFS_SCRIPT_TASK_CLASS:
				case WFS_SERVICE_TASK_CLASS:
					if (executionLog.getType().endsWith("COMPLETED")) {
						activity.setActivityStatus(WorkFlowTaskStatus.Success);
					} else if (executionLog.getType().endsWith("CREATED")) {
						activity.setActivityStatus(WorkFlowTaskStatus.Warning);
					} else if (executionLog.getType().endsWith("FAILURE")) {
						activity.setActivityStatus(WorkFlowTaskStatus.Error);
					}
				case WFS_START_EVENT_CLASS:
					for (ExecutionLog log : activityStatus) {
						if (log.getType().equals("WORKFLOW_STARTED")) {
							activity.setActivityStatus(WorkFlowTaskStatus.Success);
						}
					}
				case WFS_END_EVENT_CLASS:
					for (ExecutionLog log : activityStatus) {
						if (log.getType().equals("WORKFLOW_COMPLETED")) {
							activity.setActivityStatus(WorkFlowTaskStatus.Success);
						}
					}
				}
			}
		}
	}

	private MultiValueMap<String, ExecutionLog> getExecutionLogs(String processId) {
		String url = "https://bpmworkflowruntimea2d6007ea-x5qv5zg6ns.hana.ondemand.com/workflow-service/rest/v1/workflow-instances/"
				+ processId + "/execution-logs";
		JSONObject executionLog = null;
		MultiValueMap<String, ExecutionLog> exLogs = null;
		ExecutionLog exLog = null;
		RestResponse restResponse = RestUtil.callRestService(url, null, null, PMCConstant.HTTP_METHOD_GET,
				PMCConstant.APPLICATION_JSON, false, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null,
				null, null);
		if (!ServicesUtil.isEmpty(restResponse) && restResponse.getResponseCode() >= 200
				&& restResponse.getResponseCode() <= 400) {
			exLogs = new LinkedMultiValueMap<String, ExecutionLog>();
			JSONArray executionLogs = (JSONArray) restResponse.getResponseObject();
			for (Object object : executionLogs) {
				exLog = new ExecutionLog();
				executionLog = (JSONObject) object;
				if (!ServicesUtil.isEmpty(executionLog.optString("activityId")))
					exLog.setActivityId(executionLog.optString("activityId"));
				else if (ServicesUtil.isEmpty(executionLog.optString("activityId")))
					exLog.setActivityId("workFlowEvent");
				exLog.setId(executionLog.optString("id"));
				exLog.setTimeStamp(ServicesUtil.resultTAsDate(executionLog.optString("timestamp")));
				exLog.setType(executionLog.optString("type"));

				exLogs.add(exLog.getActivityId(), exLog);
			}
		}
		return exLogs;
	}

	public static void removeNode(String node, MutableGraph<Object> graph) {
		Set<Object> predecessors = graph.predecessors(node);
		Set<Object> successors = graph.successors(node);

		for (Object predecessor : predecessors) {
			for (Object successor : successors) {
				graph.putEdge(predecessor, successor);
			}
		}

		graph.removeNode(node);
	}

	public String saveWorkFlowModel(String workFlowDefinitionId) {
		WorkFlowModelMaster workflowModel = WorkFlowModelParser.parseWorkflowModel(workFlowDefinitionId);
		String response;
		try {
			if (!ServicesUtil.isEmpty(workflowModel)) {
				saveModelEvents(workflowModel.getEvents());
				saveModelActivities(workflowModel.getActivities());
				saveModelExclusiveGateways(workflowModel.getExclusiveGateways());
				saveModelSequences(workflowModel.getSequences());
			}
			response = "Success";
		} catch (Exception ex) {
			System.err.println("Exception saving Workflow Model : " + ex.getMessage());
			getSession().getTransaction().rollback();
			response = "Failure";
		}
		return response;
	}

	private void saveModelEvents(List<WorkFlowEvent> events) {
		Session session = null;
		if (!ServicesUtil.isEmpty(events) && events.size() > 0) {
			session = this.getSession();
			for (int i = 0; i < events.size(); i++) {
				session.saveOrUpdate(events.get(i));
				if (i % _HIBERNATE_BATCH_SIZE == 0 && i > 0) {
					session.flush();
					session.clear();
				}
			}
			if (!session.getTransaction().wasCommitted()) {
				session.flush();
			}
		}
	}

	private void saveModelActivities(List<WorkFlowActivity> activities) {
		Session session = null;
		if (!ServicesUtil.isEmpty(activities) && activities.size() > 0) {
			session = this.getSession();
			for (int i = 0; i < activities.size(); i++) {
				session.saveOrUpdate(activities.get(i));
				if (i % _HIBERNATE_BATCH_SIZE == 0 && i > 0) {
					session.flush();
					session.clear();
				}
			}
			if (!session.getTransaction().wasCommitted()) {
				session.flush();
			}
		}
	}

	private void saveModelExclusiveGateways(List<WorkFlowExclusiveGateway> exclusiveGateways) {
		Session session = null;
		if (!ServicesUtil.isEmpty(exclusiveGateways) && exclusiveGateways.size() > 0) {
			session = this.getSession();
			for (int i = 0; i < exclusiveGateways.size(); i++) {
				session.saveOrUpdate(exclusiveGateways.get(i));
				if (i % _HIBERNATE_BATCH_SIZE == 0 && i > 0) {
					session.flush();
					session.clear();
				}
			}
			if (!session.getTransaction().wasCommitted()) {
				session.flush();
			}
		}
	}

	private void saveModelSequences(List<WorkFlowSequenceFlow> sequenceFlows) {
		Session session = null;
		if (!ServicesUtil.isEmpty(sequenceFlows) && sequenceFlows.size() > 0) {
			session = this.getSession();
			for (int i = 0; i < sequenceFlows.size(); i++) {
				session.saveOrUpdate(sequenceFlows.get(i));
				if (i % _HIBERNATE_BATCH_SIZE == 0 && i > 0) {
					session.flush();
					session.clear();
				}
			}
			if (!session.getTransaction().wasCommitted()) {
				session.flush();
			}
		}
	}
	
	class ProjectProcessDetail {

		public ProjectProcessDetail() {
			super();
		}

		public ProjectProcessDetail(String processId, String processName, Date startedAt) {
			super();
			this.processId = processId;
			this.processName = processName;
			this.startedAt = startedAt;
		}

		private String processId;
		private String processName;
		private Date startedAt;

		public String getProcessId() {
			return processId;
		}

		public void setProcessId(String processId) {
			this.processId = processId;
		}

		public String getProcessName() {
			return processName;
		}

		public void setProcessName(String processName) {
			this.processName = processName;
		}

		public Date getStartedAt() {
			return startedAt;
		}

		public void setStartedAt(Date startedAt) {
			this.startedAt = startedAt;
		}

		@Override
		public String toString() {
			return "ProjectProcessDetail [processId=" + processId + ", processName=" + processName + ", startedAt="
					+ startedAt + "]";
		}

	}

	public WorkFlowModelMaster getMasterWorkFlowModel(String projectId) {
		
		List<WorkFlowActivity> activities = null;
		List<WorkFlowArtifactSequence> networkSequence = null;
		MultiValueMap<String, ProjectProcessDetail> processDetails = getProcessForProject(projectId);
		MultiValueMap<Integer, String> processRanking = getProjectRanking();
		WorkFlowModelMaster modelDetail = null;
		WorkFlowModelMaster masterModelDetail = null;
		List<String> processes = null;
		List<WorkFlowMasterModelGroup> groups = null;
		List<ProjectProcessDetail> prcDet = null;
		List<WorkFlowArtifactSequence> groupSequence = null;
		WorkFlowMasterModelGroup wfModelGroup = null;
		MultiValueMap<String,WorkFlowMasterModelGroup> mvGroupMap = null;
		if(!ServicesUtil.isEmpty(processRanking) && !ServicesUtil.isEmpty(processRanking.keySet())) {
			groups = new ArrayList<WorkFlowMasterModelGroup>();
			for(Integer key : processRanking.keySet()) {
				processes = processRanking.get(key);
				for(String process : processes) {
					prcDet = processDetails.get(process);
					if(!ServicesUtil.isEmpty(prcDet) && prcDet.size() > 0) {
						for(ProjectProcessDetail detail : prcDet) {
							modelDetail = getWorkFlowModel(process, detail.getProcessId());
							if(ServicesUtil.isEmpty(masterModelDetail)) {
								masterModelDetail = modelDetail;
							} else {
								mergeModelMasterDetail(masterModelDetail, modelDetail);
							}
							wfModelGroup = new WorkFlowMasterModelGroup(detail.getProcessId() + "||" + process, process, null);
							groups.add(wfModelGroup);
						}
					} else {
						modelDetail = getWorkFlowModel(process, null);
						mergeModelMasterDetail(masterModelDetail, modelDetail);
						groups.add(new WorkFlowMasterModelGroup(process, process, null));
					}
				}
			}
		}
		
		List<String> prevProcesses = null;
		List<String> nextProcesses = null;
		groupSequence = new ArrayList<WorkFlowArtifactSequence>();
		mvGroupMap = getMVGroupMap(groups);
		for(Integer key : processRanking.keySet()) {
			if(key != processRanking.size()) {
				prevProcesses = processRanking.get(key);
				nextProcesses = processRanking.get(key+1);
				
				groupSequence.addAll(formGroupSequence(prevProcesses, nextProcesses, mvGroupMap));
			}
		}
		
		masterModelDetail.setGroupSequence(groupSequence);
		masterModelDetail.setGroups(groups);
		
		if(!ServicesUtil.isEmpty(processRanking) && processRanking.size() >= 1 &&  !ServicesUtil.isEmpty(masterModelDetail)) {
			List<ProjectDetail> projectDetails = projectProcessDao.getProjectDetails(projectId, null, null, null, false);
			ProjectDetail projectDetail = null;
			if(!ServicesUtil.isEmpty(projectDetails) && projectDetails.size() == 1)
				projectDetail = projectDetails.get(0);
			List<WorkFlowDetailAttribute> attributes = new ArrayList<WorkFlowDetailAttribute>();
			{
				attributes.add(new WorkFlowDetailAttribute(projectDetail.getProjectDescription(), "Project Description"));
				attributes.add(new WorkFlowDetailAttribute(projectDetail.getCreatedAtInString(), "Project Created At"));
				attributes.add(new WorkFlowDetailAttribute(projectDetail.getReason(), "Reason"));
				attributes.add(new WorkFlowDetailAttribute(projectDetail.getRegionCode(), "Region Code"));
				attributes.add(new WorkFlowDetailAttribute(projectDetail.getUserCreated(), "Created By"));
			}
			WorkFlowActivity activity = new WorkFlowActivity();
			activity.setId(projectId);
			activity.setActivityShape("Box");
			activity.setAttributes(attributes);
			
			
			activities = masterModelDetail.getActivities();
			networkSequence = masterModelDetail.getNetworkSequence();
			List<String> initialProcess = processRanking.get(1);
			
			if(!ServicesUtil.isEmpty(activities))
				activities.add(activity);

			for(WorkFlowActivity inActivity : activities) {
				if(!ServicesUtil.isEmpty(inActivity) && WorkFlowModelParser.WFS_START_EVENT_CLASS.equals(inActivity.getArtifactClassDefinition()) && initialProcess.contains(getDefName(inActivity.getWorkFlowDefId()))) {
					networkSequence.add(new WorkFlowArtifactSequence(activity.getId(), inActivity.getId(), null));
				}
			}
		}
		
		if(!ServicesUtil.isEmpty(networkSequence))
			masterModelDetail.setNetworkSequence(networkSequence);
		if(!ServicesUtil.isEmpty(activities))
			masterModelDetail.setActivities(activities);
		
		return masterModelDetail;
	}
	
	private static String getDefName(String workFlowDefId) {
		if(workFlowDefId.contains("||")) {
			return workFlowDefId.split(Pattern.quote("||"))[1];
		}
		return workFlowDefId;
	}

	private MultiValueMap<String, WorkFlowMasterModelGroup> getMVGroupMap(List<WorkFlowMasterModelGroup> groups) {
		MultiValueMap<String, WorkFlowMasterModelGroup> mvGroupMap = null;
		if(!ServicesUtil.isEmpty(groups) && groups.size() > 0) {
			mvGroupMap = new LinkedMultiValueMap<String, WorkFlowMasterModelGroup>();
			for(WorkFlowMasterModelGroup group : groups) {
				mvGroupMap.add(group.getTitle(), group);
			}
		}
		return mvGroupMap;
	}

	private Set<WorkFlowArtifactSequence> formGroupSequence(List<String> prevProcesses, List<String> nextProcesses, 
			MultiValueMap<String, WorkFlowMasterModelGroup> groups) {
		Set<WorkFlowArtifactSequence> gSequence = null;
		if(!ServicesUtil.isEmpty(prevProcesses) && prevProcesses.size() > 0) {
			gSequence = new HashSet<WorkFlowArtifactSequence>();
			
			if(!ServicesUtil.isEmpty(prevProcesses)) { 
				for(String currentProcess : prevProcesses) {
					List<WorkFlowMasterModelGroup> curGroups = groups.get(currentProcess);
					if(!ServicesUtil.isEmpty(nextProcesses)) {
						for(String nextProcess : nextProcesses) {
							List<WorkFlowMasterModelGroup> nxtGroups = groups.get(nextProcess);
							for(WorkFlowMasterModelGroup curGroup : curGroups) {
								for(WorkFlowMasterModelGroup nxtGroup : nxtGroups) {
									gSequence.add(new WorkFlowArtifactSequence(curGroup.getKey(), nxtGroup.getKey(), null));
								}
							}
						}
					}
				}
			}
		}
		return gSequence;
	}

	private void mergeModelMasterDetail(WorkFlowModelMaster masterModelDetail, WorkFlowModelMaster modelDetail) {
		masterModelDetail.getActivities().addAll(modelDetail.getActivities());
		masterModelDetail.getNetworkSequence().addAll(modelDetail.getNetworkSequence());
		
	}
	
	@SuppressWarnings("unchecked")
	private MultiValueMap<String, ProjectProcessDetail> getProcessForProject(String projectId) {
		MultiValueMap<String, ProjectProcessDetail> prjDetails = null;
//		List<ProjectProcessDetail> prjPrcDetails = null;
		ProjectProcessDetail prjPrcDetail = null;
		if(!ServicesUtil.isEmpty(projectId)) {
			String query = " SELECT PM.PROCESS_ID, PE.NAME, PE.STARTED_AT "
					+ " FROM \"NPI_WORKBOX_USER\".\"PROJECT_PROCESS_MAP\" PM JOIN PROCESS_EVENTS PE ON PM.PROCESS_ID = PE.PROCESS_ID "
					+ " WHERE PROJECT_ID = '"+projectId+"' "
					+ " ORDER BY PE.STARTED_AT ASC";
			SQLQuery sqlQuery = getSession().createSQLQuery(query);
			List<Object[]> resultList = sqlQuery.list();
			if(!ServicesUtil.isEmpty(resultList) && resultList.size() > 0) {
//				prjPrcDetails = new ArrayList<ProjectProcessDetail>();
				prjDetails = new LinkedMultiValueMap<String, ProjectProcessDetail>();
				for(Object[] object : resultList) {
					prjPrcDetail = new ProjectProcessDetail();
					prjPrcDetail.setProcessId(ServicesUtil.asString(object[0]));
					prjPrcDetail.setProcessName(ServicesUtil.asString(object[1]));
					prjPrcDetail.setStartedAt(ServicesUtil.resultAsDate(object[2]));

					prjDetails.add(prjPrcDetail.getProcessName(), prjPrcDetail);
//					prjPrcDetails.add(prjPrcDetail);
				}
			}
		}
		return prjDetails;
	}
	
	@SuppressWarnings("unchecked")
	private MultiValueMap<Integer, String> getProjectRanking() {
		MultiValueMap<Integer, String> rankings = null;
		List<ProjectProcessRanking> result = getSession().createCriteria(ProjectProcessRanking.class).list();
		if(!ServicesUtil.isEmpty(result) && result.size() > 0) {
			rankings = new LinkedMultiValueMap<Integer, String>();
			for(ProjectProcessRanking rank : result) {
				rankings.add(rank.getRank(), rank.getProcessName());
			}
		}
		return rankings;
	}
	
	public void insertProjectRankings() {
		ProjectProcessRanking ranking = null;
		ranking = new ProjectProcessRanking("NPI", "detailed_scoping_wf", 1);
		this.getSession().saveOrUpdate(ranking);
		ranking = new ProjectProcessRanking("NPI", "material_definition_wf", 2);
		this.getSession().saveOrUpdate(ranking);
		ranking = new ProjectProcessRanking("NPI", "salesorg_definition_wf", 3);
		this.getSession().saveOrUpdate(ranking);
		ranking = new ProjectProcessRanking("NPI", "plant_definition_wf", 3);
		this.getSession().saveOrUpdate(ranking);
		ranking = new ProjectProcessRanking("NPI", "warehouse_definition_wf", 3);
		this.getSession().saveOrUpdate(ranking);
	}

	public static void main(String[] args) {
//		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(HibernateConfiguration.class);
//		WorkFlowModelDao workFlowModelDao = applicationContext.getBean(WorkFlowModelDao.class);
//		workFlowModelDao.insertProjectRankings();
//		System.out.println(workFlowModelDao.getProjectRanking());
//		applicationContext.close();
		
		
		String str = "989f7994-1ff4-11e9-9bbc-00163e817f7b||detailed_scoping_wf";
		System.out.println(getDefName(str));
		
		System.out.println(Pattern.quote("||"));
		
	}
	
}
