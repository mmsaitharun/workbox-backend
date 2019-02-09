package oneapp.workbox.services.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "CUSTOM_ATTR_TEMPLATE")
public class CustomAttributeTemplate implements Serializable {

	
	public CustomAttributeTemplate() {
		super();
	}

	public CustomAttributeTemplate(String dataType, String label, Boolean isMandatory, Boolean isEditable) {
		super();
		this.dataType = dataType;
		this.label = label;
		this.isMandatory = isMandatory;
		this.isEditable = isEditable;
	}

	private static final long serialVersionUID = 1L;

	@Column(name = "DATA_TYPE", length = 50)
	private String dataType;

	@Column(name = "LABEL", length = 60)
	private String label;

	@Id
	@Column(name = "PROCESS_NAME", length = 32)
	private String processName;

	@Id
	@Column(name = "KEY", length = 50)
	private String key;

	@Column(name = "IS_ACTIVE")
	@ColumnDefault(value = "1")
	private Boolean isActive = true;

	@Column(name = "ATTR_DES", length = 200)
	private String description;
	
	@Transient
	private String value;

	/* Added for Custom Detail Screen */
	
	@Column(name = "IS_MAND")
	private Boolean isMandatory;

	@Column(name = "IS_EDITABLE")
	private Boolean isEditable;

	@Transient
	private List<CustomAttributeValue> attributeValues;
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getIsMandatory() {
		return isMandatory;
	}

	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public Boolean getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}

	public List<CustomAttributeValue> getAttributeValues() {
		return attributeValues;
	}

	public void setAttributeValues(List<CustomAttributeValue> attributeValues) {
		this.attributeValues = attributeValues;
	}

	@Override
	public String toString() {
		return "CustomAttributeTemplate [dataType=" + dataType + ", processName=" + processName + ", key=" + key
				+ ", isActive=" + isActive + ", value=" + value + ", attributeValues=" + attributeValues + "]";
	}

}
