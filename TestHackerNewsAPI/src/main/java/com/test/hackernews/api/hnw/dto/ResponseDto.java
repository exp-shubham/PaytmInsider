package com.test.hackernews.api.hnw.dto;

import java.util.List;

import com.test.hackernews.api.hnw.model.ItemResponse;

import lombok.Data;

/**
 * Instantiates a new response dto.
 */
@Data
public class ResponseDto {

	/** The count. */
	private int count;
	
	/** The items. */
	private List<ItemResponse> items;
	
}
