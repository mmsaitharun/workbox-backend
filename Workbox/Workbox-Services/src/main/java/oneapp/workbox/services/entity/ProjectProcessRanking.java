package oneapp.workbox.services.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PRJ_PROCESS_RANKING")
public class ProjectProcessRanking implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProjectProcessRanking() {
		super();
	}

	public ProjectProcessRanking(String project, String processName, Integer rank) {
		super();
		this.project = project;
		this.processName = processName;
		this.rank = rank;
	}

	@Id
	@Column(name = "ID", length = 70)
	private String id;

	@Column(name = "PROJECT", length = 50)
	private String project;

	@Column(name = "PROCESS_NAME", length = 50)
	private String processName;

	@Column(name = "RANK")
	private Integer rank;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	@Override
	public String toString() {
		return "ProjectProcessRanking [id=" + id + ", project=" + project + ", processName=" + processName + ", rank="
				+ rank + "]";
	}

}
