package oneapp.workbox.services.dao;

import static oneapp.workbox.services.adapters.WorkFlowModelParser.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
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

import oneapp.workbox.services.adapters.WorkFlowModelMaster;
import oneapp.workbox.services.adapters.WorkFlowModelParser;
import oneapp.workbox.services.dto.ExecutionLog;
import oneapp.workbox.services.dto.RestResponse;
import oneapp.workbox.services.dto.WorkFlowArtifactSequence;
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

	private Session getSession() {
		return sessionFactory.getCurrentSession();
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
				networkSequence.add(artifactSeq);
			}
		}
		workflowModel.setNetworkSequence(networkSequence);

		// Forming Status of WF
		MultiValueMap<String, ExecutionLog> executionLogs = getExecutionLogs(processId);
		for (WorkFlowActivity activity : workflowModel.getActivities()) {
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

}
