package oneapp.workbox.services.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import oneapp.workbox.services.dto.TokenDetailsDto;

public class EhCache {
	
	private CacheManager cacheManager;
	private Cache cache;

	public EhCache() {
	}

	public EhCache(String cacheName) {
		cacheManager = CacheManager.create();
		cache = cacheManager.getCache(cacheName);
	}
	
	/* OAuth Token Cache Impl */
	public void putTokenInCache(TokenDetailsDto token, String key) {
		System.out.println("[EhCacheImplementation][TokenCache][putTokenInCache] putInCache : "+token.getToken());
		try {
			cache.put(new Element(key, token));
		} catch (CacheException e) {
			System.err.println(String.format("Problem occurred while putting Token into cache: %s", e.getMessage()));
		}
	}
	
	public TokenDetailsDto retrieveTokenFromCache(String key) {
		System.out.println("[EhCacheImplementation][TokenCache][retrieveTokenFromCache] retriveFromCache : "+key);
		try {
			Element element = cache.get(key);
			if (element != null)
				return (TokenDetailsDto) element.getObjectValue();
		} catch (CacheException ce) {
			System.err.println(
					String.format("Problem occurred while trying to retrieve TaskCacheInstance from cache: %s", ce.getMessage()));
		}
		return null;
	}
	
}