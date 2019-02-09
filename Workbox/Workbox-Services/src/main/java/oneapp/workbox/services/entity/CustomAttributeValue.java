package oneapp.workbox.services.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "CUSTOM_ATTR_VALUES")
public class CustomAttributeValue implements Serializable {

	private static final long serialVersionUID = 1L;

	public CustomAttributeValue() {
		super();
	}

	public CustomAttributeValue(String taskId, String processName, String key, String attributeValue) {
		super();
		this.taskId = taskId;
		this.processName = processName;
		this.key = key;
		this.attributeValue = attributeValue;
	}

	public CustomAttributeValue(String processName, String key, String attributeValue) {
		super();
		this.processName = processName;
		this.key = key;
		this.attributeValue = attributeValue;
	}

	@Id
	@Column(name = "TASK_ID", length = 32)
	private String taskId;

	@Id
	@Column(name = "PROCESS_NAME", length = 32)
	private String processName;

	@Id
	@Column(name = "KEY", length = 50)
	private String key;

	@Transient
	private String attributeTemplate;

	public String getAttributeTemplate() {
		return attributeTemplate;
	}

	public void setAttributeTemplate(String attributeTemplate) {
		this.attributeTemplate = attributeTemplate;
	}

	@Column(name = "ATTR_VALUE", length = 32)
	private String attributeValue;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
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

	@Override
	public String toString() {
		return "CustomAttributeValue [processName=" + processName + ", key=" + key + ", attributeValue="
				+ attributeValue + "]";
	}

}
