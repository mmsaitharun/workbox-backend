package oneapp.workbox.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.SlaListDto;
import oneapp.workbox.services.dto.SlaProcessNamesResponse;
import oneapp.workbox.services.service.SlaManagementFacadeLocal;

@RestController
@CrossOrigin
@ComponentScan("oneapp.incture.workbox")
@RequestMapping(value = "/sla", produces = "application/json")
public class SLARest {

	@Autowired
	private SlaManagementFacadeLocal sla;

	@RequestMapping(value = "/process", method = RequestMethod.GET)
	public SlaProcessNamesResponse getAllProcessNames(String userRole) {
		return sla.getAllProcessNames(userRole);
	}

	@RequestMapping(value = "/details/{processName}", method = RequestMethod.GET)
	public SlaListDto getSlaDetails(@PathVariable("processName") String processName) {
		return sla.getSlaDetails(processName);
	}

	@RequestMapping(value = "/updateSla", method = RequestMethod.POST)
	public ResponseMessage getTasksByUserAndDuration(@RequestBody SlaListDto slaDto) {
		return sla.updateSla(slaDto);
	}

}
