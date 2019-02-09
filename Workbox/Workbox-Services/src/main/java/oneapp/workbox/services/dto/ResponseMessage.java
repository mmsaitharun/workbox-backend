package oneapp.workbox.services.dto;

public class ResponseMessage {
	
	private String status;
	private String statusCode;
	private String message;
	
	@Override
	public String toString() {
		return "ResponseMessage [status=" + status + ", statusCode=" + statusCode + ", message=" + message + "]";
	}

	public ResponseMessage() {
		super();
	}

	public ResponseMessage(String status, String statusCode, String message) {
		super();
		this.status = status;
		this.statusCode = statusCode;
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
