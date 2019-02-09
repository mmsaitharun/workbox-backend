package oneapp.workbox.services.dto;

import java.util.List;

import oneapp.workbox.services.entity.CustomAttributeValue;

public class CustomAttributeValueRequest {

	List<CustomAttributeValue> customAttributeValues;

	public List<CustomAttributeValue> getCustomAttributeValues() {
		return customAttributeValues;
	}

	public void setCustomAttributeValues(List<CustomAttributeValue> customAttributeValues) {
		this.customAttributeValues = customAttributeValues;
	}

	@Override
	public String toString() {
		return "CustomAttributeTempRequest [customAttributeValues=" + customAttributeValues + "]";
	}

}
