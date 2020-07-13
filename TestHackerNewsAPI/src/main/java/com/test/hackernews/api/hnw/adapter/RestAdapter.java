package com.test.hackernews.api.hnw.adapter;

import java.util.Map;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public interface RestAdapter {
	
	<T> Mono<T> getObject(String serviceUrl, Map<String, String> headersMap, Class<T> type);	

}
