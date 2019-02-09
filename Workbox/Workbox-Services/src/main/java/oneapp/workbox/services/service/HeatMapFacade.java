package oneapp.workbox.services.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oneapp.workbox.services.dao.HeatMapDao;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.SearchListDto;
import oneapp.workbox.services.dto.SearchListResponseDto;
import oneapp.workbox.services.dto.UserWorkLoadResponseDto;
import oneapp.workbox.services.dto.UserWorkloadDto;
import oneapp.workbox.services.dto.UserWorkloadRequestDto;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;

@Service("HeatMapFacade")
public class HeatMapFacade implements HeatMapFacadeLocal {

	private static final Logger logger = LoggerFactory.getLogger(HeatMapFacade.class);

	@Autowired
	HeatMapDao heatMapDao;

	ResponseMessage responseDto;

	@Override
	public SearchListResponseDto getSearchList() {
		SearchListResponseDto dto = new SearchListResponseDto();
		responseDto = new ResponseMessage();
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		try {
			List<SearchListDto> procList = new ArrayList<SearchListDto>(), statusList = new ArrayList<SearchListDto>();
			procList = heatMapDao.getSearchList(PMCConstant.SEARCH_PROCESS);
			statusList = heatMapDao.getSearchList(PMCConstant.SEARCH_STATUS);
			if (!ServicesUtil.isEmpty(procList) || !ServicesUtil.isEmpty(statusList)) {
				dto.setProcList(procList);
				dto.setStatusList(statusList);
				responseDto.setMessage("Data fetched Successfully");
			} else {
				responseDto.setMessage(PMCConstant.NO_RESULT);
			}
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		} catch (Exception e) {
			logger.error("[PMC][SubstitutionRuleFacade][getUserSubstitution][error]" + e.getMessage());
			responseDto.setMessage("Fetching data failed due to " + e.getMessage());
		}
		dto.setMessage(responseDto);
		return dto;
	}

	@Override
	public UserWorkLoadResponseDto getUserWorkLoad(UserWorkloadRequestDto dto) {
		UserWorkLoadResponseDto response = new UserWorkLoadResponseDto();
		responseDto = new ResponseMessage();
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		try {
			List<UserWorkloadDto> dtos = heatMapDao.getUserWorkload(dto.getProcessName(), dto.getRequestId(),
					dto.getTaskStatus());

			if (!ServicesUtil.isEmpty(dtos)) {
				response.setUserWorkloadDtos(dtos);
				responseDto.setMessage("Data fetched Successfully");
			} else {
				responseDto.setMessage(PMCConstant.NO_RESULT);
			}
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		} catch (Exception e) {
			logger.error("[PMC][HeatMap][getUserWorkLoad][error]" + e.getMessage());
			responseDto.setMessage("Fetching data failed due to " + e.getMessage());
		}
		response.setMessage(responseDto);
		return response;
	}


}