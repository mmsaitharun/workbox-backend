package oneapp.workbox.services.adapters;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.jsoniter.JsonIterator;
import com.jsoniter.spi.TypeLiteral;

import oneapp.workbox.services.dto.RestResponse;
import oneapp.workbox.services.dto.TokenDetailsDto;
import oneapp.workbox.services.util.OAuth;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.RestUtil;
import oneapp.workbox.services.util.ServicesUtil;

public abstract class EventDownloader<T extends List<?>> implements Callable<T> {
	
	@SuppressWarnings("rawtypes")
	public List getInstances(String requestUrl, Class clazz) {
		
		TokenDetailsDto tokenDetails = OAuth.getToken();

		int taskInstancesCount = -1;
		Object responseObject = null;
		HttpResponse httpResponse = null;
		RestResponse restResponse = null;
		List<Task> taskArray = null;
		List<Task> taskArraySkip = null;

		List<Process> processArray = null;
		List<Process> processArraySkip = null;
		
		int taskArraySize = 0;
		int processArraySize = 0;
		
		if (!ServicesUtil.isEmpty(requestUrl)) {
			requestUrl += "&$top=1000&$inlinecount=allpages";
			restResponse = RestUtil.invokeRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TI, null, "GET",
					"application/json", false, null, null, null, null, tokenDetails.getToken(), tokenDetails.getTokenType());
		}
		responseObject = restResponse.getResponseObject();
		httpResponse = restResponse.getHttpResponse();
		if(!ServicesUtil.isEmpty(httpResponse)) {
			for (Header header : httpResponse.getAllHeaders()) {
				if (header.getName().equalsIgnoreCase("X-Total-Count")) {
					taskInstancesCount = Integer.parseInt(header.getValue());
				}
			}
		} else if(!ServicesUtil.isEmpty(restResponse.getUrlConnection()) && restResponse.getResponseCode() == HttpURLConnection.HTTP_OK) {
			taskInstancesCount = Integer.parseInt(restResponse.getUrlConnection().getHeaderField("X-Total-Count"));
		}
		if (clazz.equals(Task.class)) {
			taskArray = ServicesUtil.isEmpty(responseObject) ? null
					: JsonIterator.deserialize(responseObject.toString(), new TypeLiteral<List<Task>>() {
					});
			if(!ServicesUtil.isEmpty(taskArray))
				taskArraySize = taskArray.size();
			if (taskInstancesCount > taskArraySize) {
				int skip = 1000;
				for (int k = 1; k < taskInstancesCount / skip; k++) {
					requestUrl += "&$skip=" + (skip * k);
					restResponse = RestUtil.invokeRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TI, null, "GET",
							"application/json", true, null, null, null, null,
							tokenDetails.getToken(), tokenDetails.getTokenType());
					responseObject = restResponse.getResponseObject();
					taskArraySkip = ServicesUtil.isEmpty(responseObject) ? null
							: JsonIterator.deserialize(responseObject.toString(), new TypeLiteral<List<Task>>() {
							});
					if (!ServicesUtil.isEmpty(taskArray) && taskArraySize > 0) {
						taskArray.addAll(taskArraySkip);
					} else {
						taskArray = taskArraySkip;
					}
				}
			}
			return taskArray;
		} else if (clazz.equals(Process.class)) {
			processArray = ServicesUtil.isEmpty(responseObject) ? null
					: JsonIterator.deserialize(responseObject.toString(), new TypeLiteral<List<Process>>() {
					});
			if(!ServicesUtil.isEmpty(processArray))
				processArraySize = processArray.size();
			if (taskInstancesCount > processArraySize) {
				int skip = 1000;
				for (int k = 1; k < taskInstancesCount / skip; k++) {
					requestUrl += "&$skip=" + (skip * k);
					restResponse = RestUtil.invokeRestService(requestUrl, PMCConstant.SAML_HEADER_KEY_TI, null, "GET",
							"application/json", false, null, null, null, null,
							tokenDetails.getToken(), tokenDetails.getTokenType());
					responseObject = restResponse.getResponseObject();
					processArraySkip = ServicesUtil.isEmpty(responseObject) ? null
							: JsonIterator.deserialize(responseObject.toString(), new TypeLiteral<List<Process>>() {
							});
					if (!ServicesUtil.isEmpty(processArray) && processArraySize > 0) {
						processArray.addAll(processArraySkip);
					} else {
						processArray = processArraySkip;
					}
				}
			}
			return processArray;
		}
		return null;

	}

}
