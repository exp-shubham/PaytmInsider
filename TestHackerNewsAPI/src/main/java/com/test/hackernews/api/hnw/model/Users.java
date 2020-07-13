package com.test.hackernews.api.hnw.model;

import java.util.List;

import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * Instantiates a new users.
 */
@Data
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class Users {

	/** The id. */
	@Id
	private String id;
	
	/** The about. */
	private String about;
	
	/** The delay. */
	private long delay;
	
	/** The created. */
	private long created;
	
	/** The karma. */
	private Integer karma;
	
	/** The submitted. */
	private List<Integer> submitted;
}
