package oneapp.workbox.services.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.security.um.user.User;

import oneapp.workbox.services.dao.UserDetailsDao;
import oneapp.workbox.services.dao.UserIDPMappingDao;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.UserDetailsDto;
import oneapp.workbox.services.dto.UserIDPMappingDto;
import oneapp.workbox.services.dto.UserIDPMappingResponseDto;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;
import oneapp.workbox.services.util.UserManagementUtil;

@Service("UserIDPMappingFacade")
public class UserIDPMappingFacade implements UserIDPMappingFacadeLocal {

	private static final Logger logger = LoggerFactory.getLogger(UserIDPMappingFacade.class);

	@Autowired
	private UserIDPMappingDao idpUserDao;

	@Autowired
	private UserDetailsDao userDetailDao;

	ResponseMessage responseDto;

	UserIDPMappingResponseDto userDto;

	@Override
	public UserIDPMappingResponseDto getIDPUser() {
		UserIDPMappingResponseDto userDto = new UserIDPMappingResponseDto();
		responseDto = new ResponseMessage();
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		try {
			List<UserIDPMappingDto> dtoList = idpUserDao.getAllUser();

			if (!ServicesUtil.isEmpty(dtoList)) {
				userDto.setDto(dtoList);
				responseDto.setMessage("Data fetched Successfully");
			} else {
				responseDto.setMessage(PMCConstant.NO_RESULT);
			}
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		} catch (Exception e) {
			logger.error("[PMC][UserIDPMappingFacade][getIDPUser][error]" + e.getMessage());
			responseDto.setMessage("Fetching data failed due to " + e.getMessage());
		}
		userDto.setMessage(responseDto);
		return userDto;
	}

	@Override
	public ResponseMessage createIdpUsers() {
		responseDto = new ResponseMessage();
		responseDto.setStatus(PMCConstant.STATUS_FAILURE);
		responseDto.setStatusCode(PMCConstant.CODE_FAILURE);

		String userNotCreated = "";
		String jsonString = "";
//		jsonString = DestinationUtil.executeWithDest("idpdestination", "scim/Users", "GET",
//				"application/scim+json", null, null, null, false, null, null);

		JSONObject jsonObject = new JSONObject(jsonString);
		JSONArray resources = jsonObject.getJSONArray("Resources");
		JSONObject resource;
		UserIDPMappingDto mappingDto = new UserIDPMappingDto();
		List<UserIDPMappingDto> userDto = idpUserDao.getAllUser();
		List<String> existingUser = null;
		List<String> newUsers = new ArrayList<String>();
		System.err.println("[Workbox][createIdpUsers][existingUserDto]" + userDto);
		if (!ServicesUtil.isEmpty(userDto)) {
			existingUser = new ArrayList<String>();
			for (UserIDPMappingDto userIDPMappingDto : userDto) {
				existingUser.add(userIDPMappingDto.getUserLoginName());
			}
			System.err.println("[Workbox][createIdpUsers][existingUser]" + existingUser);

		}
		for (Object obj : resources) {
			resource = (JSONObject) obj;

			try {
				mappingDto.setUserId(resource.getString("id"));
				mappingDto.setUserFirstName(resource.getJSONObject("name").getString("givenName"));
				mappingDto.setUserLastName(resource.getJSONObject("name").getString("familyName"));
				mappingDto.setUserEmail(resource.getJSONArray("emails").getJSONObject(0).getString("value"));
				boolean loginId = resource.has("userName");
				String loginName = "";
				if (loginId)
					loginName = resource.getString("userName");
				else
					loginName = resource.getString("id");

				mappingDto.setUserLoginName(loginName);
				newUsers.add(loginName);
				idpUserDao.createIDPUser(mappingDto).equals(PMCConstant.SUCCESS);

			} catch (Exception e) {
				userNotCreated += resource.getString("id") + " , ";
				logger.error("[PMC][UserIDPMappingFacade][getIDPUser][error]" + e.getMessage());
			}

		}
		if (!ServicesUtil.isEmpty(existingUser) && !ServicesUtil.isEmpty(newUsers)) {
			existingUser.removeAll(newUsers);
			System.err.println("[WORKBOX][createUser][existingUser]" + existingUser);
			if (!ServicesUtil.isEmpty(existingUser)) {
				for (String str : existingUser) {
					UserIDPMappingDto dto = new UserIDPMappingDto();
					dto.setUserLoginName(str);
					try {
						idpUserDao.delete(dto);
					} catch (Exception e) {
						System.err.println("[PMC][UserIDPMappingFacade][createUser][error]" + e.getMessage());
					}
				}
			}
		}
		if (ServicesUtil.isEmpty(userNotCreated))
			responseDto.setMessage("IDP User " + PMCConstant.CREATED_SUCCESS);
		else
			responseDto
					.setMessage("IDP User " + PMCConstant.CREATED_SUCCESS + ", Users not created: " + userNotCreated);

		responseDto.setStatus(PMCConstant.STATUS_SUCCESS);
		responseDto.setStatusCode(PMCConstant.CODE_SUCCESS);
		return responseDto;
	}

	@Override
	public UserDetailsDto getUserDetails() {

		User loggedInUser = UserManagementUtil.getLoggedInUser();
		UserDetailsDto dto = new UserDetailsDto();
		dto.setUserId(loggedInUser.getName());
		dto.setFirstName(loggedInUser.getName());

		return userDetailDao.getUserDetails(dto);
	}
}
