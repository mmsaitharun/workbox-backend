package oneapp.workbox.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.RoleInfoDto;
import oneapp.workbox.services.dto.UserDetailsDto;
import oneapp.workbox.services.dto.UserIDPMappingResponseDto;
import oneapp.workbox.services.service.UserIDPMappingFacadeLocal;

@RestController
@ComponentScan("oneapp.incture.workbox")
@RequestMapping(value = "/idpMapping", produces = "application/json")
public class UserIDPMappingRest {

	@Autowired
	private UserIDPMappingFacadeLocal userIDPMappingLocal;

	@RequestMapping(value = "/getUsers", method = RequestMethod.GET)
	public UserIDPMappingResponseDto getIdpUsers() {
		return userIDPMappingLocal.getIDPUser();
	}

	@RequestMapping(value = "/createUsers", method = RequestMethod.GET)
	public ResponseMessage createIDPUsers() {
		return userIDPMappingLocal.createIdpUsers();
	}

	@RequestMapping(value = "/getUserRoleInfo", method = RequestMethod.GET)
	public RoleInfoDto getUserRoleInfo(@RequestParam("userId") String userId) {
		RoleInfoDto roleInfoDto = null;
		if ("P000006".equalsIgnoreCase(userId)) {
			roleInfoDto = new RoleInfoDto("admin", "Workbox Admin", true);
		} else {
			roleInfoDto = new RoleInfoDto("workbox_user", "Workbox User", false);
		}
		return roleInfoDto;
	}
	
	@RequestMapping(value = "/getuserDetails", method = RequestMethod.GET)
	public UserDetailsDto getUserDetails() {
		return userIDPMappingLocal.getUserDetails();
	}
}
