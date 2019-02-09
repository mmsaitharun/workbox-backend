package oneapp.workbox.services.dao;

import static oneapp.workbox.services.util.ServicesUtil.asBoolean;
import static oneapp.workbox.services.util.ServicesUtil.asInteger;
import static oneapp.workbox.services.util.ServicesUtil.asString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import oneapp.workbox.services.dto.ActionDto;
import oneapp.workbox.services.dto.CustomDetailDto;
import oneapp.workbox.services.dto.DynamicButtonDto;
import oneapp.workbox.services.dto.DynamicDetailDto;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.entity.CustomAttributeTemplate;
import oneapp.workbox.services.entity.CustomAttributeValue;
import oneapp.workbox.services.entity.TaskEventsDo;
import oneapp.workbox.services.service.WorkFlowActionFacadeLocal;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;
import oneapp.workbox.services.util.UserManagementUtil;

@Repository("CustomAttributeDao")
@Transactional
public class CustomAttributeDao {

	private static final Logger logger = LoggerFactory.getLogger(CustomAttributeDao.class);

	@Autowired
	SessionFactory sessionFactory;
	
	@Autowired
	WorkFlowActionFacadeLocal scpActions;
	
	@Autowired
	TaskEventsDao taskEvents;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public void addCustomAttributeTemp(CustomAttributeTemplate customAttributeTemplate) {
		if (!ServicesUtil.isEmpty(customAttributeTemplate)) {

			if (ServicesUtil.isEmpty(customAttributeTemplate.getIsActive()))
				customAttributeTemplate.setIsActive(true);

			logger.error("CustomAttributeDao:addCustomAttributeTemp:" + customAttributeTemplate);
			this.getSession().saveOrUpdate(customAttributeTemplate);
		}
	}

	public void addCustomAttributeTemp(List<CustomAttributeTemplate> customAttributeTemplates) {
		if (!ServicesUtil.isEmpty(customAttributeTemplates) && customAttributeTemplates.size() > 0) {
			for (CustomAttributeTemplate customAttributeTemp : customAttributeTemplates) {

				if (ServicesUtil.isEmpty(customAttributeTemp.getIsActive()))
					customAttributeTemp.setIsActive(true);
				this.getSession().saveOrUpdate(customAttributeTemp);
			}
		}
	}

	public void addCustomAttributeValue(CustomAttributeValue customAttributeValue) {
		if (!ServicesUtil.isEmpty(customAttributeValue)) {
			this.getSession().saveOrUpdate(customAttributeValue);
		}
	}

	public void addCustomAttributeValue(List<CustomAttributeValue> customAttributeValues) {
		if (!ServicesUtil.isEmpty(customAttributeValues) && customAttributeValues.size() > 0) {
			for (CustomAttributeValue customAttributeValue : customAttributeValues) {
				this.getSession().saveOrUpdate(customAttributeValue);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<CustomAttributeValue> getCustomAttributes(String taskId) {
		if (!ServicesUtil.isEmpty(taskId)) {
			Query query = this.getSession().createSQLQuery(
					"select cv.PROCESS_NAME,cv.KEY, cv.ATTR_VALUE, ct.LABEL from CUSTOM_ATTR_VALUES cv, CUSTOM_ATTR_TEMPLATE ct where cv.PROCESS_NAME = ct.PROCESS_NAME and cv.KEY=ct.KEY and cv.TASK_ID = '"
							+ taskId + "'");
			return convertToCVList(query.list());
		}
		return null;
	}

	private List<CustomAttributeValue> convertToCVList(List<Object[]> list) {

		List<CustomAttributeValue> customAttributeValues = null;
		CustomAttributeValue customAttributeValue = null;
		if (!ServicesUtil.isEmpty(list) && list.size() > 0) {
			customAttributeValues = new ArrayList<CustomAttributeValue>();
			for (Object[] object : list) {
				customAttributeValue = new CustomAttributeValue();
				customAttributeValue.setProcessName((String) object[0]);
				customAttributeValue.setKey((String) object[1]);
				customAttributeValue.setAttributeValue((String) object[2]);
				customAttributeValue.setAttributeTemplate((String) object[3]);
				customAttributeValues.add(customAttributeValue);
			}
		}
		return customAttributeValues;
	}

	@SuppressWarnings("unchecked")
	public List<String> getKeysFromTemplate(String processName) {
		Criteria criteria = this.getSession().createCriteria(CustomAttributeTemplate.class);
		if (!ServicesUtil.isEmpty(processName)) {
			criteria.add(Restrictions.eq("processName", processName));
			criteria.setProjection(Projections.property("key"));
			return criteria.list();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<CustomAttributeTemplate> getCustomAttributeTemplates(String processName, String key, Boolean isActive) {
		if (!ServicesUtil.isEmpty(processName)) {
			Criteria criteria = this.getSession().createCriteria(CustomAttributeTemplate.class);
			if(!ServicesUtil.isEmpty(isActive)) {
				criteria.add(Restrictions.eq("isActive", isActive));
			}
			criteria.add(Restrictions.eq("processName", processName));
			if(!ServicesUtil.isEmpty(key))
				criteria.add(Restrictions.eq("key",key));
			criteria.add(Restrictions.ne("dataType", "BUTTON").ignoreCase());
			
			return criteria.list();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<String> getAutoCompleteValues(String processName,String key, String attributeValue) {
		if (ServicesUtil.isEmpty(processName) || ServicesUtil.isEmpty(attributeValue))
			return null;
		Criteria criteria = this.getSession().createCriteria(CustomAttributeValue.class);
		if (!ServicesUtil.isEmpty(processName))
			criteria.add(Restrictions.eq("processName", processName));
		if (!ServicesUtil.isEmpty(key))
			criteria.add(Restrictions.eq("key", key));
		if (!ServicesUtil.isEmpty(attributeValue))
			criteria.add(Restrictions.ilike("attributeValue", attributeValue, MatchMode.ANYWHERE));
		criteria.setProjection(Projections.distinct(Projections.property("attributeValue")));
		criteria.addOrder(Order.asc("attributeValue"));
		return criteria.list();

	}
	
	
	/* Services for Detail Page */
	
	@SuppressWarnings("unchecked")
	public CustomDetailDto getDetail(String processName, String taskId) {
		CustomDetailDto customDetail = new CustomDetailDto();
		List<CustomAttributeTemplate> customAttributes = null;
		List<DynamicButtonDto> dynamicButtons = null;
		String query = "SELECT *, ROW_NUMBER() OVER (PARTITION BY KEY) AS ROW_NUM "
				+" FROM ( "
				+" SELECT CV.PROCESS_NAME, CV.KEY, CT.LABEL, CT.IS_ACTIVE, CT.DATA_TYPE, CT.ATTR_DES, CT.IS_MAND, CT.IS_EDITABLE, CV.ATTR_VALUE "
				+" FROM CUSTOM_ATTR_TEMPLATE CT "
				+" INNER JOIN CUSTOM_ATTR_VALUES CV " 
				+" ON CT.PROCESS_NAME = CV.PROCESS_NAME AND CT.KEY = CV.KEY "
				+" WHERE CT.PROCESS_NAME = '"+processName+"' "
				+" AND CV.TASK_ID = '"+taskId+"' "
				+" ) ORDER BY ROW_NUM, KEY";

		logger.error("Custom Detail Query : "+query);
		
		List<Object[]> resultList = this.getSession().createSQLQuery(query).list();
		if(!ServicesUtil.isEmpty(resultList) && resultList.size() > 0) {
			customAttributes = convertResultToCustomDto(resultList);
		}
		
		String buttonQuery = "SELECT DATA_TYPE, LABEL, KEY, PROCESS_NAME FROM CUSTOM_ATTR_TEMPLATE WHERE UPPER(DATA_TYPE) = UPPER('BUTTON') AND PROCESS_NAME = '"+processName+"'";
		resultList = this.getSession().createSQLQuery(buttonQuery).list();
		if(!ServicesUtil.isEmpty(resultList) && resultList.size() > 0) {
			dynamicButtons = convertResultToButtonDto(resultList);
		}
		customDetail.setDynamicDetails(Arrays.asList(new DynamicDetailDto("Task Details", customAttributes), new DynamicDetailDto("Comment Details", Arrays.asList(new CustomAttributeTemplate("Text Area", "comments", false, true)))));
		customDetail.setDynamicButtons(dynamicButtons);
		customDetail.setResponseMessage(new ResponseMessage(PMCConstant.STATUS_SUCCESS, PMCConstant.CODE_SUCCESS, "Custom Detail Fetch Success"));
		return customDetail;
	}

	private List<DynamicButtonDto> convertResultToButtonDto(List<Object[]> resultList) {
		List<DynamicButtonDto> dynamicButtons = new ArrayList<DynamicButtonDto>();
		DynamicButtonDto button = null;
		for(Object[] row : resultList) {
			button = new DynamicButtonDto();
			button.setButtonText(asString(row[1]));
			button.setButtonKey(asString(row[2]));
			button.setProcessName(asString(row[3]));
			button.setButtonFlag(false);
			dynamicButtons.add(button);
		}
		return dynamicButtons;
	}

	private List<CustomAttributeTemplate> convertResultToCustomDto(List<Object[]> resultList) {
		List<CustomAttributeTemplate>  attributeTemplates = new ArrayList<CustomAttributeTemplate>();
		CustomAttributeTemplate attributeTemplate = null;
		Map<String, CustomAttributeTemplate> customDetails = new HashMap<>();
 		for(Object[] row : resultList) {
			attributeTemplate = new CustomAttributeTemplate();
			attributeTemplate.setProcessName(asString(row[0]));
			attributeTemplate.setKey(asString(row[1]));
			attributeTemplate.setLabel(asString(row[2]));
			attributeTemplate.setIsActive(asBoolean(row[3]));
			attributeTemplate.setDataType(asString(row[4]));
			attributeTemplate.setDescription(asString(row[5]));
			attributeTemplate.setIsMandatory(asBoolean(row[6]));
			attributeTemplate.setIsEditable(asBoolean(row[7]));
			if(asInteger(row[9]) == 1) {
				attributeTemplate.setValue(asString(row[8]));
				attributeTemplate.setAttributeValues(new ArrayList<CustomAttributeValue>(Arrays.asList(new CustomAttributeValue(attributeTemplate.getProcessName(), attributeTemplate.getKey(), attributeTemplate.getValue()))));
				customDetails.put(attributeTemplate.getProcessName()+"||"+attributeTemplate.getKey(), attributeTemplate);
			} else if(asInteger(row[9]) > 1){
				attributeTemplate.setValue(asString(row[8]));
				List<CustomAttributeValue> values = customDetails.get(attributeTemplate.getProcessName()+"||"+attributeTemplate.getKey()).getAttributeValues();
				values.add(new CustomAttributeValue(attributeTemplate.getProcessName(), attributeTemplate.getKey(), attributeTemplate.getValue()));
				attributeTemplate.setAttributeValues(values);
				customDetails.put(attributeTemplate.getProcessName()+"||"+attributeTemplate.getKey(), attributeTemplate);
			}
		}
 		for(Entry<String, CustomAttributeTemplate> entry : customDetails.entrySet()) {
 			attributeTemplates.add(entry.getValue());
 		}
		return attributeTemplates;
	}
	
	public ResponseMessage postDetail(CustomDetailDto customDetail) {
		List<DynamicButtonDto> dynamicButtons = customDetail.getDynamicButtons();
		for(DynamicButtonDto button : dynamicButtons) {
			if(button.getButtonFlag()) {
				performAction(customDetail, button);
				return new ResponseMessage(PMCConstant.SUCCESS, PMCConstant.CODE_SUCCESS, "Action Performed successfully");
			}
		}
		return new ResponseMessage(PMCConstant.FAILURE, PMCConstant.CODE_FAILURE, "Action Not Performed successfully");
	}

	private void performAction(CustomDetailDto customDetail, DynamicButtonDto button) {
		String origin = getOrigin(button.getProcessName());
		String action = getAction(button.getButtonKey());
		String loggedInUser = UserManagementUtil.getLoggedInUser().getName();
		switch (origin) {
		case "SCP":
			ResponseMessage taskActionResponse = scpActions.taskAction(new ActionDto(action, loggedInUser, Arrays.asList(new String(customDetail.getTaskId())), "Completed"));
			if(taskActionResponse.getStatus().equals(PMCConstant.SUCCESS)) {
				taskEvents.changeTaskStatus(new TaskEventsDo(customDetail.getTaskId(), "COMPLETED", loggedInUser));
			}
			break;
		case "SuccessFactors":
			break;
		case "BPM":
			break;
		case "ECC":
			break;
		}
	}

	private String getAction(String buttonKey) {
		switch (buttonKey) {
		case "accept":
		case "sendToNext":
			return "Approve";
		case "reject":
		case "sendToPrevious":
			return "Reject";
		default:
			return "Accept";
		}
	}

	private String getOrigin(String processName) {
		switch (processName) {
		case "testworkflow":
		case "purchaseorderprocess":
		case "leaveapprovalprocess":
			return "SCP";
		case "61":
		case "141":
			return "SuccessFactors";
		default:
			return "SCP";
		}
	}

}
