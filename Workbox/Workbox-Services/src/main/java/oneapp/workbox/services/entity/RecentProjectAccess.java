package oneapp.workbox.services.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "RECENT_PROJECT_ACCESS")
public class RecentProjectAccess implements Serializable {

	private static final long serialVersionUID = 1L;

	public RecentProjectAccess() {
		super();
	}

	public RecentProjectAccess(String projectId, Date lastAccessed, String userAccessed) {
		super();
		this.projectId = projectId;
		this.lastAccessed = lastAccessed;
		this.userAccessed = userAccessed;
	}

	@Id
	@Column(name = "PROJECT_ID", length = 50)
	private String projectId;

	@Column(name = "LAST_ACCESSED")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAccessed;

	@Id
	@Column(name = "USER_ACCESSED")
	private String userAccessed;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public Date getLastAccessed() {
		return lastAccessed;
	}

	public void setLastAccessed(Date lastAccessed) {
		this.lastAccessed = lastAccessed;
	}

	public String getUserAccessed() {
		return userAccessed;
	}

	public void setUserAccessed(String userAccessed) {
		this.userAccessed = userAccessed;
	}

	@Override
	public String toString() {
		return "RecentProjectAccess [projectId=" + projectId + ", lastAccessed=" + lastAccessed + ", userAccessed="
				+ userAccessed + "]";
	}

}
