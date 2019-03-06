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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((processId == null) ? 0 : processId.hashCode());
		result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectProcessMapping other = (ProjectProcessMapping) obj;
		if (processId == null) {
			if (other.processId != null)
				return false;
		} else if (!processId.equals(other.processId))
			return false;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		return true;
	}

}
