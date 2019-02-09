package oneapp.workbox.services.service;

import oneapp.workbox.services.dto.ActionDto;
import oneapp.workbox.services.dto.ResponseMessage;

public interface WorkFlowActionFacadeLocal {

	public ResponseMessage taskAction(ActionDto dto);
}
