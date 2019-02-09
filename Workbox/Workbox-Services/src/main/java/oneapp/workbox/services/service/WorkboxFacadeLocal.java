package oneapp.workbox.services.service;

import java.util.List;

import oneapp.workbox.services.dto.SlaProcessNameListDto;
import oneapp.workbox.services.dto.TrackingResponse;
import oneapp.workbox.services.dto.WorkboxRequestDto;
import oneapp.workbox.services.dto.WorkboxResponseDto;

public interface WorkboxFacadeLocal {

	public WorkboxResponseDto getWorkboxFilterData(WorkboxRequestDto taskRequest);

	public WorkboxResponseDto getWorkboxCompletedFilterData(String processName, String requestId, String createdBy,
			String createdAt, String completedAt, String period, Integer skipCount, Integer maxCount, Integer page);

	public TrackingResponse getTrackingResults();

	public String sayHello();

	public List<SlaProcessNameListDto> getProcessNames();

	WorkboxResponseDto getWorkboxFilterData(WorkboxRequestDto taskRequest, Boolean isChatBot);

}
