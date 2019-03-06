package oneapp.workbox.services.adapters;

import java.util.List;

import oneapp.workbox.services.dto.ProcessEventsDto;
import oneapp.workbox.services.dto.TaskEventsDto;
import oneapp.workbox.services.dto.TaskOwnersDto;
import oneapp.workbox.services.dto.WorkBoxDto;
import oneapp.workbox.services.entity.ProcessDetail;
import oneapp.workbox.services.entity.ProjectProcessMapping;

public class AdminParseResponse {

	public AdminParseResponse(List<TaskEventsDto> tasks, List<ProcessEventsDto> processes, List<TaskOwnersDto> owners,
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

	private List<TaskEventsDto> tasks;
	private List<ProcessEventsDto> processes;
	private List<TaskOwnersDto> owners;
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

	public List<TaskEventsDto> getTasks() {
		return tasks;
	}

	public void setTasks(List<TaskEventsDto> tasks) {
		this.tasks = tasks;
	}

	public List<ProcessEventsDto> getProcesses() {
		return processes;
	}

	public void setProcesses(List<ProcessEventsDto> processes) {
		this.processes = processes;
	}

	public List<TaskOwnersDto> getOwners() {
		return owners;
	}

	public void setOwners(List<TaskOwnersDto> owners) {
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