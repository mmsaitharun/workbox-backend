package oneapp.workbox.services.dto;

public class HeaderDto {

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String name;
	private String key;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "HeaderDto [type=" + type + ", name=" + name + ", key=" + key + "]";
	}

}