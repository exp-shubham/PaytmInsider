package com.test.hackernews.api.hnw.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.test.hackernews.api.hnw.constants.CommonConstants;
import com.test.hackernews.api.hnw.dto.ResponseDto;
import com.test.hackernews.api.hnw.service.HackerNewsService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * The Class HackerNewsHandler.
 */
@Component
@Slf4j
public class HackerNewsHandler {
	
	/** The hacker news service. */
	@Autowired
	private HackerNewsService hackerNewsService;

	/** The not found. */
	private Mono<ServerResponse> notFound = ServerResponse.notFound().build();
	
	private Mono<ServerResponse> badRequest = ServerResponse.badRequest().build();

	/**
	 * Gets the top stories.
	 *
	 * @param serverRequest the server request
	 * @return the top stories
	 */
	public Mono<ServerResponse> getTopStories(ServerRequest serverRequest) {
		return hackerNewsService.getTopStories()
				.map(list -> {
					log.debug("response list {}", list);
					return list;
				})
				.flatMap(stories -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
						.bodyValue(stories))
				.switchIfEmpty(notFound)
				.onErrorResume(err -> {
					return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.bodyValue("Error has occurred "+err.getMessage());
				});
	}

	/**
	 * Gets the comments.
	 *
	 * @param serverRequest the server request
	 * @return the comments
	 */
	public Mono<ServerResponse> getComments(ServerRequest serverRequest) {
		String storyId = serverRequest.pathVariable(CommonConstants.STORY_ID);
		if (StringUtils.isEmpty(storyId)) {
			return badRequest;
		}	
		return hackerNewsService.getComments(storyId)
				.flatMap(comments -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
						.bodyValue(comments))
				.switchIfEmpty(notFound)
				.onErrorResume(err -> {
					return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.bodyValue("Error has occurred "+err.getMessage());
				});
	}

	/**
	 * Gets the past stories.
	 *
	 * @param serverRequest the server request
	 * @return the past stories
	 */
	public Mono<ServerResponse> getPastStories(ServerRequest serverRequest) {
		return hackerNewsService.getPastStories()
				.collectList()
				.map(list -> {
					ResponseDto  response = new ResponseDto();
					response.setCount(list.size());
					response.setItems(list);
					return response;
				})
				.flatMap(stories -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
						.bodyValue(stories))
				.switchIfEmpty(notFound).onErrorResume(err -> {
					return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.bodyValue("Error has occurred "+err.getMessage());
				});
	}

}
