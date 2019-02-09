package oneapp.workbox.services.adapters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import oneapp.workbox.services.entity.WorkFlowActivity;
import oneapp.workbox.services.entity.WorkFlowArtifact;
import oneapp.workbox.services.entity.WorkFlowEvent;
import oneapp.workbox.services.entity.WorkFlowExclusiveGateway;
import oneapp.workbox.services.entity.WorkFlowSequenceFlow;
import oneapp.workbox.services.util.ServicesUtil;

public class WorkFlowModelParser {

	public static void main(String[] args) {
		System.out.println(WorkFlowModelParser.parseWorkflowModel("plant_definition_wf"));
	}

	static final String WFS_MODEL_CLASS = "com.sap.bpm.wfs.Model";
	static final String WFS_START_EVENT_CLASS = "com.sap.bpm.wfs.StartEvent";
	static final String WFS_END_EVENT_CLASS = "com.sap.bpm.wfs.EndEvent";
	static final String WFS_USER_TASK_CLASS = "com.sap.bpm.wfs.UserTask";
	static final String WFS_PARALLEL_GATEWAY_CLASS = "com.sap.bpm.wfs.ParallelGateway";
	static final String WFS_SERVICE_TASK_CLASS = "com.sap.bpm.wfs.ServiceTask";
	static final String WFS_EXCLUSIVE_GATEWAY_CLASS = "com.sap.bpm.wfs.ExclusiveGateway";
	static final String WFS_SCRIPT_TASK_CLASS = "com.sap.bpm.wfs.ScriptTask";
	static final String WFS_SEQUENCE_FLOW_CLASS = "com.sap.bpm.wfs.SequenceFlow";
	static final String WFS_LAST_IDS_CLASS = "com.sap.bpm.wfs.LastIDs";

	public static WorkFlowModelMaster parseWorkflowModel(String workFlowDefinitionId) {
		WorkFlowModelMaster workflowModelMaster = null;
		WorkFlowArtifact artifact = null;

		List<WorkFlowEvent> events;
		List<WorkFlowActivity> activities;
		List<WorkFlowSequenceFlow> sequences;
		List<WorkFlowExclusiveGateway> exclusiveGateways;

		JSONObject workflowModel = WorkFlowModelDownloader.getWorkflowModel(workFlowDefinitionId);
		if (!ServicesUtil.isEmpty(workflowModel)) {
			workflowModelMaster = new WorkFlowModelMaster();

			events = new ArrayList<WorkFlowEvent>();
			activities = new ArrayList<WorkFlowActivity>();
			sequences = new ArrayList<WorkFlowSequenceFlow>();
			exclusiveGateways = new ArrayList<WorkFlowExclusiveGateway>();

			JSONObject jsonContents = workflowModel.optJSONObject("contents");
			Iterator<String> keys = jsonContents.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				JSONObject jsonArtifact = jsonContents.optJSONObject(key);
				if (!ServicesUtil.isEmpty(jsonArtifact) && jsonArtifact instanceof org.json.JSONObject) {
					artifact = getWorkFlowArtifact(jsonArtifact, key, workFlowDefinitionId);
					if (!ServicesUtil.isEmpty(artifact) && !ServicesUtil.isEmpty(artifact.getId())) {
						if (artifact instanceof WorkFlowEvent) {
							events.add((WorkFlowEvent) artifact);
						} else if (artifact instanceof WorkFlowActivity) {
							activities.add((WorkFlowActivity) artifact);
						} else if (artifact instanceof WorkFlowSequenceFlow) {
							sequences.add((WorkFlowSequenceFlow) artifact);
						} else if (artifact instanceof WorkFlowExclusiveGateway) {
							exclusiveGateways.add((WorkFlowExclusiveGateway) artifact);
						}
					}
				}
			}
			if (!ServicesUtil.isEmpty(workflowModelMaster)) {
				workflowModelMaster.setEvents(events);
				workflowModelMaster.setActivities(activities);
				workflowModelMaster.setExclusiveGateways(exclusiveGateways);
				workflowModelMaster.setSequences(sequences);
			}
		}
		return workflowModelMaster;
	}

	private static WorkFlowArtifact getWorkFlowArtifact(JSONObject jsonArtifact, String id, String workFlowDefId) {
		WorkFlowArtifact workFlowArtifact = null;
		if (!ServicesUtil.isEmpty(jsonArtifact) && !ServicesUtil.isEmpty(jsonArtifact.optString("classDefinition"))) {
			switch (jsonArtifact.optString("classDefinition")) {
			case WFS_MODEL_CLASS:
				// workFlowArtifact = getModel(jsonArtifact);
				break;
//				workFlowArtifact = getEvent(jsonArtifact, id, workFlowDefId);
//				break;
			case WFS_START_EVENT_CLASS:
			case WFS_END_EVENT_CLASS:
			case WFS_USER_TASK_CLASS:
			case WFS_SERVICE_TASK_CLASS:
			case WFS_SCRIPT_TASK_CLASS:
				workFlowArtifact = getUserTask(jsonArtifact, id, workFlowDefId);
				break;
			case WFS_EXCLUSIVE_GATEWAY_CLASS:
			case WFS_PARALLEL_GATEWAY_CLASS:
				workFlowArtifact = getExclusiveGateway(jsonArtifact, id, workFlowDefId);
				break;
			case WFS_SEQUENCE_FLOW_CLASS:
				workFlowArtifact = getSequenceFlow(jsonArtifact, id, workFlowDefId);
				break;
			case WFS_LAST_IDS_CLASS:
				// workFlowArtifact = getLastIds(jsonArtifact);
				break;
			}
		}
		return workFlowArtifact;
	}

	private static WorkFlowArtifact getExclusiveGateway(JSONObject jsonArtifact, String id, String workFlowDefId) {
		WorkFlowExclusiveGateway exclusiveGateway = new WorkFlowExclusiveGateway();
		exclusiveGateway.setId(id);
		exclusiveGateway.setArtifactId(jsonArtifact.optString("id"));
		exclusiveGateway.setArtifactClassDefinition(jsonArtifact.optString("classDefinition"));
		exclusiveGateway.setArtifactName(jsonArtifact.optString("name"));
		exclusiveGateway.setGatewayDefault(jsonArtifact.optString("default"));
		exclusiveGateway.setWorkFlowDefId(workFlowDefId);
		return exclusiveGateway;
	}

	@SuppressWarnings("unused")
	private static WorkFlowArtifact getEvent(JSONObject jsonArtifact, String id, String workFlowDefId) {
		WorkFlowEvent event = new WorkFlowEvent();
		event.setId(id);
		event.setArtifactId(jsonArtifact.optString("id"));
		event.setArtifactClassDefinition(jsonArtifact.optString("classDefinition"));
		event.setArtifactName(jsonArtifact.optString("name"));
		event.setWorkFlowDefId(workFlowDefId);
		return event;
	}

	private static WorkFlowArtifact getSequenceFlow(JSONObject jsonArtifact, String id, String workFlowDefId) {
		WorkFlowSequenceFlow sequence = new WorkFlowSequenceFlow();
		sequence.setId(id);
		sequence.setArtifactId(jsonArtifact.optString("id"));
		sequence.setArtifactClassDefinition(jsonArtifact.optString("classDefinition"));
		sequence.setArtifactName(jsonArtifact.optString("name"));
		sequence.setSourceRef(jsonArtifact.optString("sourceRef"));
		sequence.setTargetRef(jsonArtifact.optString("targetRef"));
		sequence.setWorkFlowDefId(workFlowDefId);
		return sequence;
	}

	private static WorkFlowArtifact getUserTask(JSONObject jsonArtifact, String id, String workFlowDefId) {
		WorkFlowActivity activity = new WorkFlowActivity();
		activity.setId(id);
		activity.setArtifactId(jsonArtifact.optString("id"));
		activity.setArtifactClassDefinition(jsonArtifact.optString("classDefinition"));
		activity.setArtifactName(jsonArtifact.optString("name"));
		activity.setActivityPriority(jsonArtifact.optString("priority"));
		activity.setWorkFlowDefId(workFlowDefId);
		return activity;
	}

}
