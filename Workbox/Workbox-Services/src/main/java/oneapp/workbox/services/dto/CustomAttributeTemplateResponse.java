package oneapp.workbox.services.dto;

import java.util.List;

import oneapp.workbox.services.entity.CustomAttributeTemplate;

public class CustomAttributeTemplateResponse {

	List<CustomAttributeTemplate> customAttributeTemplates;
	ResponseMessage responseMessage = new ResponseMessage();
	
	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}

	public List<CustomAttributeTemplate> getCustomAttributeTemplates() {
		return customAttributeTemplates;
	}

	public void setCustomAttributeTemplates(List<CustomAttributeTemplate> customAttributeTemplates) {
		this.customAttributeTemplates = customAttributeTemplates;
	}

	@Override
	public String toString() {
		return "CustomAttributeTemplateResponse [customAttributeTemplates=" + customAttributeTemplates
				+ ", responseMessage=" + responseMessage + "]";
	}

}
