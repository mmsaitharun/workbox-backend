package oneapp.workbox.services.util;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oneapp.workbox.services.dto.TokenDetailsDto;

public class OAuth {

	private static final Logger logger = LoggerFactory.getLogger(OAuth.class);

	protected static TokenDetailsDto generateToken() {
		TokenDetailsDto tokenDetailsDto = null;
		try {
			tokenDetailsDto = new TokenDetailsDto();
			String requestUrl = "https://oauthasservices-x5qv5zg6ns.hana.ondemand.com/oauth2/api/v1/token?grant_type=client_credentials";
			Object responseObject = RestUtil
					.callRestService(requestUrl, null, null, "POST", "application/json", false, null,
							"e5422b86-1f6f-33a6-a6b6-2bd5cabde2a1", "Workbox@123", null, null, null)
					.getResponseObject();
			JSONObject resources = ServicesUtil.isEmpty(responseObject) ? null : (JSONObject) responseObject;
			tokenDetailsDto.setToken(resources.optString("access_token"));
			tokenDetailsDto.setTokenType(resources.optString("token_type"));
			tokenDetailsDto.setExpirationDateTime(System.currentTimeMillis() + (resources.optLong("expires_in") * 1000));
		} catch (Exception e) {
			logger.error("[PMC][OAuth][getOAuthToken][error]" + e.getMessage());
		}
		return tokenDetailsDto;
	}
	
	public static synchronized TokenDetailsDto getToken() {
		return CacheImplementation.fetchTokenFromCache(PMCConstant.APPLICATION_OAUTH_TOKEN);
	}

}
