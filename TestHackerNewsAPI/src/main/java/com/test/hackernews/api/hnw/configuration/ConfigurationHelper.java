package com.test.hackernews.api.hnw.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class ConfigurationHelper {

	@Value("${app.config.zone.id: \"Asia/Kolkata\"}")
	private String zoneId;
	
	@Value("${hackernews.base.uri}")
	private String hackerNewsBaseUri;
	
	@Value("${hackernews.api.version}")
	private String hackerNewsApiVersion;
	
	@Value("${pagination.limit}")
	private int limit;
	
}

