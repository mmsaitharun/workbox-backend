package oneapp.workbox.services.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oneapp.workbox.services.dao.CustomAttributeDao;
import oneapp.workbox.services.dto.CustomAttributeTemplateResponse;
import oneapp.workbox.services.dto.CustomDetailDto;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.RunningSearchResponseDto;
import oneapp.workbox.services.dto.SingleValueDto;
import oneapp.workbox.services.entity.CustomAttributeTemplate;
import oneapp.workbox.services.entity.CustomAttributeValue;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;

@Service("CustomAttributeFacade")
public class CustomAttributeFacade implements CustomAttributeFacadeLocal {

	private static final Logger logger = LoggerFactory.getLogger(CustomAttributeFacade.class);

	@Autowired
	CustomAttributeDao customAttributeDao;

	@Override
	public ResponseMessage addCustomAttributeTemp(List<CustomAttributeTemplate> customAttributeTemplates) {
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setStatus(PMCConstant.FAILURE);
		responseMessage.setStatusCode(PMCConstant.CODE_FAILURE);
		try {
			customAttributeDao.addCustomAttributeTemp(customAttributeTemplates);
			responseMessage.setStatus(PMCConstant.SUCCESS);
			responseMessage.setStatusCode(PMCConstant.CODE_SUCCESS);
			responseMessage.setMessage("Adding Custom attribute templates successful");
		} catch (Exception ex) {
			responseMessage.setMessage("Exception while setting custom attribute templates"+ex.getMessage());
			logger.error("Exception while setting custom attribute templates : " + ex.getMessage());
		}
		return responseMessage;
	}

	@Override
	public ResponseMessage addCustomAttributeValues(List<CustomAttributeValue> customAttributeValues) {
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setStatus(PMCConstant.FAILURE);
		responseMessage.setStatusCode(PMCConstant.CODE_FAILURE);
		try {
			customAttributeDao.addCustomAttributeValue(customAttributeValues);
			responseMessage.setStatus(PMCConstant.SUCCESS);
			responseMessage.setStatusCode(PMCConstant.CODE_SUCCESS);
			responseMessage.setMessage("Adding Custom attribute values successful");
		} catch (Exception ex) {
			responseMessage.setMessage("Exception while setting custom attribute values");
			System.err.println("Exception while setting custom attribute values : " + ex.getMessage());
		}
		return responseMessage;
	}

	@Override
	public ResponseMessage addCustomAttributeValue(CustomAttributeValue customAttributeValue) {
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setStatus(PMCConstant.FAILURE);
		responseMessage.setStatusCode(PMCConstant.CODE_FAILURE);
		try {
			customAttributeDao.addCustomAttributeValue(customAttributeValue);
			responseMessage.setStatus(PMCConstant.SUCCESS);
			responseMessage.setStatusCode(PMCConstant.CODE_SUCCESS);
			responseMessage.setMessage("Adding Custom attribute values successful");
		} catch (Exception ex) {
			responseMessage.setMessage("Exception while setting custom attribute values");
			System.err.println("Exception while setting custom attribute values : " + ex.getMessage());
		}
		return responseMessage;
	}

	@Override
	public CustomAttributeTemplateResponse getCustomAttributeTemplates(String processName, String key, Boolean isActive) {
		CustomAttributeTemplateResponse customAttributeTemplateResponse = null;
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setStatus(PMCConstant.FAILURE);
		responseMessage.setStatusCode(PMCConstant.CODE_FAILURE);
		if (!ServicesUtil.isEmpty(processName)) {
			customAttributeTemplateResponse = new CustomAttributeTemplateResponse();
			try {
				List<CustomAttributeTemplate> attributeTemplates = customAttributeDao
						.getCustomAttributeTemplates(processName, key, isActive);
				customAttributeTemplateResponse.setCustomAttributeTemplates(attributeTemplates);
				responseMessage.setStatus(PMCConstant.SUCCESS);
				responseMessage.setStatusCode(PMCConstant.CODE_SUCCESS);
				if (!ServicesUtil.isEmpty(attributeTemplates) && attributeTemplates.size() > 0)
					responseMessage.setMessage("Fetching Custom attribute templates successful");
				else
					responseMessage.setMessage(PMCConstant.NO_RESULT);
			} catch (Exception ex) {
				responseMessage.setMessage("Exception while fetching custom attribute templates");
				System.err.println("Exception while fetching custom attribute templates : " + ex.getMessage());
			}
			customAttributeTemplateResponse.setResponseMessage(responseMessage);
		}
		return customAttributeTemplateResponse;
	}

	@Override
	public RunningSearchResponseDto getAutoCompleteValues(String processName,String key, String attributeValue) {

		RunningSearchResponseDto responsedto = new RunningSearchResponseDto();

		try {
			List<String> attributeValues = customAttributeDao.getAutoCompleteValues(processName, key, attributeValue);
			if (!ServicesUtil.isEmpty(attributeValues)) {
				List<SingleValueDto> values = new ArrayList<>();
				for (String val : attributeValues) {
					SingleValueDto dto = new SingleValueDto();
					dto.setValue(val);
					values.add(dto);
				}
                responsedto.setValues(values);
				responsedto
						.setMessage(new ResponseMessage(PMCConstant.SUCCESS, PMCConstant.CODE_SUCCESS, "Values Found"));
			} else {
				responsedto.setMessage(
						new ResponseMessage(PMCConstant.FAILURE, PMCConstant.CODE_FAILURE, "No value found"));
			}

		} catch (Exception e) {
			responsedto.setMessage(new ResponseMessage(PMCConstant.FAILURE, PMCConstant.CODE_FAILURE, "Error"));

		}

		return responsedto;
	}
	
	/* Services for Detail Page */
	
	@Override
	public CustomDetailDto getDetail(String processName, String taskId) { 
		return customAttributeDao.getDetail(processName, taskId);
	}

	@Override
	public ResponseMessage postDetail(CustomDetailDto customDetail) {
		return customAttributeDao.postDetail(customDetail);
	}

}
