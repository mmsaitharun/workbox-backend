package oneapp.workbox.services.dto;

public class GraphResponseDto {

	private GraphDto graphDto;
	private ResponseMessage message;

	ResponseMessage getMessage() {
		return message;
	}

	public GraphDto getGraphDto() {
		return graphDto;
	}

	public void setGraphDto(GraphDto graphDto) {
		this.graphDto = graphDto;
	}

	public void setMessage(ResponseMessage message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "GraphResponseDto [graphDto=" + graphDto + ", message=" + message + "]";
	}

}
