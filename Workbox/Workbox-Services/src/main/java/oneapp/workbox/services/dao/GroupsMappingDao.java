package oneapp.workbox.services.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import oneapp.workbox.services.entity.GroupsMapping;
import oneapp.workbox.services.util.ServicesUtil;

@Repository
@Transactional
public class GroupsMappingDao {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public void saveOrUpdateGroups(GroupsMapping groupsMapping) {
		this.getSession().saveOrUpdate(groupsMapping);
	}
	
	public GroupsMapping getGroupsMapping(String groupName) {
		return (GroupsMapping) this.getSession().load(GroupsMapping.class, groupName);
	}
	
	public List<String> getUsersUnderGroup(String groupName) {
		GroupsMapping groupsMapping = this.getGroupsMapping(groupName);
		List<String> groupUsers = null;
		if(!ServicesUtil.isEmpty(groupsMapping)) {
			String users = groupsMapping.getUsers();
			groupUsers = new ArrayList<>();
			if(!ServicesUtil.isEmpty(users) && users.contains(",")) {
				String[] usersArray = users.split(",");
				for(String u : usersArray) {
					groupUsers.add(u.trim());
				}
			} else if(!ServicesUtil.isEmpty(users) && !users.contains(",")) {
				groupUsers.add(users.trim());
			}
		}
		return groupUsers;
	}
	
}
