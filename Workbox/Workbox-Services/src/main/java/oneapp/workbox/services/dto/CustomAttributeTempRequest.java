package oneapp.workbox.services.dto;

import java.util.List;

import oneapp.workbox.services.entity.CustomAttributeTemplate;

public class CustomAttributeTempRequest {

	List<CustomAttributeTemplate> customAttributeTemplates ;

	

	public List<CustomAttributeTemplate> getCustomAttributeTemplates() {
		return customAttributeTemplates;
	}



	public void setCustomAttributeTemplates(List<CustomAttributeTemplate> customAttributeTemplates) {
		this.customAttributeTemplates = customAttributeTemplates;
	}



	@Override
	public String toString() {
		return "CustomAttributeTempRequest [customAttributeTemplates=" + customAttributeTemplates + "]";
	}



	

}