package oneapp.workbox.services.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oneapp.workbox.services.dao.SubstitutionRuleDao;
import oneapp.workbox.services.dao.TaskOwnersDao;
import oneapp.workbox.services.dao.UserTaskMappingDao;
import oneapp.workbox.services.dto.RequestDto;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.RestResponse;
import oneapp.workbox.services.dto.SubstitutionRuleResponseDto;
import oneapp.workbox.services.dto.SubstitutionRulesDto;
import oneapp.workbox.services.dto.UserTaskMappingDto;
import oneapp.workbox.services.util.OAuth;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.RestUtil;
import oneapp.workbox.services.util.ServicesUtil;
import oneapp.workbox.services.util.UserManagementUtil;

@Service("SubstitutionRuleFacade")
public class SubstitutionRuleFacade implements SubstitutionRuleFacadeLocal {

	private static final Logger logger = LoggerFactory.getLogger(SubstitutionRuleFacade.class);

	@Autowired
	OAuth oAuth;

	String[] tokens = null;

	@PostConstruct
	public void executeAfterConstructor() {
		tokens = oAuth.getToken();
	}

	@Autowired
	private SubstitutionRuleDao substitutionDaoNew;

	@Autowired
	private UserTaskMappingDao userTaskMappingDao;

	@Autowired
	private TaskOwnersDao taskOwnersDao;

	SubstitutionRuleResponseDto substitutionResponseDto;

	ResponseMessage responseDto;

	@Override
	public ResponseMessage createSubstitutionRule(SubstitutionRulesDto dto) {

		responseDto = new ResponseMessage();
		responseDto.setStatus(PMCConstant.STATUS_FAILURE);
		responseDto.setStatusCode(PMCConstant.CODE_FAILURE);
		try {
			String result = substitutionDaoNew.createRule(dto);
			if (result.equals(PMCConstant.SUCCESS)) {
				responseDto.setMessage("Substitution rule " + PMCConstant.CREATED_SUCCESS);
				responseDto.setStatus(PMCConstant.STATUS_SUCCESS);
				responseDto.setStatusCode(PMCConstant.CODE_SUCCESS);
			} else
				responseDto.setMessage("Substitution rule " + PMCConstant.CREATE_FAILURE);
		} catch (Exception e) {
			System.err.println(
					"[PMC][SubstitutionRuleFacde][create][error]" + e.getMessage() + "," + e.getLocalizedMessage());
			e.printStackTrace();
		}
		return responseDto;
	}

	@Override
	public ResponseMessage deleteSubstitutionRule(SubstitutionRulesDto dto) {
		responseDto = new ResponseMessage();
		responseDto.setStatus(PMCConstant.STATUS_FAILURE);
		responseDto.setStatusCode(PMCConstant.CODE_FAILURE);
		try {
			String result = substitutionDaoNew.deleteRule(dto);
			if (result.equals(PMCConstant.SUCCESS)) {
				responseDto.setMessage("Substitution Rule " + PMCConstant.DELETE_SUCCESS);
				responseDto.setStatus(PMCConstant.STATUS_SUCCESS);
				responseDto.setStatusCode(PMCConstant.CODE_SUCCESS);
			} else {
				responseDto.setMessage("Substitution Rule " + PMCConstant.DELETE_FAILURE);
			}
		} catch (Exception e) {
			System.err.println("[PMC][SubstitutionRuleFacde][delete][error]" + e.getMessage());
		}
		return responseDto;

	}

	@Override
	public ResponseMessage updateSubstitutionRule(SubstitutionRulesDto dto) {
		responseDto = new ResponseMessage();
		responseDto.setStatus(PMCConstant.STATUS_FAILURE);
		responseDto.setStatusCode(PMCConstant.CODE_FAILURE);
		try {
			String result = substitutionDaoNew.updateRule(dto,PMCConstant.SUBSTITUTION_INSTANT_TYPE);
			if (result.equals(PMCConstant.SUCCESS)) {
				responseDto.setMessage("Substitution rule " + PMCConstant.UPDATE_SUCCESS);
				responseDto.setStatus(PMCConstant.STATUS_SUCCESS);
				responseDto.setStatusCode(PMCConstant.CODE_SUCCESS);
			} else
				responseDto.setMessage("Substitution rule " + PMCConstant.UPDATE_FAILURE);
		} catch (Exception e) {
			System.err.println("[PMC][SubstitutionRuleFacde][create][error]" + e.getMessage());
			e.printStackTrace();
		}
		return responseDto;

	}

	@Override
	public SubstitutionRuleResponseDto getSubstituting() {
		SubstitutionRuleResponseDto ruleResponseDto = new SubstitutionRuleResponseDto();
		responseDto = new ResponseMessage();
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		try {
			String loggedInUser = UserManagementUtil.getLoggedInUser().getName();
			List<SubstitutionRulesDto> dtoList = substitutionDaoNew.getSubstitution(loggedInUser, PMCConstant.isEnabled,
					PMCConstant.SUBSTITUTING);

			if (!ServicesUtil.isEmpty(dtoList)) {
				ruleResponseDto.setDtoList(dtoList);
				responseDto.setMessage("Data fetched Successfully");
			} else {
				responseDto.setMessage(PMCConstant.NO_RESULT);
			}
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		} catch (Exception e) {
			logger.error("[PMC][SubstitutionRuleFacade][getUserSubstitution][error]" + e.getMessage());
			responseDto.setMessage("Fetching data failed due to " + e.getMessage());
		}
		ruleResponseDto.setMessage(responseDto);
		return ruleResponseDto;
	}

	@Override
	public SubstitutionRuleResponseDto getSubstituted() {
		SubstitutionRuleResponseDto ruleResponseDto = new SubstitutionRuleResponseDto();
		responseDto = new ResponseMessage();
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		try {
			String loggedInUser = UserManagementUtil.getLoggedInUser().getName();

			List<SubstitutionRulesDto> dtoList = substitutionDaoNew.getSubstitution(loggedInUser, PMCConstant.isEnabled,
					PMCConstant.SUBSTITUTED);

			if (!ServicesUtil.isEmpty(dtoList)) {
				ruleResponseDto.setDtoList(dtoList);
				responseDto.setMessage("Data fetched Successfully");
			} else {
				responseDto.setMessage(PMCConstant.NO_RESULT);
			}
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		} catch (Exception e) {
			logger.error("[PMC][SubstitutionRuleFacade][getUserAsSubstitute][error]" + e.getMessage());
			responseDto.setMessage("Fetching data failed due to " + e.getMessage());
		}
		ruleResponseDto.setMessage(responseDto);
		return ruleResponseDto;
	}

	@Override
	public ResponseMessage forwardTask(String taskId, String processor) {
		responseDto = new ResponseMessage();
		responseDto.setStatus(PMCConstant.STATUS_FAILURE);
		responseDto.setStatusCode(PMCConstant.CODE_FAILURE);
		try {

			if (updateRecipientUserInWorkflow(taskId, getPayloadForForward(processor, taskId))
					.equals(PMCConstant.SUCCESS)) {
				responseDto.setMessage("Task forwarded Successfully");
				responseDto.setStatus(PMCConstant.STATUS_SUCCESS);
				responseDto.setStatusCode(PMCConstant.CODE_SUCCESS);
			} else
				responseDto.setMessage("Task Forwarding Failed");
		} catch (Exception e) {
			logger.error("[PMC][SubstitutionRuleFacade][iterateTaskCollection]" + e.getMessage() + " ," + taskId);
		}
		return responseDto;
	}

	@Override
	public ResponseMessage bulkForward(RequestDto dto) {
		responseDto = new ResponseMessage();
		responseDto.setStatus(PMCConstant.STATUS_FAILURE);
		responseDto.setStatusCode(PMCConstant.CODE_FAILURE);
		try {
			String result = "";
			String unForwaredTask = "";
			String forwardedTask = "";
			for (String task : dto.getIds()) {
				result = updateRecipientUserInWorkflow(task, getPayloadForForward(dto.getStrName(), task));
				if (result.equals(PMCConstant.SUCCESS)) {
					forwardedTask += "'" + task + "',";
				} else {
					unForwaredTask += "'" + task + "',";
				}
			}
			if (ServicesUtil.isEmpty(unForwaredTask)) {
				responseDto.setMessage("Task forwarded Successfully");
				responseDto.setStatus(PMCConstant.STATUS_SUCCESS);
				responseDto.setStatusCode(PMCConstant.CODE_SUCCESS);
				System.err.println("[PMC][SubstitutionRuleFacade][iterateTaskCollection][failed for ]" + forwardedTask);

			} else {
				responseDto.setMessage("Task Forwarding Failed ");
				System.err.println("[PMC][SubstitutionRuleFacade][BulkForward][failed for ]" + unForwaredTask);
			}
		} catch (Exception e) {
			System.err.println(
					"[PMC][SubstitutionRuleFacade][BulkForward][failed for ]" + e.getMessage() + " ," + dto.getIds());
		}
		return responseDto;
	}
	
	public List<String> getRecipientUser(String taskId, String type, JSONObject resource) {
		List<String> resultList = new ArrayList<String>();
		JSONObject resources = resource;
		try {
			if (type.equals(PMCConstant.FORWARD_TASK)) {
				String requestUrl = PMCConstant.REQUEST_URL_INST + "task-instances/" + taskId;
				RestResponse responseObject = RestUtil.callRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TI, null,
						"GET", "application/json", false, null, null, null, null, tokens[0], tokens[1]);
				if(!ServicesUtil.isEmpty(responseObject)) {
					resources = (JSONObject) responseObject.getResponseObject();
				}
			}
			JSONArray jsonArray = resources.getJSONArray("recipientUsers");
			for (int i = 0; i < jsonArray.length(); i++)
				resultList.add(jsonArray.optString(i));
			String currentProcessor = resource.optString("processor");

			if (!ServicesUtil.isEmpty(currentProcessor))
				resultList.add(currentProcessor);
		} catch (Exception e) {
			logger.error("[PMC][SubstitutionRuleFacade][getRecipientUser][error]" + e.getMessage());
		}
		return resultList;

	}

	public String getPayloadForSubstitution(List<String> recipientUser) {
		String listToString = "";
		if (!ServicesUtil.isEmpty(recipientUser) && recipientUser.size() > 0) {
			listToString = "{ \"recipientUsers\":";
			listToString += "\"";
			for (String str : recipientUser) {
				listToString += str + ",";
			}
			listToString = listToString.substring(0, listToString.length() - 1);
			listToString += "\"";
			listToString += ",\"processor\":\"\"}";
		} else if (ServicesUtil.isEmpty(recipientUser)) // for RU of group
			listToString = "{ \"recipientUsers\":\"\"}";
		return listToString;
	}

	public String getPayloadForForward(String processor, String taskId) {
		String listToString = "{ \"processor\":";
		if (!ServicesUtil.isEmpty(processor)) {
			listToString += "\"";
			listToString += processor;
		}
		listToString += "\"";
		List<String> recipientUser = getRecipientUser(taskId, PMCConstant.FORWARD_TASK, null);
		if(!ServicesUtil.isEmpty(processor) && recipientUser.size() > 0 && !ServicesUtil.isEmpty(recipientUser)) {
			recipientUser.add(processor);
		} else if (ServicesUtil.isEmpty(recipientUser) && !ServicesUtil.isEmpty(processor)){
			recipientUser = new ArrayList<>();
			recipientUser.add(processor);
		}
		if (!ServicesUtil.isEmpty(recipientUser) && recipientUser.size() > 0) {
			listToString += " ,\"recipientUsers\":";
			listToString += "\"";
			for (String str : recipientUser) {
				listToString += str + ",";
			}
			listToString = listToString.substring(0, listToString.length() - 1);
			listToString += "\"";
		}
		listToString += "}";
		return listToString;
	}

	public String updateRecipientUserInWorkflow(String taskId, String payload) {
		try {
			String requestUrl = PMCConstant.REQUEST_URL_INST + "task-instances/" + taskId;

			RestResponse restResponse = RestUtil.callRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TI, payload,
					"PATCH", "application/json", false, "Fetch", null, null, null, tokens[0], tokens[1]);
			if (!ServicesUtil.isEmpty(restResponse) && (restResponse.getResponseCode() >= 200)
					&& (restResponse.getResponseCode() <= 207)) {
				taskOwnersDao.deleteUser(taskId, null);
			}
		} catch (Exception e) {
			System.err.println("[PMC][SubstitutionRuleFacade][updateRecipientUserInWorkflow][error]" + e.getMessage());
			e.printStackTrace();
			logger.error("[PMC][SubstitutionRuleFacade][updateRecipientUserInWorkflow][error]" + e.getMessage());
		}
		return PMCConstant.SUCCESS;

	}

//	@Override
//	public String updateTaskList() {
//		System.err.println("[PMC][UpdateTaskList]called");
//		String response = PMCConstant.FAILURE;
//		String taskId = "";
//		List<String> recipientUsers = new ArrayList<String>();
//
//		List<SubstitutionRulesDto> dtos = substitutionDao.getSubstitutionResultDto();
//
//		String requestUrl = PMCConstant.REQUEST_URL_INST + "task-instances?status=READY&status=RESERVED";
//		ExternalService externalService = new ExternalService();
//		JSONArray jsonArray = externalService.getTaskList(requestUrl, tokens[0], tokens[1]);
//		JSONObject resource = null;
//		try {
//			if (!ServicesUtil.isEmpty(jsonArray) && !ServicesUtil.isEmpty(dtos)) {
//				List<String> groupUsers = null;
//				for (Object obj : jsonArray) {
//					resource = (JSONObject) obj;
//					taskId = resource.getString("id");
//					recipientUsers = getRecipientUser(taskId, PMCConstant.SUBSTITUTING, resource);
//					String groupName = resource.getJSONArray("recipientGroups").optString(0);
//
//					if (!ServicesUtil.isEmpty(groupName) && PMCConstant.GROUP_NAME.equals(groupName)
//							&& ServicesUtil.isEmpty(groupUsers)) {
//						groupUsers = getRecipientUserOfGroup();
//					}
//					if (!ServicesUtil.isEmpty(groupUsers) && !ServicesUtil.isEmpty(groupName)) {
//						recipientUsers.addAll(groupUsers);
//					}
//
//					if (!ServicesUtil.isEmpty(recipientUsers))
//						for (SubstitutionRulesDto dto : dtos) {
//							String substitutedUser = dto.getSubstitutedUser(),
//									substitutingUser = dto.getSubstitutingUser();
//
//							if (recipientUsers.contains(substitutedUser) && recipientUsers.contains(substitutingUser)) {
//
//								recipientUsers.remove(substitutingUser);
//								taskOwnersDao.deleteUser(taskId, substitutingUser);
//								userTaskMappingDao.deleteDisabledSubstitution(substitutedUser, substitutingUser);
//
//							} else if (recipientUsers.contains(substitutedUser)) {
//								recipientUsers.add(substitutingUser);
//
//								userTaskMappingDao.createUserTask(taskId, substitutedUser, substitutingUser);
//							}
//						}
//					if (PMCConstant.GROUP_NAME.equals(groupName) && !ServicesUtil.isEmpty(groupUsers)) {
//						recipientUsers.removeAll(groupUsers);
//
//					}
//					response = updateRecipientUserInWorkflow(taskId, getPayloadForSubstitution(recipientUsers));
//				}
//			}
//		} catch (Exception e) {
//			System.err.println("[substitution]error" + e.getMessage() + "[]," + e.getLocalizedMessage());
//		}
//		return response;
//	}

	// Update in workflow all recipient user as P000035 to test substitution
//	@Override
//	public String updateTaskListInFlow() {
//		System.err.println("[PMC][UpdateTaskList]called");
//		String response = PMCConstant.FAILURE;
//
//		String requestUrl = PMCConstant.REQUEST_URL_INST + "task-instances?status=READY&status=RESERVED";
//		ExternalService externalService = new ExternalService();
//		JSONArray jsonArray = externalService.getTaskList(requestUrl, tokens[0], tokens[1]);
//		JSONObject resource = null;
//		List<String> recipientUsers = new ArrayList<String>();
//		recipientUsers.add("P000035");
//		if (!ServicesUtil.isEmpty(jsonArray)) {
//			for (Object obj : jsonArray) {
//				resource = (JSONObject) obj;
//
//				response = updateRecipientUserInWorkflow(resource.getString("id"),
//						getPayloadForSubstitution(recipientUsers));
//			}
//		}
//		return response;
//	}

	public ResponseMessage releaseTask(String taskId) {

		responseDto = new ResponseMessage();
		responseDto.setStatus(PMCConstant.STATUS_FAILURE);
		responseDto.setStatusCode(PMCConstant.CODE_FAILURE);

		try {
			List<UserTaskMappingDto> dtos = userTaskMappingDao.getSubstitutedUserDisabled(taskId);
			List<String> recipientUsers = new ArrayList<String>();
			for (UserTaskMappingDto dto : dtos) {
				recipientUsers.add(dto.getSubstitutedUser());
				userTaskMappingDao.deleteUserTask(dto);
				taskOwnersDao.deleteUser(taskId, dto.getSubstitutingUser());
			}
			if (updateRecipientUserInWorkflow(taskId, getPayloadForSubstitution(recipientUsers))
					.equals(PMCConstant.SUCCESS)) {
				responseDto.setMessage("Release Task " + PMCConstant.SUCCESS);
				responseDto.setStatus(PMCConstant.STATUS_SUCCESS);
				responseDto.setStatusCode(PMCConstant.CODE_SUCCESS);
			} else
				responseDto.setMessage("Release Task " + PMCConstant.FAILURE);
		} catch (Exception e) {
			logger.error("[PMC][SubstitutionRuleFacade][iterateTaskCollection]" + e.getMessage() + " ," + taskId);
		}
		return responseDto;

	}

//	public String iterateTaskCollection() {
//
//		String response = PMCConstant.FAILURE;
//		substitutionDao.updateSubstitutionRule();
//		String taskId = "";
//		List<String> recipientUsers = new ArrayList<String>();
//		Map<String, String> newSubstitutionMap = substitutionDao.getSubstitutionResult();
//		Map<String, UserTaskMappingDto> disableSubstitutionMap = null;
//		String requestUrl = PMCConstant.REQUEST_URL_INST + "task-instances?status=READY&status=RESERVED";
//		Object responseObject = SCPRestUtil.callRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TC, null, "GET",
//				"application/json", false, null, null, null, tokens[0], tokens[1]).getResponseObject();
//		JSONArray jsonArray = ServicesUtil.isEmpty(responseObject) ? null : (JSONArray) responseObject;
//		JSONObject resource = null;
//
//		if (!ServicesUtil.isEmpty(jsonArray)) {
//			List<String> groupUsers = null;
//			for (Object obj : jsonArray) {
//				resource = (JSONObject) obj;
//				taskId = resource.getString("id");
//				recipientUsers = getRecipientUser(taskId, PMCConstant.SUBSTITUTING, resource);
//				String groupName = resource.getJSONArray("recipientGroups").optString(0);
//
//				if (!ServicesUtil.isEmpty(groupName) && PMCConstant.GROUP_NAME.equals(groupName)
//						&& ServicesUtil.isEmpty(groupUsers)) {
//					groupUsers = getRecipientUserOfGroup();
//				}
//
//				if (!ServicesUtil.isEmpty(groupUsers))
//					recipientUsers.addAll(groupUsers);
//				if (!ServicesUtil.isEmpty(recipientUsers))
//					try {
//						disableSubstitutionMap = userTaskMappingDao.getDisabledSubstitutionResult(taskId);
//						for (String str : recipientUsers) {
//							if (newSubstitutionMap.keySet().contains(str)
//									&& userTaskMappingDao.recordExistsOrNot(taskId, str, newSubstitutionMap.get(str))
//											.equals(PMCConstant.FAILURE)) {
//								userTaskMappingDao.createUserTask(taskId, str, newSubstitutionMap.get(str));
//								recipientUsers.add(newSubstitutionMap.get(str));
//							}
//							if (disableSubstitutionMap.keySet().contains(str)) {
//								userTaskMappingDao.deleteUserTask(disableSubstitutionMap.get(str));
//								taskOwnersDao.deleteUser(taskId, str);
//								recipientUsers.remove(str);
//							}
//						}
//					} catch (Exception e) {
//						logger.error("[PMC][SubstitutionRuleFacade][iterateTaskCollection]" + e.getMessage() + " ,"
//								+ taskId);
//					}
//				if (!ServicesUtil.isEmpty(groupName) && PMCConstant.GROUP_NAME.equals(groupName)
//						&& !ServicesUtil.isEmpty(groupUsers)) {
//					recipientUsers.removeAll(groupUsers);
//				}
//				response = updateRecipientUserInWorkflow(taskId, getPayloadForSubstitution(recipientUsers));
//			}
//		}
//		return response;
//	}

//	public String substitution() {
//
//		String response = PMCConstant.FAILURE;
//		substitutionDao.updateSubstitutionRule();
//		String taskId = "";
//		List<String> recipientUsers = new ArrayList<String>();
//		Map<String, String> newSubstitutionMap = substitutionDao.getSubstitutionResult();
//		Map<String, UserTaskMappingDto> disableSubstitutionMap = null;
//		String requestUrl = PMCConstant.REQUEST_URL_INST + "task-instances?status=READY&status=RESERVED";
//		ExternalService externalService = new ExternalService();
//		JSONArray jsonArray = externalService.getTaskList(requestUrl, tokens[0], tokens[1]);
//		JSONObject resource = null;
//
//		if (!ServicesUtil.isEmpty(jsonArray)) {
//			List<String> groupUsers = null;
//			for (Object obj : jsonArray) {
//				resource = (JSONObject) obj;
//				taskId = resource.getString("id");
//				recipientUsers = getRecipientUser(taskId, PMCConstant.SUBSTITUTING, resource);
//				String groupName = resource.getJSONArray("recipientGroups").optString(0);
//
//				if (!ServicesUtil.isEmpty(groupName) && PMCConstant.GROUP_NAME.equals(groupName)
//						&& ServicesUtil.isEmpty(groupUsers)) {
//					groupUsers = getRecipientUserOfGroup();
//				}
//
//				if (!ServicesUtil.isEmpty(groupUsers))
//					recipientUsers.addAll(groupUsers);
//				if (!ServicesUtil.isEmpty(recipientUsers))
//					try {
//						disableSubstitutionMap = userTaskMappingDao.getDisabledSubstitutionResult(taskId);
//						for (String str : recipientUsers) {
//							if (newSubstitutionMap.keySet().contains(str)
//									&& userTaskMappingDao.recordExistsOrNot(taskId, str, newSubstitutionMap.get(str))
//											.equals(PMCConstant.FAILURE)) {
//								userTaskMappingDao.createUserTask(taskId, str, newSubstitutionMap.get(str));
//								recipientUsers.add(newSubstitutionMap.get(str));
//							}
//							if (disableSubstitutionMap.keySet().contains(str)) {
//								userTaskMappingDao.deleteUserTask(disableSubstitutionMap.get(str));
//								System.err.println("[agco]" + str + taskId);
//								taskOwnersDao.deleteUser(taskId, str);
//								recipientUsers.remove(str);
//							}
//						}
//					} catch (Exception e) {
//						logger.error("[PMC][SubstitutionRuleFacade][iterateTaskCollection]" + e.getMessage() + " ,"
//								+ taskId);
//					}
//				if (!ServicesUtil.isEmpty(groupName) && PMCConstant.GROUP_NAME.equals(groupName)
//						&& !ServicesUtil.isEmpty(groupUsers)) {
//					recipientUsers.removeAll(groupUsers);
//				}
//				response = updateRecipientUserInWorkflow(taskId, getPayloadForSubstitution(recipientUsers));
//			}
//		}
//		return response;
//	}

	/* Below Methods are not being Used */
	//
	// public String getCurrentProcessor(String taskId) {
	// String processor = "";
	// String requestUrl =
	// "https://bpmworkflowruntimea2d6007ea-jg2kiqgyo3.hana.ondemand.com/workflow-service/rest/v1/task-instances/"
	// + taskId;
	// JSONObject jsonObject = SCPSCPRestUtil.callRestService(requestUrl, null,
	// "GET", "application/json", true);
	// JSONObject resource = (JSONObject) jsonObject;
	// try {
	// processor = resource.getJSONObject("processor").toString();
	// } catch (Exception e) {
	// System.err.println("[PMC][SubstitutionRuleFacade][getRecipientUser][Error]"
	// + e.getLocalizedMessage());
	// logger.error("[PMC][SubstitutionRuleFacade][getRecipientUser][error]" +
	// e.getMessage());
	// }
	// return processor;
	//
	// }
	//
	// public StringListResponseDto getSubstitutingUsersId() {
	// StringListResponseDto dto = new StringListResponseDto();
	// responseDto = new ResponseMessage();
	// responseDto.setStatus("FAILURE");
	// responseDto.setStatusCode("1");
	// try {
	// String loggedInUser = UserManagementUtil.getLoggedInUser().getName();
	//
	// List<String> dtoList = substitutionDao.getUserId(loggedInUser);
	//
	// if (!ServicesUtil.isEmpty(dtoList)) {
	// dto.setStr(dtoList);
	// responseDto.setMessage("Data fetched Successfully");
	// } else {
	// responseDto.setMessage(PMCConstant.NO_RESULT);
	// }
	// responseDto.setStatus("SUCCESS");
	// responseDto.setStatusCode("0");
	// } catch (Exception e) {
	// logger.error("[PMC][SubstitutionRuleFacade][getSubstitutingUserId][error]"
	// + e.getMessage());
	// responseDto.setMessage("Fetching data failed due to " + e.getMessage());
	// }
	// dto.setMessage(responseDto);
	// return dto;
	// }
}
