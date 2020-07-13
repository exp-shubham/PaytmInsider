package com.test.hackernews.api.hnw.dto;

import java.util.List;

import com.test.hackernews.api.hnw.model.ItemResponse;

import lombok.Data;

@Data
public class ResponseDto {

	private int count;
	private List<ItemResponse> items;
	
}
