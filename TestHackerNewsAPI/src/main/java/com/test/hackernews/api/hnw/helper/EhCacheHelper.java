package com.test.hackernews.api.hnw.helper;

import javax.annotation.PreDestroy;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.test.hackernews.api.hnw.constants.CommonConstants;

import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import reactor.core.publisher.Mono;

@Component
@EnableCaching
@Slf4j
public class EhCacheHelper {
	@Autowired(required = false)
	
	private Ehcache cache;
	
	public EhCacheHelper() {
		cache= getEHcache();
	}
	
	@Bean
	public Ehcache getEHcache() {
		Configuration cacheManagerConfig = new Configuration();

		CacheConfiguration cacheConfig = new CacheConfiguration("localCache",
				1000).memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
						.eternal(true)
						.timeToLiveSeconds(60000000)
						.timeToIdleSeconds(60000000)
						.diskExpiryThreadIntervalSeconds(60000000)
						.persistence(new PersistenceConfiguration()
								.strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP));

		cacheManagerConfig.addCache(cacheConfig);

		return CacheManager.create(cacheManagerConfig).getEhcache("localCache");
	}

	public <T> Mono<T> addObject(String key, T value, int timeToLiveSeconds) {
		int timeToIdleSeconds = timeToLiveSeconds;
			try {
				log.debug("Adding object for key {}", key );
				cache.put(new Element(key, value, timeToIdleSeconds, timeToLiveSeconds));
				cache.put(new Element(CommonConstants.LAST_MODIFIED,DateTime.now()));
				return Mono.just(value);
			} catch (Exception exception) {
				log.error("Error while adding object to local cache {}", exception);
				return Mono.empty();
			}
	}

	public <T> Mono<T> addObject(String key, T value) {
			try {
				log.debug("Adding object for key {}", key );
				cache.put(new Element(key, value));
				cache.put(new Element(CommonConstants.LAST_MODIFIED,DateTime.now()));
				return Mono.just(value);
			} catch (Exception exception) {
				log.error("Error while adding object to local cache {}", exception);
				return Mono.empty();
			}
	}

	@SuppressWarnings("unchecked")
	public <T> Mono<T> getObject(String key) {
			try {
				Element element = cache.get(key);
				if (element == null) {
					return Mono.empty();
				}
				T value = (T) element.getObjectValue();
				return Mono.just(value);
			} catch (Exception exception) {
				log.error("Error while getting data from local cache {}", exception);
				return Mono.empty();
			}
	}

	public boolean removeObject(String key) {
		if (cache != null) {
			try {
				return cache.remove(key);
			} catch (Exception exception) {
				log.error("Error while removing object from local cache {}", exception);
				return false;
			}
		}
		log.warn("cache not available");
		return false;
	}

	@PreDestroy
	public void removeAllObjects() {
		if (cache != null) {
			try {
				cache.removeAll();
			} catch (Exception exception) {
				log.error("Error while removing object from local cache {}", exception);
			}
		} else {
			log.warn("cache not available");
		}
	}

	public boolean isKeyExists(String key) {
		if (cache != null) {
			try {
				return cache.isKeyInCache(key);
			} catch (Exception exception) {
				log.error("Error while checking object from local cache {}", exception);
				return false;
			}
		}
		log.warn("cache not available");
		return false;
	}
}
