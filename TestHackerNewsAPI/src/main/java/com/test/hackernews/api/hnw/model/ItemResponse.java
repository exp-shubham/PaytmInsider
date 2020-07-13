package com.test.hackernews.api.hnw.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import com.couchbase.client.java.repository.annotation.Field;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Instantiates a new item response dto.
 */
@Data
@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemResponse implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1793623563794394853L;

	/** The author. */
	@JsonProperty("by")
	@Field("by")
	@SerializedName("by")
	private String author;
	
	/** The time. */
	private long time;
	
	/** The url. */
	private String url;
	
	/** The score. */
	private Integer score;
	
	/** The title. */
	private String title;
	
	/** The id. */
	@JsonIgnore
	@Id
	private Integer id;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemResponse other = (ItemResponse) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}
}
