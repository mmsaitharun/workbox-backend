package oneapp.workbox.services.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private Map<String, String> groupMappings;

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public void saveOrUpdateGroups(GroupsMapping groupsMapping) {
		this.getSession().saveOrUpdate(groupsMapping);
	}
	
	public String getGroupsMapping(String groupName) {
		String groups = null;
		if(ServicesUtil.isEmpty(groupMappings)) {
			refreshAllGroupsMappings();
			groups = getGroupsMapping(groupName);
		} else {
			groups = groupMappings.get(groupName);
		}
		return groups;
	}
	
	public void emptyGroupsMapping() {
		groupMappings = null;
	}
	
	@SuppressWarnings("unchecked")
	public List<GroupsMapping> getGroupMappingsResponse() {
		return this.getSession().createCriteria(GroupsMapping.class).list();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> refreshAllGroupsMappings() {
		Map<String, String> mappings = new HashMap<String, String>();
		List<GroupsMapping> groupMappings = this.getSession().createCriteria(GroupsMapping.class).list();
		for(GroupsMapping group : groupMappings) {
			mappings.put(group.getGroupName(), group.getUsers());
		}
		this.groupMappings = mappings;
		return mappings;
	}
	
	public List<String> getUsersUnderGroup(String groupName) {
		String users = this.getGroupsMapping(groupName);
		List<String> groupUsers = null;
		if(!ServicesUtil.isEmpty(users)) {
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
