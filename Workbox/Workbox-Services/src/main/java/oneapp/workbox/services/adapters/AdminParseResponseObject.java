package oneapp.workbox.services.adapters;

import java.util.List;

import oneapp.workbox.services.dto.WorkBoxDto;
import oneapp.workbox.services.entity.ProcessDetail;
import oneapp.workbox.services.entity.ProcessEventsDo;
import oneapp.workbox.services.entity.ProjectProcessMapping;
import oneapp.workbox.services.entity.TaskEventsDo;
import oneapp.workbox.services.entity.TaskOwnersDo;

public class AdminParseResponseObject {

	public AdminParseResponseObject(List<TaskEventsDo> tasks, List<ProcessEventsDo> processes, List<TaskOwnersDo> owners,
			List<WorkBoxDto> workbox, int resultCount, List<ProjectProcessMapping> prjPrcMaps,
			List<ProcessDetail> processDetails) {
		super();
		this.tasks = tasks;
		this.processes = processes;
		this.owners = owners;
		this.workbox = workbox;
		this.resultCount = resultCount;
		this.prjPrcMaps = prjPrcMaps;
		this.processDetails = processDetails;
	}

	private List<TaskEventsDo> tasks;
	private List<ProcessEventsDo> processes;
	private List<TaskOwnersDo> owners;
	private List<WorkBoxDto> workbox;
	private int resultCount;

	// Added for Colgate
	private List<ProjectProcessMapping> prjPrcMaps;
	private List<ProcessDetail> processDetails;

	public List<ProcessDetail> getProcessDetails() {
		return processDetails;
	}

	public void setProcessDetails(List<ProcessDetail> processDetails) {
		this.processDetails = processDetails;
	}

	public List<ProjectProcessMapping> getPrjPrcMaps() {
		return prjPrcMaps;
	}

	public void setPrjPrcMaps(List<ProjectProcessMapping> prjPrcMaps) {
		this.prjPrcMaps = prjPrcMaps;
	}

	public List<TaskEventsDo> getTasks() {
		return tasks;
	}

	public void setTasks(List<TaskEventsDo> tasks) {
		this.tasks = tasks;
	}

	public List<ProcessEventsDo> getProcesses() {
		return processes;
	}

	public void setProcesses(List<ProcessEventsDo> processes) {
		this.processes = processes;
	}

	public List<TaskOwnersDo> getOwners() {
		return owners;
	}

	public void setOwners(List<TaskOwnersDo> owners) {
		this.owners = owners;
	}

	public List<WorkBoxDto> getWorkbox() {
		return workbox;
	}

	public void setWorkbox(List<WorkBoxDto> workbox) {
		this.workbox = workbox;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	@Override
	public String toString() {
		return "AdminParseResponse [tasks=" + tasks + ", processes=" + processes + ", owners=" + owners + ", workbox="
				+ workbox + ", resultCount=" + resultCount + "]";
	}

}