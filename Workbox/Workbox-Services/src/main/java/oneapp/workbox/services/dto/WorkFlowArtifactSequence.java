package oneapp.workbox.services.dto;

public class WorkFlowArtifactSequence {

	public WorkFlowArtifactSequence() {
		super();
	}

	public WorkFlowArtifactSequence(String from, String to, String status) {
		super();
		this.from = from;
		this.to = to;
		this.status = status;
	}

	private String from;
	private String to;
	private String status;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "WorkFlowArtifactSequence [from=" + from + ", to=" + to + ", status=" + status + "]";
	}

	

}
