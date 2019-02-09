package oneapp.workbox.services.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "GROUPS_MAPPING")
public class GroupsMapping {

	public GroupsMapping() {
		super();
	}

	public GroupsMapping(String groupName, String users) {
		super();
		this.groupName = groupName;
		this.users = users;
	}

	@Id
	@Column(name = "GROUP_NAME", length = 50)
	private String groupName;

	@Column(name = "USERS", length = 500)
	private String users;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "GroupsMapping [groupName=" + groupName + ", users=" + users + "]";
	}

}
