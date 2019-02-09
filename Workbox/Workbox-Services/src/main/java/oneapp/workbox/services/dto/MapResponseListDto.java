package oneapp.workbox.services.dto;

import java.util.List;

public class MapResponseListDto {
	private List<MapResponseDto> entry;

	public List<MapResponseDto> getEntry() {
		return entry;
	}

	public void setEntry(List<MapResponseDto> entry) {
		this.entry = entry;
	}
	
	@Override
	public String toString() {
		return "MapResponseListDto [entry=" + entry + "]";
	}
}