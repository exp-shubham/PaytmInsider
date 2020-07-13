package com.test.hackernews.api.hnw.handler;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.test.hackernews.api.hnw.dto.CommentResponseDto;
import com.test.hackernews.api.hnw.model.ItemResponse;
import com.test.hackernews.api.hnw.service.HackerNewsService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class HackerNewsHandlerTest {
	
	@InjectMocks
	private HackerNewsHandler hackerNewsHandler;
	
	@Mock
	private HackerNewsService hackerNewsService;
	
	@Mock
	private ServerRequest serverRequest;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}	
	
	@Test
	public void testTopStories() {
		ItemResponse  itemResponse = new ItemResponse();
		itemResponse.setId(123);
		List<ItemResponse> respList = new ArrayList<ItemResponse>();
		respList.add(itemResponse);
		Mockito.when(hackerNewsService.getTopStories())
		.thenReturn(Mono.just(respList));
		StepVerifier.create(hackerNewsHandler.getTopStories(serverRequest))
		.expectNextMatches(res -> {
			assertNotNull(res, "Expecting res not null ");
			return true;
		}).expectComplete().verify();
	}
	
	@Test
	public void testTopStoriesForError() {
		Mockito.when(hackerNewsService.getTopStories())
		.thenReturn(Mono.error(new RuntimeException()));
		StepVerifier.create(hackerNewsHandler.getTopStories(serverRequest))
		.expectNextMatches(res -> {
			assertNotNull(res, "Expecting res not null ");
			return true;
		}).expectComplete().verify();
	}
	
	@Test
	public void testPastStories() {
		ItemResponse  itemResponse= new ItemResponse();
		itemResponse.setId(123);
		Mockito.when(hackerNewsService.getPastStories())
		.thenReturn(Flux.just(itemResponse));
		StepVerifier.create(hackerNewsHandler.getPastStories(serverRequest))
		.expectNextMatches(res -> {
			assertNotNull(res, "Expecting res not null ");
			return true;
		}).expectComplete().verify();
	}
	
	@Test
	public void testPastStoriesForError() {
	
		Mockito.when(hackerNewsService.getPastStories())
		.thenReturn(Flux.error(new RuntimeException()));
		StepVerifier.create(hackerNewsHandler.getPastStories(serverRequest))
		.expectNextMatches(res -> {
			assertNotNull(res.statusCode(), "Expecting res not null ");
			return true;
		}).expectComplete().verify();
	}
	
	@Test
	public void testComments() {
		Mockito.when(serverRequest.pathVariable(Mockito.anyString()))
		.thenReturn("124");
		CommentResponseDto  itemResponse = new CommentResponseDto();
		itemResponse.setUserHandle("jl");
		itemResponse.setText("hahha");
		itemResponse.setAge("24");
		
		Mockito.when(hackerNewsService.getComments(Mockito.anyString()))
		.thenReturn(Mono.just(Arrays.asList(itemResponse)));
		StepVerifier.create(hackerNewsHandler.getComments(serverRequest))
		.expectNextMatches(res -> {
			assertNotNull(res, "Expecting res not null ");
			return true;
		}).expectComplete().verify();
	}
	
	@Test
	public void testCommentsForBlankStoryId() {
		Mockito.when(serverRequest.pathVariable(Mockito.anyString()))
		.thenReturn(" ");
		StepVerifier.create(hackerNewsHandler.getComments(serverRequest))
		.expectNextMatches(res -> {
			assertNotNull(res, "Expecting res not null ");
			return true;
		}).expectComplete().verify();
	}
	
	@Test
	public void testCommentsForError() {
		Mockito.when(serverRequest.pathVariable(Mockito.anyString()))
		.thenReturn("323");
		Mockito.when(hackerNewsService.getComments(Mockito.anyString()))
		.thenReturn(Mono.error(new RuntimeException()));
		StepVerifier.create(hackerNewsHandler.getComments(serverRequest))
		.expectNextMatches(res -> {
			assertNotNull(res, "Expecting res not null ");
			return true;
		}).expectComplete().verify();
	}
	
	

}
