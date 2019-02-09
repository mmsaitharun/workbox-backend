package oneapp.workbox.services.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity implementation class for Entity: ProcessEventsDo
 *
 */
@Entity
@Table(name = "PROCESS_EVENTS")
public class ProcessEventsDo implements Serializable {

	private static final long serialVersionUID = 1L;

	public ProcessEventsDo() {
		super();
	}

	@Id
	@Column(name = "PROCESS_ID", length = 32)
	private String processId;

	@Column(name = "NAME", length = 100)
	private String name;

	@Column(name = "SUBJECT", length = 100)
	private String subject;

	@Column(name = "STATUS", length = 100)
	private String status;

	@Column(name = "STARTED_BY", length = 255)
	private String startedBy;

	@Column(name = "STARTED_AT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startedAt;

	@Column(name = "COMPLETED_AT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date completedAt;

	@Column(name = "REQUEST_ID", length = 30)
	private String requestId;

	@Column(name = "STARTED_BY_DISP", length = 100)
	private String startedByDisplayName;

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStartedBy() {
		return startedBy;
	}

	public void setStartedBy(String startedBy) {
		this.startedBy = startedBy;
	}

	public Date getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}

	public Date getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(Date completedAt) {
		this.completedAt = completedAt;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getStartedByDisplayName() {
		return startedByDisplayName;
	}

	public void setStartedByDisplayName(String startedByDisplayName) {
		this.startedByDisplayName = startedByDisplayName;
	}

	@Override
	public String toString() {
		return "ProcessEventsDo [processId=" + processId + ", name=" + name + ", subject=" + subject + ", status=" + status + ", startedBy=" + startedBy + ", startedAt=" + startedAt + ", completedAt="
				+ completedAt + ", requestId=" + requestId + ", startedByDisplayName=" + startedByDisplayName + "]";
	}

}
