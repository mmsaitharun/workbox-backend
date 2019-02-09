package oneapp.workbox.services.service;

import oneapp.workbox.services.dto.SearchListResponseDto;
import oneapp.workbox.services.dto.UserWorkLoadResponseDto;
import oneapp.workbox.services.dto.UserWorkloadRequestDto;

public interface HeatMapFacadeLocal {

	public SearchListResponseDto getSearchList();

	public UserWorkLoadResponseDto getUserWorkLoad(UserWorkloadRequestDto dto);

}
