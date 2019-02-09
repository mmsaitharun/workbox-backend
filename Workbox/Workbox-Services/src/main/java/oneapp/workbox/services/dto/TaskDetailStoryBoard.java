package oneapp.workbox.services.dto;

import java.util.List;

import oneapp.workbox.services.entity.ProcessDetail;
import oneapp.workbox.services.entity.TaskDetail;

public class TaskDetailStoryBoard {

	public TaskDetailStoryBoard() {
		super();
	}

	public TaskDetailStoryBoard(ProcessDetail processDetail, List<TaskDetail> taskDetails, Integer tasksCount,
			ResponseMessage responseMessage) {
		super();
		this.processDetail = processDetail;
		this.taskDetails = taskDetails;
		this.tasksCount = tasksCount;
		this.responseMessage = responseMessage;
	}
	
	public TaskDetailStoryBoard(ProcessDetailMasterDto processDetailMasterDto, List<TaskDetail> taskDetails, Integer tasksCount,
			ResponseMessage responseMessage) {
		super();
		this.processDetailMasterDto = processDetailMasterDto;
		this.taskDetails = taskDetails;
		this.tasksCount = tasksCount;
		this.responseMessage = responseMessage;
	}

	private ProcessDetail processDetail;
	private List<TaskDetail> taskDetails;
	private int tasksCount;

	private ResponseMessage responseMessage;
	
	private ProcessDetailMasterDto processDetailMasterDto;
	
	public ProcessDetailMasterDto getProcessDetailMasterDto() {
		return processDetailMasterDto;
	}

	public void setProcessDetailMasterDto(ProcessDetailMasterDto processDetailMasterDto) {
		this.processDetailMasterDto = processDetailMasterDto;
	}

	public int getTasksCount() {
		return tasksCount;
	}

	public void setTasksCount(int tasksCount) {
		this.tasksCount = tasksCount;
	}

	public ProcessDetail getProcessDetail() {
		return processDetail;
	}

	public void setProcessDetail(ProcessDetail processDetail) {
		this.processDetail = processDetail;
	}

	public List<TaskDetail> getTaskDetails() {
		return taskDetails;
	}

	public void setTaskDetails(List<TaskDetail> taskDetails) {
		this.taskDetails = taskDetails;
	}

	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}

	@Override
	public String toString() {
		return "TaskDetailStoryBoard [processDetail=" + processDetail + ", taskDetails=" + taskDetails + ", tasksCount="
				+ tasksCount + ", responseMessage=" + responseMessage + "]";
	}

}
