package oneapp.workbox.services.util;

import java.util.Date;

import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.TokenDetailsDto;

public class CacheImplementation {
	
	/* OAuth Token Cache */
	public static TokenDetailsDto fetchTokenFromCache(String key) {
		EhCache ehCache = null;
		TokenDetailsDto tokenDetailsDto = null;
		try {
			ehCache = new EhCache("oAuthTokenCache");
			tokenDetailsDto = ehCache.retrieveTokenFromCache(key);
			if(ServicesUtil.isEmpty(tokenDetailsDto) || !checkTokenValidity(tokenDetailsDto.getExpirationDateTime())) {
				System.err.println("Token Not Valid Generating a new Token");
				tokenDetailsDto = OAuth.generateToken();
				putTokenInCache(tokenDetailsDto, key);
			}
		} catch (Exception ex) {
			System.err.println("Exception while fetching object from Cache : "+ex.getMessage());
		}
		return tokenDetailsDto;
	}
	
	private static ResponseMessage putTokenInCache(TokenDetailsDto tokenDetail, String key) {
		EhCache ehCache = null;
		try {
			ehCache = new EhCache("oAuthTokenCache");
			ehCache.putTokenInCache(tokenDetail, key);
			return new ResponseMessage(PMCConstant.SUCCESS, PMCConstant.CODE_SUCCESS, "Inserting Token Success");
		} catch (Exception ex) {
			System.err.println("Exception while clearing Cache : "+ex.getMessage());
		}
		return new ResponseMessage(PMCConstant.FAILURE, PMCConstant.CODE_FAILURE, "Inserting Token Failure");
	}
	
	private static boolean checkTokenValidity(Long longDate){
		Date expirationDate;
		try {
			Date currentDate = new Date();
			expirationDate = new Date(longDate);
			if (currentDate.before(expirationDate)) {
				return true;
			} 
		}
		catch (Exception e) {
			System.err.println("[CacheImplFacade][checkTokenValidity][Exception] : Error while checking expiration time, refetching token from service "+e);
		}
		return false;
	}
	
}