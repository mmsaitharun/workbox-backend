package oneapp.workbox.services.dto;

import java.util.List;

import oneapp.workbox.services.entity.CustomAttributeTemplate;

public class DynamicDetailDto {
	
	public DynamicDetailDto() {
		super();
	}
	public DynamicDetailDto(String title, List<CustomAttributeTemplate> customDetails) {
		super();
		this.title = title;
		this.customDetails = customDetails;
	}
	private String title;
	private List<CustomAttributeTemplate> customDetails;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<CustomAttributeTemplate> getCustomDetails() {
		return customDetails;
	}
	public void setCustomDetails(List<CustomAttributeTemplate> customDetails) {
		this.customDetails = customDetails;
	}
	@Override
	public String toString() {
		return "DynamicDetailDto [title=" + title + ", customDetails=" + customDetails + "]";
	}
	
	

}
