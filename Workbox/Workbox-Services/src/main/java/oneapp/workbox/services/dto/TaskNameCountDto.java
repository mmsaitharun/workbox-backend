package oneapp.workbox.services.dto;

import java.math.BigInteger;

public class TaskNameCountDto {

	private String strName;
	private BigInteger taskCount;

	public String getStrName() {
		return strName;
	}

	public void setStrName(String strName) {
		this.strName = strName;
	}

	
	public BigInteger getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(BigInteger taskCount) {
		this.taskCount = taskCount;
	}

	@Override
	public String toString() {
		return "TaskNameCountDto [strName=" + strName + ", taskCount=" + taskCount + "]";
	}

}
