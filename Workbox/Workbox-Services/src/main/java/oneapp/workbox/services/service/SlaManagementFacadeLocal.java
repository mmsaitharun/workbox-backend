package oneapp.workbox.services.service;

import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.SlaListDto;
import oneapp.workbox.services.dto.SlaProcessNamesResponse;

public interface SlaManagementFacadeLocal {

	SlaProcessNamesResponse getAllProcessNames(String userRole);

	SlaListDto getSlaDetails(String processName);

	ResponseMessage updateSla(SlaListDto dto);

}
