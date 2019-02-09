package oneapp.workbox.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import oneapp.workbox.services.dto.GraphResponseDto;
import oneapp.workbox.services.service.GraphFacadeLocal;

@RestController
@ComponentScan("oneapp.incture.workbox")
@RequestMapping(value = "/graph", produces = "application/json")
public class GraphRest {

	@Autowired
	private GraphFacadeLocal graphFacadeLocal;

	@RequestMapping(value = "/getAllDetails", method = RequestMethod.GET)
	public GraphResponseDto getGraphDetails(@RequestParam(value = "processName", required = false) String processName,
			@RequestParam(value = "graphType", required = false) String graphType,
			@RequestParam(value = "duration", required = false) String duration,
			@RequestParam(value = "userId", required = false) String userId) {
		return graphFacadeLocal.getGraphDetails(processName, graphType, duration, userId);
	}
}