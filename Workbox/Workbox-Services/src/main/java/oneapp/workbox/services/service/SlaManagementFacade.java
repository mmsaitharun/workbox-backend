package oneapp.workbox.services.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oneapp.workbox.services.dao.SlaManagementDao;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.SlaListDto;
import oneapp.workbox.services.dto.SlaProcessNamesResponse;

/**
 * Session Bean implementation class ConfigurationFacade
 */
@Service("SlaManagementFacade")
public class SlaManagementFacade implements SlaManagementFacadeLocal {

	
	@Autowired
	private SlaManagementDao dao;

	@Override
	public SlaProcessNamesResponse getAllProcessNames(String userRole) {
		SlaProcessNamesResponse response = new SlaProcessNamesResponse();
		ResponseMessage responseMessage = new ResponseMessage();
//		response.setSlaProcessNames(new SlaManagementDao(em.getEntityManager())
		response.setSlaProcessNames(dao.getSlaProcessList(userRole));
		responseMessage.setMessage("Sla Processes List Fetched Successfully");
		responseMessage.setStatus("SUCCESS");
		responseMessage.setStatusCode("1");
		response.setResponseMessage(responseMessage);
		return response;
	}

	@Override
	public SlaListDto getSlaDetails(String processName) {
		SlaListDto  slaList = dao.getDetailSla(processName);
		return slaList;
	}

	@Override
	public ResponseMessage updateSla(SlaListDto dto) {
		return dao.updateSla(dto);
	}
	
}
