package oneapp.workbox.services.adapters;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.jsoniter.JsonIterator;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;

import oneapp.workbox.services.dto.RestResponse;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.RestUtil;
import oneapp.workbox.services.util.ServicesUtil;

public class TestClass {

	public static void main(String[] args) {
		String requestUrl = PMCConstant.REQUEST_URL_INST
				+ "task-instances?status=READY&status=RESERVED&status=CANCELED&status=COMPLETED&$expand=attributes";
		List<Event> list = null;
		list = getInstances(requestUrl, Task.class);
		System.out.println(list.get(0) instanceof Task);
	}

	@SuppressWarnings("rawtypes")
	private static List<Event> getInstances(String requestUrl, Class clazz) {
		
		JsoniterSpi.registerTypeImplementation(Event.class, clazz);

		int taskInstancesCount = -1;
		Object responseObject = null;
		HttpResponse httpResponse = null;
		RestResponse restResponse = null;
		List<Event> jsonArray = null;
		List<Event> jsonArraySkip = null;

		if (!ServicesUtil.isEmpty(requestUrl)) {
			requestUrl += "&$top=1000&$inlinecount=allpages";
			restResponse = RestUtil.invokeRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TI, null, "GET",
					"application/json", false, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null, null,
					null);
		}
		responseObject = restResponse.getResponseObject();
		httpResponse = restResponse.getHttpResponse();
		for (Header header : httpResponse.getAllHeaders()) {
			if (header.getName().equalsIgnoreCase("X-Total-Count")) {
				taskInstancesCount = Integer.parseInt(header.getValue());
			}
		}
		jsonArray = ServicesUtil.isEmpty(responseObject) ? null : JsonIterator.deserialize(responseObject.toString(), new TypeLiteral<List<Event>>(){});
		if (taskInstancesCount > jsonArray.size()) {
			int skip = 1000;
			for (int k = 1; k < taskInstancesCount / skip; k++) {
				requestUrl += "&$skip=" + (skip * k);
				restResponse = RestUtil.invokeRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TI, null, "GET",
						"application/json", true, null, PMCConstant.WF_BASIC_USER, PMCConstant.WF_BASIC_PASS, null,
						null, null);
				responseObject = restResponse.getResponseObject();
				jsonArraySkip = ServicesUtil.isEmpty(responseObject) ? null : JsonIterator.deserialize(responseObject.toString(), new TypeLiteral<List<Event>>(){});
				if(!ServicesUtil.isEmpty(jsonArray) && jsonArray.size() > 0) {
					jsonArray.addAll(jsonArraySkip);
				} else {
					jsonArray = jsonArraySkip;
				}
			}
		}
		return jsonArray;
	}

}
