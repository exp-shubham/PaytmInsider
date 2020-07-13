package com.test.hackernews.api.hnw.constants;

import lombok.Data;

/**
 * Instantiates a new common constants.
 * @author shubham.srivastava
 */
@Data
public class CommonConstants {
	
	/** The Constant URI_SEPERATOR. */
	public static final String URI_SEPERATOR = "/";
	
	/** The Constant TOP_STORIES_URI. */
	public static final String TOP_STORIES= "top-stories";
	
	/** The Constant PAST_STORIES_URI. */
	public static final String PAST_STORIES = "past-stories";
	
	/** The Constant COMMENTS_URI. */
	public static final String COMMENTS = "comments";
	
	/** The Constant VERSION_1. */
	public static final String VERSION_1 = "v1";
	
	/** The Constant GET_TOP_STORIES_URI. */
	public static final String GET_TOP_STORIES_URI = new StringBuilder()
			.append(URI_SEPERATOR)
			.append(VERSION_1)
			.append(URI_SEPERATOR)
			.append(TOP_STORIES).toString();
	
	/** The Constant GET_PAST_STORIES_URI. */
	public static final String GET_PAST_STORIES_URI = new StringBuilder()
			.append(URI_SEPERATOR)
			.append(VERSION_1)
			.append(URI_SEPERATOR)
			.append(PAST_STORIES).toString();
	
	/** The Constant START_CURLY_BRACE. */
	public static final String START_CURLY_BRACE = "{";
	
	/** The Constant END_CURLY_BRACE. */
	public static final String END_CURLY_BRACE = "}";
	
	/** The Constant DOC_ID. */
	public static final String DOC_ID = "id";
	
	/** The Constant JSON_PRETTY_URI. */
	public static final String JSON_PRETTY_URI= ".json?print=pretty";

	/** The Constant LAST_MODIFIED. */
	public static final String LAST_MODIFIED = "lastModified";

	/** The Constant STORY_ID. */
	public static final String STORY_ID = "storyId";
	
	/** The Constant GET_COMMENTS_URI. */
	public static final String GET_COMMENTS_URI = new StringBuilder()
			.append(URI_SEPERATOR)
			.append(VERSION_1)
			.append(URI_SEPERATOR)
			.append(COMMENTS)
			.append(URI_SEPERATOR)
			.append(START_CURLY_BRACE)
			.append(STORY_ID).append(END_CURLY_BRACE).toString();
}
