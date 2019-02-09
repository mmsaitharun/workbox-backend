package oneapp.workbox.services.service;

import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.UserDetailsDto;
import oneapp.workbox.services.dto.UserIDPMappingResponseDto;

public interface UserIDPMappingFacadeLocal {

	public UserIDPMappingResponseDto getIDPUser();

	public ResponseMessage createIdpUsers();

	public UserDetailsDto getUserDetails();

}
