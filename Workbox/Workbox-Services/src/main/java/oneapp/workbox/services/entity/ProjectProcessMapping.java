package oneapp.workbox.services.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PROJECT_PROCESS_MAP")
public class ProjectProcessMapping implements Serializable {

	private static final long serialVersionUID = 3305729237617070495L;

	public ProjectProcessMapping() {
		super();
	}

	public ProjectProcessMapping(String processId, String projectId) {
		super();
		this.processId = processId;
		this.projectId = projectId;
	}

	@Id
	@Column(name = "PROCESS_ID", length = 45)
	private String processId;

	@Id
	@Column(name = "PROJECT_ID", length = 45)
	private String projectId;

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	@Override
	public String toString() {
		return "ProjectProcessMapping [processId=" + processId + ", projectId=" + projectId + "]";
	}

}
