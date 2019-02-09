package oneapp.workbox.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import oneapp.workbox.services.dto.SearchListResponseDto;
import oneapp.workbox.services.dto.UserWorkLoadResponseDto;
import oneapp.workbox.services.dto.UserWorkloadRequestDto;
import oneapp.workbox.services.service.HeatMapFacadeLocal;

@RestController
@ComponentScan("oneapp.incture.workbox")
@RequestMapping(value = "/heatMap", produces = "application/json")
public class HeatMapRest {

	@Autowired
	private HeatMapFacadeLocal heatMapFacadeLocal;

	@RequestMapping(value = "/getSearchList", method = RequestMethod.GET)
	public SearchListResponseDto forwarding() {
		return heatMapFacadeLocal.getSearchList();
	}

	@RequestMapping(value = "/getUserWorkload", method = RequestMethod.POST)
	public UserWorkLoadResponseDto getUserWorkload(@RequestBody UserWorkloadRequestDto dto) {
		return heatMapFacadeLocal.getUserWorkLoad(dto);
	}
}