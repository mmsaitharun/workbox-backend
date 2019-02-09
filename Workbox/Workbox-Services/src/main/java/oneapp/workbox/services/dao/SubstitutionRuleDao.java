package oneapp.workbox.services.dao;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import oneapp.workbox.services.dto.SubstitutionRulesDto;
import oneapp.workbox.services.dto.TaskOwnersDto;
import oneapp.workbox.services.dto.UserTaskMappingDto;
import oneapp.workbox.services.entity.SubstitutionRuleDo;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;

@Repository
@Transactional
public class SubstitutionRuleDao {

	@Autowired
	SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Autowired
	UserIDPMappingDao userDao;

	@Autowired
	UserTaskMappingDao userTaskDao;

	@Autowired
	TaskOwnersDao taskOwnerDao;

	protected SubstitutionRuleDo importDto(SubstitutionRulesDto fromDto) {

		SubstitutionRuleDo entity = new SubstitutionRuleDo();
		if (!ServicesUtil.isEmpty(fromDto.getRuleId()))
			entity.setRuleId(fromDto.getRuleId());
		if (!ServicesUtil.isEmpty(fromDto.getSubstitutedUser()))
			entity.setSubstitutedUser(fromDto.getSubstitutedUser());
		if (!ServicesUtil.isEmpty(fromDto.getSubstitutedUserName()))
			entity.setSubstitutedUserName(fromDto.getSubstitutedUserName());
		if (!ServicesUtil.isEmpty(fromDto.getSubstitutingUser()))
			entity.setSubstitutingUser(fromDto.getSubstitutingUser());
		if (!ServicesUtil.isEmpty(fromDto.getSubstitutingUserName()))
			entity.setSubstitutingUserName(fromDto.getSubstitutingUserName());
		if (!ServicesUtil.isEmpty(fromDto.getEndDate()))
			entity.setEndDate(ServicesUtil.convertFromStringToDateSubstitution(fromDto.getEndDate()));
		if (!ServicesUtil.isEmpty(fromDto.getStartDate()))
			entity.setStartDate(ServicesUtil.convertFromStringToDateSubstitution(fromDto.getStartDate()));
		if (!ServicesUtil.isEmpty(fromDto.getUpdateAt()))
			entity.setUpdatedAt(ServicesUtil.convertFromStringToDateSubstitution(fromDto.getUpdateAt()));
		if (!ServicesUtil.isEmpty(fromDto.isActive()))
			entity.setActive(fromDto.isActive());
		if (!ServicesUtil.isEmpty(fromDto.isEnabled()))
			entity.setEnabled(fromDto.isEnabled());

		return entity;
	}

	protected SubstitutionRulesDto exportDto(SubstitutionRuleDo entity) {

		SubstitutionRulesDto dto = new SubstitutionRulesDto();
		if (!ServicesUtil.isEmpty(entity.getRuleId()))
			dto.setRuleId(entity.getRuleId());
		if (!ServicesUtil.isEmpty(entity.getSubstitutedUser()))
			dto.setSubstitutedUser(entity.getSubstitutedUser());
		if (!ServicesUtil.isEmpty(entity.getSubstitutedUserName()))
			dto.setSubstitutedUserName(entity.getSubstitutedUserName());
		if (!ServicesUtil.isEmpty(entity.getSubstitutingUser()))
			dto.setSubstitutingUser(entity.getSubstitutingUser());
		if (!ServicesUtil.isEmpty(entity.getSubstitutingUserName()))
			dto.setSubstitutingUserName(entity.getSubstitutingUserName());
		if (!ServicesUtil.isEmpty(entity.getEndDate()))
			dto.setEndDate(ServicesUtil.convertFromDateToString(entity.getEndDate()));
		if (!ServicesUtil.isEmpty(entity.getStartDate()))
			dto.setStartDate(ServicesUtil.convertFromDateToString(entity.getStartDate()));
		if (!ServicesUtil.isEmpty(entity.getUpdatedAt()))
			dto.setUpdateAt(ServicesUtil.convertFromDateToString(entity.getUpdatedAt()));
		if (!ServicesUtil.isEmpty(entity.isActive()))
			dto.setActive(entity.isActive());
		if (!ServicesUtil.isEmpty(entity.isEnabled()))
			dto.setEnabled(entity.isEnabled());

		return dto;
	}

	public String substitutionProcess(String substitutingUser) {
		String response = PMCConstant.FAILURE;
		try {
			List<String> substitutedUsers = getSubstitutedOfSubstituting(substitutingUser);
			if (!ServicesUtil.isEmpty(substitutedUsers)) {
				for (String substitutedUser : substitutedUsers) {
					createProcess(substitutedUser, substitutedUser, substitutingUser, substitutingUser,
							PMCConstant.NEW_TASK);
				}
			}
			response = PMCConstant.SUCCESS;
		} catch (Exception e) {
			System.err.println("[Workbox][SubstitutionRuleDao][substitutionProcess][error]" + e.getLocalizedMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	public List<String> getNewTaskList(String substitutedUsers) {
		try {
			String queryString = " SELECT DISTINCT TE.EVENT_ID FROM TASK_EVENTS TE JOIN task_owners TO ON TE.EVENT_ID=TO.EVENT_ID , SUBSTITUTION_RULE SR  WHERE upper(TO.TASK_OWner) =UPPER('"
					+ substitutedUsers + "') AND TE.CREATED_AT > SR.UPDATED_AT";
			Query query = this.getSession().createSQLQuery(queryString);
			return query.list();
		} catch (Exception e) {
			System.err.println("[Workbox][SubstitutionRuleDao][getNewTaskList][Error]" + e.getLocalizedMessage());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<String> getSubstitutedOfSubstituting(String substitutingUser) {
		try {
			String queryString = " SELECT DISTINCT SUBSTITUTED_USER FROM SUBSTITUTION_RULE WHERE upper(substituting_User)=upper('"
					+ substitutingUser + "') ";
			Query query = this.getSession().createSQLQuery(queryString);
			return query.list();
		} catch (Exception e) {
			System.err.println(
					"[Workbox][SubstitutionRuleDao][getSubstitutedOfSubstituting][Error]" + e.getLocalizedMessage());
		}
		return null;
	}

	public static Boolean getIsEnabled(String startTime, String type) {

		Boolean result = false;
		if (PMCConstant.SUBSTITUTION_SCHEDULER_TYPE.equals(type)) {
			result = (ServicesUtil.convertFromStringToDateSubstitution(ServicesUtil.getIstTimeTwoMinuteBefore())
					.before(ServicesUtil.convertFromStringToDateSubstitution(startTime)))
					&& (ServicesUtil.convertFromStringToDateSubstitution(ServicesUtil.getIstTimeTwoMinuteAfter())
							.after(ServicesUtil.convertFromStringToDateSubstitution(startTime)));
		} else if (PMCConstant.SUBSTITUTION_INSTANT_TYPE.equals(type)) {
			result = ServicesUtil.convertFromStringToDateSubstitution(ServicesUtil.getIstTime())
					.after(ServicesUtil.convertFromStringToDateSubstitution(startTime));
		} else
			result = false;
		return result;
	}

	public Boolean getIsDisabled(String endTime) {

		if (ServicesUtil.convertFromStringToDateSubstitution(ServicesUtil.getIstTimeTwoMinuteBefore())
				.before(ServicesUtil.convertFromStringToDateSubstitution(endTime))
				&& ServicesUtil.convertFromStringToDateSubstitution(endTime).before(
						ServicesUtil.convertFromStringToDateSubstitution(ServicesUtil.getIstTimeTwoMinuteAfter()))) {
			return true;
		} else
			return false;
	}

	public String createRule(SubstitutionRulesDto dto) {
		String response = PMCConstant.FAILURE;
		try {

			if (getIsEnabled(dto.getStartDate(), PMCConstant.SUBSTITUTION_INSTANT_TYPE) && dto.isActive()) {
				dto.setEnabled(true);
				createProcess(dto.getSubstitutedUser(), dto.getSubstitutedUserName(), dto.getSubstitutingUser(),
						dto.getSubstitutingUserName(), PMCConstant.ALL_TASK);
			}
			dto.setUpdateAt(ServicesUtil.getIstTime());
			response = createSubstitution(dto);
		} catch (Exception e) {
			System.err.println("[Workbox][SubstitutionRuleDao][createRule][error]" + e.getLocalizedMessage());
		}
		return response;

	}

	public String createProcess(String substitutedUser, String substitutedUserDisp, String substitutingUser,
			String substitutingUserDisp, String type) {
		String response = PMCConstant.FAILURE;
		try {
			List<TaskOwnersDto> ownersDtos = new ArrayList<TaskOwnersDto>();
			TaskOwnersDto ownersDto = null;
			List<UserTaskMappingDto> mappingDtos = new ArrayList<UserTaskMappingDto>();
			UserTaskMappingDto mappingDto = null;
			List<String> taskIds = null;
			if (!PMCConstant.NEW_TASK.equals(type)) {
				taskIds = taskOwnerDao.getSubstitutedUserTask(substitutedUser, substitutingUser);
			} else {
				taskIds = getNewTaskList(substitutedUser);
			}
			// System.err.println("[SubstitutionRuleDao][tasks to be created
			// with task_owner]" + taskIds);
			if (!ServicesUtil.isEmpty(taskIds)) {
				for (String taskId : taskIds) {
					ownersDto = new TaskOwnersDto();
					ownersDto.setEventId(taskId);
					ownersDto.setIsProcessed(false);
					ownersDto.setIsSubstituted(true);
					ownersDto.setTaskOwner(substitutingUser);
					ownersDto.setTaskOwnerDisplayName(substitutingUserDisp);
					ownersDtos.add(ownersDto);

					mappingDto = new UserTaskMappingDto();
					mappingDto.setSubstitutedUser(substitutedUser);
					mappingDto.setSubstitutingUser(substitutingUser);
					mappingDto.setTaskId(taskId);
					mappingDtos.add(mappingDto);
				}
				System.err.println("Creating records Started mappingDtos : " + mappingDtos);
				System.err.println("Creating records Started ownersDtos : " + ownersDtos);
				userTaskDao.saveOrUpdateTasks(mappingDtos);
				taskOwnerDao.saveOrUpdateOwners(ownersDtos);
				System.err.println(
						"ENDED [ownerDtos]" + ownersDtos.toString() + ",[mappingDtos]" + mappingDtos.toString());

				System.err.println("done");
			}
			response = PMCConstant.SUCCESS;
		} catch (Exception e) {
			System.err.println("[Workbox][SubstitutionRuleDao][createProcess][Error]" + e.getLocalizedMessage());
		}
		return response;
	}

	public String createSubstitution(SubstitutionRulesDto dto) {
		String response = PMCConstant.FAILURE;
		try {
			dto.setUpdateAt(ServicesUtil.getIstTime());
			this.getSession().save(importDto(dto));
			response = PMCConstant.SUCCESS;
		} catch (Exception e) {
			System.err.println("[PMC][SubstitutionRuleFacade][SubstitutionRuleDao][createSubstitution][error]"
					+ e.getLocalizedMessage());
			System.err.println(
					"[PMC][SubstitutionRuleFacade][SubstitutionRuleDao][createSubstitution][error]" + e.getMessage());
		}
		return response;
	}

	public String deleteRule(SubstitutionRulesDto dto) {
		String response = PMCConstant.FAILURE;
		try {
			deleteProcess(dto.getSubstitutedUser(), dto.getSubstitutingUser());
			deleteSubstitution(dto);
			response = PMCConstant.SUCCESS;
		} catch (Exception e) {
			System.err.println(
					"[PMC][SubstitutionRuleFacade][SubstitutionRuleDao][deleteRule][error]" + e.getLocalizedMessage());
		}
		return response;
	}

	public String deleteProcess(String substitutedUser, String substitutingUser) {
		String response = PMCConstant.FAILURE;
		try {
			List<String> taskIds = userTaskDao.getTaskOfSubstitutingUser(substitutedUser, substitutingUser);
			taskOwnerDao.deleteSubstitutionOwner(substitutingUser, taskIds);
			userTaskDao.deleteDisabledSubstitution(substitutedUser, substitutingUser);
			response = PMCConstant.SUCCESS;
		} catch (Exception e) {
			System.err.println("[PMC][SubstitutionRuleFacade][SubstitutionRuleDao][deleteSubstitution][error]"
					+ e.getLocalizedMessage());
		}
		return response;

	}

	public String deleteSubstitution(SubstitutionRulesDto dto) {
		String response = PMCConstant.FAILURE;
		try {
			this.getSession().delete(importDto(dto));
			response = PMCConstant.SUCCESS;
		} catch (Exception e) {
			System.err.println("[PMC][SubstitutionRuleFacade][SubstitutionRuleDao][deleteSubstitution][error]"
					+ e.getLocalizedMessage());
		}
		return response;
	}

	public String updateRule(SubstitutionRulesDto dto, String type) {
		String response = PMCConstant.FAILURE;
		try {
			updateProcess(dto, type);
			updateSubstitution(dto);
			response = PMCConstant.SUCCESS;
		} catch (Exception e) {
			System.err.println("[PMC][SubstitutionRuleDao][updateRule][error]" + e.getMessage());
		}
		return response;
	}

	public String updateProcess(SubstitutionRulesDto dto, String type) {
		String response = PMCConstant.FAILURE;
		try {
			if (getIsEnabled(dto.getStartDate(), type) && dto.isActive()) {
				createProcess(dto.getSubstitutedUser(), dto.getSubstitutedUserName(), dto.getSubstitutingUser(),
						dto.getSubstitutingUserName(), PMCConstant.ALL_TASK);
			} else if ((!dto.isActive() && getIsEnabled(dto.getStartDate(), type))
					|| (dto.isActive() && getIsDisabled(dto.getEndDate()))) {
				deleteProcess(dto.getSubstitutedUser(), dto.getSubstitutingUser());
			}
			response = PMCConstant.SUCCESS;
		} catch (Exception e) {
			System.err.println("[PMC][SubstitutionRuleDao][updateProcess][error]" + e.getMessage());
		}
		return response;

	}

	public String updateSubstitution(SubstitutionRulesDto dto) {
		String response = PMCConstant.FAILURE;
		try {
			if (getIsEnabled(dto.getStartDate(), PMCConstant.SUBSTITUTION_INSTANT_TYPE))
				dto.setEnabled(true);
			dto.setUpdateAt(ServicesUtil.getIstTime());
			this.getSession().update(importDto(dto));
			response = PMCConstant.SUCCESS;
		} catch (Exception e) {
			System.err.println("[PMC][SubstitutionRuleDao][updateSubstitution][error]" + e.getMessage());
		}
		return response;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public List<SubstitutionRulesDto> getSubstitution(String userId, Boolean enable, String userType) {
		List<SubstitutionRulesDto> ruleDtos = new ArrayList<SubstitutionRulesDto>();
		String userQuery = "";
		try {
			/*
			 * UPDATING the substitution Rule Is_enable using current_time
			 */
			String updateEnable = updateSubstitutionRule();
			if (PMCConstant.SUBSTITUTED.equals(userType)) {
				userQuery += " where do.substitutedUser='" + userId + "' ";
			} else {
				userQuery += " where do.substitutingUser='" + userId + "' ";
			}

			String queryString = " select do from SubstitutionRuleDo do" + userQuery;

			Query query = this.getSession().createQuery(queryString);
			List<SubstitutionRuleDo> resultList = query.list();
			if (!ServicesUtil.isEmpty(resultList)) {
				for (SubstitutionRuleDo entity : resultList) {
					ruleDtos.add(exportDto(entity));
				}
			}
		} catch (Exception e) {
			System.err.println("[PMC][SubstitutionRuleFacade][SubstitutionRuleDao][getSubstitution][error]"
					+ e.getLocalizedMessage());
		}
		return ruleDtos;
	}

	public String updateSubstitutionRule() {
		String updateEnableQuery = "update sr set is_enable= (case when (current_timestamp between sr.start_Date and sr.end_date) then 1 else 0 end ) , updated_at='"
				+ ServicesUtil.getIstTime() + "'"
				+ " from substitution_rule sr where sr.is_Active=1 and sr.updated_at < '"
				+ ServicesUtil.getSchedularTimeDifference() + "'";
		int resultRows = this.getSession().createSQLQuery(updateEnableQuery).executeUpdate();

		if (resultRows > 0)
			return PMCConstant.SUCCESS;

		else
			return PMCConstant.FAILURE;
	}

	@SuppressWarnings("unchecked")
	public List<SubstitutionRulesDto> getChangedRule() {
		List<SubstitutionRulesDto> ruleDtos = new ArrayList<SubstitutionRulesDto>();
		try {
			String queryString = " select do from SubstitutionRuleDo do where (do.startDate = '"
					+ ServicesUtil.getIstTimeWithoutT() + "' or do.endDate='" + ServicesUtil.getIstTimeWithoutT()
					+ "') and do.isActive=1 ";
			System.err.println("[PMC][SubstitutuionRuleDao][getChangedRule]" + queryString);
			Query query = this.getSession().createQuery(queryString);
			List<SubstitutionRuleDo> resultList = query.list();
			if (!ServicesUtil.isEmpty(resultList)) {
				for (SubstitutionRuleDo entity : resultList) {
					ruleDtos.add(exportDto(entity));
				}
			}
		} catch (Exception e) {
			System.err.println("[PMC][SubstitutionRuleFacade][SubstitutionRuleDao][getEnabledRule][error]"
					+ e.getLocalizedMessage());
		}
		return ruleDtos;
	}

}
