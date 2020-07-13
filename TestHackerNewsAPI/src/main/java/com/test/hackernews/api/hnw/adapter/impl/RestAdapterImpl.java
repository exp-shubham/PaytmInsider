package com.test.hackernews.api.hnw.adapter.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.test.hackernews.api.hnw.adapter.RestAdapter;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * The Class RestAdapterImpl.
 */
@Component

/** The Constant log. */
@Slf4j
public class RestAdapterImpl implements RestAdapter {

	/** The web client. */
	@Autowired(required = true)
	private WebClient webClient;
	
	@Bean
	public WebClient createWebClient() {
		this.webClient = WebClient.builder().build();
		return this.webClient;
	}

	/**
	 * Gets the object.
	 *
	 * @param <T> the generic type
	 * @param serviceUrl the service url
	 * @param headersMap the headers map
	 * @param type the type
	 * @return the object
	 */
	@Override
	public <T> Mono<T> getObject(String serviceUrl, Map<String, String> headersMap, Class<T> type) {
		String url;
		try {
			url = URLDecoder.decode(serviceUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return Mono.error(e);
		}
		log.debug("Invoking getobject for url {}",url);
		return this.webClient.get().uri(url).headers(this.mapHeaders(headersMap))
				.accept(new MediaType[] { MediaType.APPLICATION_JSON }).retrieve().bodyToMono(type)
				.onErrorResume(err -> {
					log.error("Exception occurred in getObject while fetching {} : error {}", serviceUrl, err);
					return Mono.error(err);
				});
	}

	/**
	 * Map headers.
	 *
	 * @param headersMap the headers map
	 * @return the consumer
	 */
	public Consumer<HttpHeaders> mapHeaders(Map<String, String> headersMap) {
		return h -> headersMap.forEach((k, v) -> h.set(k, v));
	}

}
