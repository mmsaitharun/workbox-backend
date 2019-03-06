package oneapp.workbox.services.dto;

public class WorkFlowDetailAttribute {

	public WorkFlowDetailAttribute() {
		super();
	}

	public WorkFlowDetailAttribute(String attributeValue, String attributeLabel) {
		super();
		this.attributeValue = attributeValue;
		this.attributeLabel = attributeLabel;
	}

	private String attributeValue;
	private String attributeLabel;

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public String getAttributeLabel() {
		return attributeLabel;
	}

	public void setAttributeLabel(String attributeLabel) {
		this.attributeLabel = attributeLabel;
	}

	@Override
	public String toString() {
		return "WorkFlowDetailAttribute [attributeValue=" + attributeValue + ", attributeLabel=" + attributeLabel + "]";
	}

}
