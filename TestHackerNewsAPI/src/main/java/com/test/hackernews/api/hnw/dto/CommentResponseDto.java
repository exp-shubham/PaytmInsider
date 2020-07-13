package com.test.hackernews.api.hnw.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Instantiates a new comment response dto.
 */
@Data
public class CommentResponseDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -621245869066427104L;

	/** The text. */
	private String text;
	
	/** The user handle. */
	private String userHandle;
	
	/** The age. */
	@JsonProperty("age(in days)")
	@SerializedName("age(in days)")
	private String age;

}
