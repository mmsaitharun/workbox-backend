package oneapp.workbox.services.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "WB_CONFIG_VALUES")
public class ConfigValues {

	@Id
	@Column(name = "CONFIG_ID", length = 200)
	private String configId;

	@Column(name = "CONFIG_VALUE", length = 300)
	private String configValue;

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public String getConfigValue() {
		return configValue;
	}

	@Override
	public String toString() {
		return "ConfigValues [configId=" + configId + ", configValue=" + configValue + "]";
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

}