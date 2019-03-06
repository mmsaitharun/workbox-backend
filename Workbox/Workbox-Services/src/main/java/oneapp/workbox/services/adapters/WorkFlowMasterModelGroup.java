package oneapp.workbox.services.adapters;

public class WorkFlowMasterModelGroup {

	public WorkFlowMasterModelGroup() {
		super();
	}

	public WorkFlowMasterModelGroup(String key, String title, String status) {
		super();
		this.key = key;
		this.title = title;
		this.status = status;
	}

	private String key;
	private String title;
	private String status;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "WorkFlowMasterModelGroup [key=" + key + ", title=" + title + ", status=" + status + "]";
	}

}
