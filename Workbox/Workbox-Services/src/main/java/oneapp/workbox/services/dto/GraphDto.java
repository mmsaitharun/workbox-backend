package oneapp.workbox.services.dto;

import java.math.BigInteger;
import java.util.List;

public class GraphDto {

	private BigInteger totalActiveTask;
	private BigInteger openTask;
	private BigInteger pendingTask;
	private BigInteger slaBreachedTask;
	private BigInteger myTask;

	private List<TotalActiveTaskDto> totalActiveTaskList;

	private List<UserWorkCountDto> userWorkCountList;

	private List<TaskNameCountDto> taskCompletionTrendList;
	
	private List<TaskNameCountDto> taskDonutList;
	
	
	public List<TaskNameCountDto> getTaskDonutList() {
		return taskDonutList;
	}

	public void setTaskDonutList(List<TaskNameCountDto> taskDonutList) {
		this.taskDonutList = taskDonutList;
	}

	public List<TaskNameCountDto> getTaskCompletionTrendList() {
		return taskCompletionTrendList;
	}

	public void setTaskCompletionTrendList(List<TaskNameCountDto> taskCompletionTrendList) {
		this.taskCompletionTrendList = taskCompletionTrendList;
	}

	public BigInteger getTotalActiveTask() {
		return totalActiveTask;
	}

	public void setTotalActiveTask(BigInteger totalActiveTask) {
		this.totalActiveTask = totalActiveTask;
	}

	public BigInteger getOpenTask() {
		return openTask;
	}

	public void setOpenTask(BigInteger openTask) {
		this.openTask = openTask;
	}

	public BigInteger getPendingTask() {
		return pendingTask;
	}

	public void setPendingTask(BigInteger pendingTask) {
		this.pendingTask = pendingTask;
	}

	public BigInteger getSlaBreachedTask() {
		return slaBreachedTask;
	}

	public void setSlaBreachedTask(BigInteger slaBreachedTask) {
		this.slaBreachedTask = slaBreachedTask;
	}

	public BigInteger getMyTask() {
		return myTask;
	}

	public void setMyTask(BigInteger myTask) {
		this.myTask = myTask;
	}

	public List<TotalActiveTaskDto> getTotalActiveTaskList() {
		return totalActiveTaskList;
	}

	public void setTotalActiveTaskList(List<TotalActiveTaskDto> totalActiveTaskList) {
		this.totalActiveTaskList = totalActiveTaskList;
	}

	public List<UserWorkCountDto> getUserWorkCountList() {
		return userWorkCountList;
	}

	public void setUserWorkCountList(List<UserWorkCountDto> userWorkCountList) {
		this.userWorkCountList = userWorkCountList;
	}

	@Override
	public String toString() {
		return "GraphDto [totalActiveTask=" + totalActiveTask + ", openTask=" + openTask + ", pendingTask="
				+ pendingTask + ", slaBreachedTask=" + slaBreachedTask + ", myTask=" + myTask + ", totalActiveTaskList="
				+ totalActiveTaskList + ", userWorkCountList=" + userWorkCountList + ", taskCompletionTrendList="
				+ taskCompletionTrendList + ", taskDonutList=" + taskDonutList + "]";
	}

}
