package oneapp.workbox.services.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import oneapp.workbox.services.dto.UserDetailsDto;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;

@Repository
@Transactional
public class UserDetailsDao {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private ConfigDao configDao;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@SuppressWarnings("unchecked")
	public UserDetailsDto getUserDetails(UserDetailsDto userDetails) {
		UserDetailsDto user = null;
		String query = "SELECT USER_ID, IS_DELETED, STATUS, USER_EMAIL, USER_NAME FROM \"WORKBOX_AGCO_TEMP\".\"APT_USER\" ";
		if(!ServicesUtil.isEmpty(userDetails)) {
			user = new UserDetailsDto();
			query += "WHERE ";
			if(!ServicesUtil.isEmpty(userDetails.getUserId())) {
				query += " UPPER(USER_ID) LIKE UPPER('%"+userDetails.getUserId()+"%') AND ";
			}
			if(!ServicesUtil.isEmpty(userDetails.getEmailId())) {
				query += " UPPER(USER_EMAIL) LIKE UPPER('%"+userDetails.getUserId()+"%')";
			} else {
				query += " 1 = 1 ";
			}
			List<Object[]> row = this.getSession().createSQLQuery(query).list();
			if(!ServicesUtil.isEmpty(row) && row.size() == 1) {
				Object[] userResult = row.get(0);
				user.setUserId(ServicesUtil.isEmpty(userResult[0]) ? null : (String) userResult[0]);
				user.setEmailId(ServicesUtil.isEmpty(userResult[3]) ? null : (String) userResult[3]);
				user.setDisplayName(ServicesUtil.isEmpty(userResult[4]) ? null : (String) userResult[4]);
			}
		}
		return user;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getUserDetailResponse() {
		return this.getSession().createSQLQuery("SELECT USER_ID, IS_DELETED, STATUS, USER_EMAIL, USER_NAME FROM \"WORKBOX_AGCO_TEMP\".\"APT_USER\"").list();
	}
	
	public UserDetailsDto getUserDetails(com.sap.security.um.user.User user) {
		UserDetailsDto userDetails = null;
		String adminGroup = null;
		if(!ServicesUtil.isEmpty(user) && !ServicesUtil.isEmpty(user.getName())) {
			String query = "SELECT USER_ID, USER_EMAIL, USER_NAME FROM \"WORKBOX_AGCO_TEMP\".\"APT_USER\" WHERE UPPER(USER_ID) LIKE UPPER('"+user.getName()+"')";
			@SuppressWarnings("unchecked")
			List<Object[]> row = this.getSession().createSQLQuery(query).list();
			if(!ServicesUtil.isEmpty(row) && row.size() == 1) {
				userDetails = new UserDetailsDto();
				Object[] userResult = row.get(0);
				userDetails.setUserId(ServicesUtil.isEmpty(userResult[0]) ? null : (String) userResult[0]);
				userDetails.setEmailId(ServicesUtil.isEmpty(userResult[1]) ? null : (String) userResult[1]);
				userDetails.setDisplayName(ServicesUtil.isEmpty(userResult[2]) ? null : (String) userResult[2]);
			}
			if(!ServicesUtil.isEmpty(userDetails)) {
				userDetails.setIsAdmin(false);
				userDetails.setUserGroups(new ArrayList<>(user.getGroups()));
				userDetails.setUserRoles(new ArrayList<>(user.getRoles()));
				adminGroup = configDao.getConfigurationByRef(PMCConstant.ADMIN_GROUP_CONFIG_REF);
				if(!ServicesUtil.isEmpty(adminGroup) && userDetails.getUserGroups().contains(adminGroup))
					userDetails.setIsAdmin(true);
			}
		}
		return userDetails;
	}

}
