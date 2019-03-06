package oneapp.workbox.services.adapters;

import java.util.List;

import oneapp.workbox.services.dto.WorkFlowArtifactSequence;
import oneapp.workbox.services.entity.WorkFlowActivity;
import oneapp.workbox.services.entity.WorkFlowEvent;
import oneapp.workbox.services.entity.WorkFlowExclusiveGateway;
import oneapp.workbox.services.entity.WorkFlowSequenceFlow;

public class WorkFlowModelMaster {

	public WorkFlowModelMaster() {
		super();
	}

	public WorkFlowModelMaster(List<WorkFlowEvent> events, List<WorkFlowActivity> activities,
			List<WorkFlowSequenceFlow> sequences, List<WorkFlowExclusiveGateway> exclusiveGateways, 
			List<WorkFlowArtifactSequence> groupSequence, List<WorkFlowArtifactSequence> networkSequence, 
			List<WorkFlowMasterModelGroup> groups) {
		super();
		this.events = events;
		this.activities = activities;
		this.sequences = sequences;
		this.exclusiveGateways = exclusiveGateways;
		this.networkSequence = networkSequence;
		this.groupSequence = groupSequence;
		this.groups = groups;
	}

	private List<WorkFlowEvent> events;
	private List<WorkFlowActivity> activities;
	private List<WorkFlowSequenceFlow> sequences;
	private List<WorkFlowExclusiveGateway> exclusiveGateways;

	private List<WorkFlowArtifactSequence> networkSequence;
	private List<WorkFlowArtifactSequence> groupSequence;
	
	private List<WorkFlowMasterModelGroup> groups;
	
	public List<WorkFlowEvent> getEvents() {
		return events;
	}

	public void setEvents(List<WorkFlowEvent> events) {
		this.events = events;
	}

	public List<WorkFlowActivity> getActivities() {
		return activities;
	}

	public void setActivities(List<WorkFlowActivity> activities) {
		this.activities = activities;
	}

	public List<WorkFlowSequenceFlow> getSequences() {
		return sequences;
	}

	public void setSequences(List<WorkFlowSequenceFlow> sequences) {
		this.sequences = sequences;
	}

	public List<WorkFlowExclusiveGateway> getExclusiveGateways() {
		return exclusiveGateways;
	}

	public void setExclusiveGateways(List<WorkFlowExclusiveGateway> exclusiveGateways) {
		this.exclusiveGateways = exclusiveGateways;
	}

	public List<WorkFlowArtifactSequence> getNetworkSequence() {
		return networkSequence;
	}

	public void setNetworkSequence(List<WorkFlowArtifactSequence> networkSequence) {
		this.networkSequence = networkSequence;
	}

	public List<WorkFlowMasterModelGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<WorkFlowMasterModelGroup> groups) {
		this.groups = groups;
	}

	public List<WorkFlowArtifactSequence> getGroupSequence() {
		return groupSequence;
	}

	public void setGroupSequence(List<WorkFlowArtifactSequence> groupSequence) {
		this.groupSequence = groupSequence;
	}

	@Override
	public String toString() {
		return "WorkFlowModelMaster [events=" + events + ", activities=" + activities + ", sequences=" + sequences
				+ ", exclusiveGateways=" + exclusiveGateways + ", networkSequence=" + networkSequence
				+ ", groupSequence=" + groupSequence + ", groups=" + groups + "]";
	}

}
