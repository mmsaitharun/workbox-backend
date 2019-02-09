package oneapp.workbox.services.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import oneapp.workbox.services.dto.UserTaskMappingDto;
import oneapp.workbox.services.entity.UserTaskMappingDo;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;

@Repository("UserTaskMappingDao")
@Transactional
public class UserTaskMappingDao {
	
	@Autowired
	SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	private static final Logger logger = LoggerFactory.getLogger(UserTaskMappingDao.class);
	
	private static final int _HIBERNATE_BATCH_SIZE = 300;

	protected UserTaskMappingDo importDto(UserTaskMappingDto fromDto) {
		UserTaskMappingDo mappingDo = null;
		if (!ServicesUtil.isEmpty(fromDto)) {
			mappingDo = new UserTaskMappingDo();
			mappingDo.setSubstitutedUser(fromDto.getSubstitutedUser());
			mappingDo.setSubstitutingUser(fromDto.getSubstitutingUser());
			mappingDo.setTaskId(fromDto.getTaskId());
		}
		return mappingDo;
	}

	protected UserTaskMappingDto exportDto(UserTaskMappingDo entity) {
		UserTaskMappingDto mappingDto = null;
		if (!ServicesUtil.isEmpty(entity)) {
			mappingDto = new UserTaskMappingDto();
			mappingDto.setId(entity.getId());
			mappingDto.setSubstitutedUser(entity.getSubstitutedUser());
			mappingDto.setTaskId(entity.getTaskId());
			mappingDto.setSubstitutingUser(entity.getSubstitutingUser());
		}
		return mappingDto;
	}

	public String createUserTask(String taskId, String substitutedUser, String substitutingUser) {
		String response = PMCConstant.FAILURE;
		try {
			UserTaskMappingDto dto = new UserTaskMappingDto();
			dto.setSubstitutedUser(substitutedUser);
			dto.setSubstitutingUser(substitutingUser);
			dto.setTaskId(taskId);
			this.getSession().save(importDto(dto));
			response = PMCConstant.SUCCESS;
		} catch (Exception e) {
			logger.error("[PMC][UserTaskMappingDao][createUserTaskMapping][error]" + e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	public Map<String, UserTaskMappingDto> getDisabledSubstitutionResult(String taskId) {
		Map<String, UserTaskMappingDto> map = new HashMap<String, UserTaskMappingDto>();
		try {
			UserTaskMappingDto dto = null;
			String queryString = "select ut.* from user_task_mapping ut"
					+ " join substitution_rule sr on sr.substituting_user=ut.substituting_user "
					+ "and sr.substituted_user=ut.substituted_user where ut.task_id='" + taskId
					+ "'  and not (sr.exist=1 and sr.is_active =1 and sr.is_enable=1)";

			Query query = this.getSession().createSQLQuery(queryString);
			List<Object[]> resultList = query.list();
			if (!ServicesUtil.isEmpty(resultList)) {
				for (Object[] obj : resultList) {
					dto = new UserTaskMappingDto();
					dto.setId((String) obj[0]);
					dto.setSubstitutingUser((String) obj[1]);
					dto.setSubstitutedUser((String) obj[2]);
					dto.setTaskId((String) obj[3]);
					map.put((String) obj[1], dto);
				}
			}
		} catch (Exception e) {
			logger.error("[PMC][UserTaskMappingDao][getSubstitutionResult][error]" + e.getMessage());
		}
		return map;
	}
	
	
	@SuppressWarnings("unchecked")
	public Map<String, UserTaskMappingDto> getSubstitutionResultNew(String taskId) {
		Map<String, UserTaskMappingDto> map = new HashMap<String, UserTaskMappingDto>();
		try {
			UserTaskMappingDto dto = null;
			String queryString = "select ut.* from user_task_mapping ut"
					+ " join substitution_rule sr on sr.substituting_user=ut.substituting_user "
					+ "and sr.substituted_user=ut.substituted_user where ut.task_id='" + taskId
					+ "'  and not (sr.exist=1 and sr.is_active =1 and sr.is_enable=1)";

			Query query = this.getSession().createSQLQuery(queryString);
			List<Object[]> resultList = query.list();
			if (!ServicesUtil.isEmpty(resultList)) {
				for (Object[] obj : resultList) {
					dto = new UserTaskMappingDto();
					dto.setId((String) obj[0]);
					dto.setSubstitutingUser((String) obj[1]);
					dto.setSubstitutedUser((String) obj[2]);
					dto.setTaskId((String) obj[3]);
					map.put((String) obj[3], dto);
				}
			}
		} catch (Exception e) {
			logger.error("[PMC][UserTaskMappingDao][getSubstitutionResult][error]" + e.getMessage());
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public List<UserTaskMappingDto> substitutionForDeletion(String substitutedUser, String substitutingUser) {
		List<UserTaskMappingDto> dtos = new ArrayList<UserTaskMappingDto>();
		String queryString = "select ut from UserTaskMappingDo ut where (ut.substitutingUser='" + substitutingUser
				+ "' and ut.substitutedUser='" + substitutedUser + "')";
		try {
			Query query = this.getSession().createQuery(queryString);
			List<UserTaskMappingDo> resultList = query.list();
			if (!ServicesUtil.isEmpty(resultList)) {
				for (UserTaskMappingDo entity : resultList) {
					dtos.add(exportDto(entity));
				}
			}
		} catch (Exception e) {
			System.err.println("[PMC][UserTaskMappingDao][substitutionForDeletion][error]" + e.getLocalizedMessage());
		}

		return dtos;
	}

	@SuppressWarnings("unchecked")
	public List<UserTaskMappingDto> getSubstitutedUserDisabled(String taskId) {
		List<UserTaskMappingDto> list = new ArrayList<UserTaskMappingDto>();
		try {
			String queryString = "select st from UserTaskMappingDo st where st.taskId='" + taskId + "'";
			Query query = this.getSession().createQuery(queryString);
			List<UserTaskMappingDo> listDo = query.list();
			for (UserTaskMappingDo entity : listDo) {
				list.add(exportDto(entity));
			}
		} catch (Exception e) {
			logger.error("[PMC][UserTaskMappingDao][getSubstitutedUserDisabled][error]" + e.getMessage());
		}
		return list;
	}

	public String deleteUserTask(UserTaskMappingDto dto) {
		try {
			String deleteQuery = "delete from user_task_mapping where id='" + dto.getId() + "'";
			Query q = this.getSession().createSQLQuery(deleteQuery);
			int result = (Integer) q.executeUpdate();
			if (result > 0)
				return PMCConstant.SUCCESS;
		} catch (Exception e) {
			logger.error("[PMC][UserTaskMappingDao][deleteUserTask][error]" + e.getMessage());
		}
		return PMCConstant.FAILURE;
	}

	public String recordExistsOrNot(String taskId, String substituted, String substituting) {
		try {
			String queryString = "select count(*) from user_task_mapping where task_id='" + taskId
					+ "' and substituted_user ='" + substituted + "' and substituting_user='" + substituting + "'";
			BigInteger result = (BigInteger) this.getSession().createSQLQuery(queryString).list().get(0);
			if (result.intValue() > 0)
				return PMCConstant.SUCCESS;
		} catch (Exception e) {
			logger.error("[PMC][UserTaskMappingDao][recordExistsOrNot][error]" + e.getMessage());
		}
		return PMCConstant.FAILURE;
	}

	public String deleteDisabledSubstitution(String substitutedUser, String substitutingUser) {
		try {
			String deleteQuery = "delete from user_task_mapping where substituted_user='" + substitutedUser
					+ "' and substituting_user='" + substitutingUser + "'";
			Query q = this.getSession().createSQLQuery(deleteQuery);
			int result = (Integer) q.executeUpdate();
			if (result > 0)
				return PMCConstant.SUCCESS;
		} catch (Exception e) {
			logger.error("[PMC][UserTaskMappingDao][deleteUserTask][error]" + e.getMessage());
		}
		return PMCConstant.FAILURE;
	}
	
	//newly added from agco demo
	@SuppressWarnings("unchecked")
	public List<String> getTaskOfSubstitutingUser(String SubstitutedUser, String SubstitutingUser) {
		List<String> taskList = null;
		try {
			String queryString = " SELECT TASK_ID  FROM USER_TASK_MAPPING WHERE upper(SUBSTITUTING_USER)=upper('"
					+ SubstitutingUser + "') GROUP BY TASK_ID HAVING COUNT(TASK_ID)<2";
			Query query = this.getSession().createSQLQuery(queryString);
			taskList = query.list();
		} catch (Exception e) {
			System.err.println("[PMC][UserTaskMappingDao][getTaskOfSubstitutingUser][error]" + e.getMessage());
		}
		return taskList;
	}
	
	public void saveOrUpdateTasks(List<UserTaskMappingDto> list) {
		System.err.println("[started][save or update][usermapping]");

		Session session = null;
		try {
			if (!ServicesUtil.isEmpty(list) && list.size() > 0) {
				session = this.getSession();
				for (int i = 0; i < list.size(); i++) {
					session.saveOrUpdate(importDto(list.get(i)));
					if (i % _HIBERNATE_BATCH_SIZE == 0 && i > 0) {
						session.flush();
						session.clear();
					}
				}
				if (!session.getTransaction().wasCommitted()) {
					session.flush();
				}
				// session.close();
			}
		} catch (Exception e) {
			System.err.println("[Batch insert][userTaskMapping]" + e.getLocalizedMessage());
		}
		System.err.println("[Ended][save or update][userMapping]");

	}
	

}