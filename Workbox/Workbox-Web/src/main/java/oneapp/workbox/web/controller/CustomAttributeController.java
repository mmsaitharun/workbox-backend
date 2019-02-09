package oneapp.workbox.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import oneapp.workbox.services.dto.CustomAttributeTempRequest;
import oneapp.workbox.services.dto.CustomAttributeTemplateResponse;
import oneapp.workbox.services.dto.CustomAttributeValueRequest;
import oneapp.workbox.services.dto.CustomDetailDto;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.RunningSearchResponseDto;
import oneapp.workbox.services.entity.CustomAttributeTemplate;
import oneapp.workbox.services.entity.CustomAttributeValue;
import oneapp.workbox.services.service.CustomAttributeFacadeLocal;

@RestController
@CrossOrigin
@ComponentScan("oneapp.incture")
@RequestMapping(value = "/customAttribute", produces = "application/json")
public class CustomAttributeController {

	@Autowired
	CustomAttributeFacadeLocal customAttributeFacade;

	@RequestMapping(value = "/addTemp", method = RequestMethod.POST)
	public ResponseMessage addCustomAttributeTemp(@RequestBody CustomAttributeTempRequest customAttributeTempRequest) {
		return customAttributeFacade.addCustomAttributeTemp(customAttributeTempRequest.getCustomAttributeTemplates());
	}
	@RequestMapping(value="/tempRequest",method=RequestMethod.GET)
	public CustomAttributeTempRequest seeTemplate(){
		CustomAttributeTempRequest temp=new CustomAttributeTempRequest();
		List<CustomAttributeTemplate> list=new ArrayList<>();
		CustomAttributeTemplate customtemp1=new CustomAttributeTemplate();
		customtemp1.setDataType("TEXT");
		customtemp1.setKey("Test");
		customtemp1.setLabel("temp1");
		customtemp1.setProcessName("ALL");
		list.add(customtemp1);
		temp.setCustomAttributeTemplates(list);
		return temp;
	}

	@RequestMapping(value = "/addValues", method = RequestMethod.POST)
	public ResponseMessage addCustomAttributeValues(
			@RequestBody CustomAttributeValueRequest customAttributeValueRequest) {
		return customAttributeFacade.addCustomAttributeValues(customAttributeValueRequest.getCustomAttributeValues());
	}
	
	@RequestMapping(value = "/addValue", method = RequestMethod.POST)
	public ResponseMessage addCustomAttributeValue(@RequestBody CustomAttributeValue customAttributeValue) {
		return customAttributeFacade.addCustomAttributeValue(customAttributeValue);
	}
	
	@RequestMapping(value = "/getCustomTemplates", method = RequestMethod.GET)
	public CustomAttributeTemplateResponse getCustomTemplates(@RequestParam(value="processName") String processName, @RequestParam(value = "key", defaultValue = "")String key, @RequestParam(value="isActive", required=false) Boolean isActive) {
		return customAttributeFacade.getCustomAttributeTemplates(processName, key, isActive);
	}
	@RequestMapping(value = "/autoComplete", method = RequestMethod.GET)
	public RunningSearchResponseDto getAutoCompleteValues(@RequestParam("processName") String processName,@RequestParam("key") String key,@RequestParam("value") String value) {
		return customAttributeFacade.getAutoCompleteValues(processName,key, value);
	}
	
	@RequestMapping(value = "/getDetail", method = RequestMethod.GET)
	public CustomDetailDto getDetail(@RequestParam("processName") String processName, @RequestParam("taskId") String taskId) {
		return customAttributeFacade.getDetail(processName, taskId);
	}
	
	@RequestMapping(value = "/postDetail", method = RequestMethod.POST)
	public ResponseMessage postDetail(@RequestBody CustomDetailDto customDetail) {
		return customAttributeFacade.postDetail(customDetail);
	}
	
}
