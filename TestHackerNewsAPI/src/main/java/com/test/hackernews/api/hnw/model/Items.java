package com.test.hackernews.api.hnw.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Instantiates a new items.
 */
@Data
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class Items implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8506213730498252346L;

	/** The id. */
	@Id
	private Integer id;
	
	/** The deleted. */
	private boolean deleted;
	
	/** The type. */
	private String type;
	
	/** The author. */
	@JsonProperty("by")
	@Field("by")
	@SerializedName("by")
	private String author;
	
	/** The time. */
	private long time;
	
	/** The text. */
	private String text;
	
	/** The dead. */
	private boolean dead;
	
	/** The parent. */
	private Integer parent;
	
	/** The poll. */
	private String poll;
	
	/** The kids. */
	private List<Integer> kids;
	
	/** The url. */
	private String url;
	
	/** The score. */
	private Integer score;
	
	/** The title. */
	private String title;
	
	/** The parts. */
	private List<Integer> parts;
	
	/** The descendants. */
	private Integer descendants;
	
	private Integer totalComments;
	
}
