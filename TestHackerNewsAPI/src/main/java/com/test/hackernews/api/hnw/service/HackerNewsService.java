package com.test.hackernews.api.hnw.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.test.hackernews.api.hnw.dto.CommentResponseDto;
import com.test.hackernews.api.hnw.model.ItemResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The Interface HackerNewsService.
 */
@Service
public interface HackerNewsService {
	
	/**
	 * Gets the top stories.
	 *
	 * @return the top stories
	 */
	Mono<List<ItemResponse>> getTopStories();
	
	/**
	 * Gets the past stories.
	 *
	 * @return the past stories
	 */
	Flux<ItemResponse> getPastStories();
	
	/**
	 * Gets the comments.
	 *
	 * @return the comments
	 */
	Mono<List<CommentResponseDto>> getComments(String storyId);

}
