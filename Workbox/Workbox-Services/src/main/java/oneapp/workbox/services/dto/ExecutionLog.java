package oneapp.workbox.services.dto;

import java.util.Comparator;
import java.util.Date;

import oneapp.workbox.services.util.ServicesUtil;

public class ExecutionLog implements Comparator<ExecutionLog> {

	public ExecutionLog() {
		super();
	}

	public ExecutionLog(String id, String type, Date timeStamp, String activityId) {
		super();
		this.id = id;
		this.type = type;
		this.timeStamp = timeStamp;
		this.activityId = activityId;
	}

	private String id;
	private String type;
	private Date timeStamp;
	private String activityId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	@Override
	public String toString() {
		return "ExecutionLog [id=" + id + ", type=" + type + ", timeStamp=" + timeStamp + ", activityId=" + activityId
				+ "]";
	}

	@Override
	public int compare(ExecutionLog o1, ExecutionLog o2) {
		if(!ServicesUtil.isEmpty(o1.getTimeStamp()) && !ServicesUtil.isEmpty(o2.getTimeStamp()))
			return o2.getTimeStamp().compareTo(o1.getTimeStamp());
		return 0;
	}

}
