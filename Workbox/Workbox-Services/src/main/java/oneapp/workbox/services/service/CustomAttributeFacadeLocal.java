package oneapp.workbox.services.service;

import java.util.List;

import oneapp.workbox.services.dto.CustomAttributeTemplateResponse;
import oneapp.workbox.services.dto.CustomDetailDto;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.RunningSearchResponseDto;
import oneapp.workbox.services.entity.CustomAttributeTemplate;
import oneapp.workbox.services.entity.CustomAttributeValue;

public interface CustomAttributeFacadeLocal {

	ResponseMessage addCustomAttributeTemp(List<CustomAttributeTemplate> customAttributeTemplates);

	ResponseMessage addCustomAttributeValues(List<CustomAttributeValue> customAttributeValues);

	ResponseMessage addCustomAttributeValue(CustomAttributeValue customAttributeValue);

	CustomAttributeTemplateResponse getCustomAttributeTemplates(String processName, String key, Boolean isActive);

	RunningSearchResponseDto getAutoCompleteValues(String processName,String key, String attributeValue);

	CustomDetailDto getDetail(String processName, String taskId);

	ResponseMessage postDetail(CustomDetailDto customDetail);
}
