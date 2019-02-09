package oneapp.workbox.services.util;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OAuth {

	private static final Logger logger = LoggerFactory.getLogger(OAuth.class);

	public String[] getToken() {
		String[] result = new String[2];
		try {
			String requestUrl = "https://oauthasservices-kbniwmq1aj.hana.ondemand.com/oauth2/api/v1/token?grant_type=client_credentials";
			Object responseObject = RestUtil
					.callRestService(requestUrl, null, null, "POST", "application/json", false, null,
							"e5422b86-1f6f-33a6-a6b6-2bd5cabde2a1", "Workbox@123", null, null, null)
					.getResponseObject();
			JSONObject resources = ServicesUtil.isEmpty(responseObject) ? null : (JSONObject) responseObject;
			result[0] = resources.optString("access_token");
			result[1] = resources.optString("token_type");
		} catch (Exception e) {
			logger.error("[PMC][SubstitutionRuleFacade][getPrevRecipient][error]" + e.getMessage());
		}
		return result;
	}

}
