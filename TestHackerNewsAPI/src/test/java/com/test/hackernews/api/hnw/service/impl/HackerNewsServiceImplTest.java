package com.test.hackernews.api.hnw.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.test.hackernews.api.hnw.adapter.RestAdapter;
import com.test.hackernews.api.hnw.configuration.ConfigurationHelper;
import com.test.hackernews.api.hnw.helper.EhCacheHelper;
import com.test.hackernews.api.hnw.model.Items;
import com.test.hackernews.api.hnw.model.Users;
import com.test.hackernews.api.hnw.repository.ItemsRepository;
import com.test.hackernews.api.hnw.service.MappingDocumentService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class HackerNewsServiceImplTest {

	@InjectMocks
	private HackerNewsServiceImpl hackerNewsService;
	
	@Mock
	private ConfigurationHelper configHelper;

	/** The rest adapter. */
	@Mock
	private RestAdapter restAdapter;

	/** The eh cache helper. */
	@Mock
	private EhCacheHelper ehCacheHelper;
	
	@Mock
	private MappingDocumentService mappingService;
	
	@Mock
	private ItemsRepository itemsRepository;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(configHelper.getLimit()).thenReturn(10);
		Mockito.when(configHelper.getHackerNewsBaseUri()).thenReturn("abc");
		Mockito.when(configHelper.getHackerNewsApiVersion()).thenReturn("abc");
	}
	
	@Test
	public void testGetTopStories() {
		Mockito.when(ehCacheHelper.getObject(Mockito.anyString()))
		.thenReturn(Mono.empty());
		Mockito.when(restAdapter.getObject(Mockito.anyString(), Mockito.any(), Mockito.same(Integer[].class)))
		.thenReturn(Mono.just(new Integer[] {123, 234}));
		Mockito.when(ehCacheHelper.addObject(Mockito.anyString(), Mockito.any(), Mockito.anyInt()))
		.thenReturn(Mono.just(Boolean.TRUE));
		Mockito.when(ehCacheHelper.addObject(Mockito.anyString(), Mockito.any()))
		.thenReturn(Mono.just(Boolean.TRUE));
		Items item = new Items();
		item.setId(123);
		item.setScore(9);
		item.setTime(System.currentTimeMillis());
		item.setType("story");
		Mockito.when(restAdapter.getObject(Mockito.anyString(), Mockito.any(), Mockito.same(Items.class)))
		.thenReturn(Mono.just(item));
		
		StepVerifier.create(hackerNewsService.getTopStories())
		.expectNextMatches(resp -> {
			assertNotNull(resp, "Response not null");
			return true;
		}).expectComplete().verify();
	}
	
	@Test
	public void testGetComments() {
		
		Items item = new Items();
		item.setId(123);
		item.setScore(9);
		item.setTime(System.currentTimeMillis());
		item.setType("story");
		item.setAuthor("jl");
		item.setKids(Arrays.asList(234));
		Mockito.when(restAdapter.getObject(Mockito.anyString(), Mockito.any(), Mockito.same(Items.class)))
		.thenAnswer(new Answer<Mono<Items>>() {
			int count=0;
			@Override
			public Mono<Items> answer(InvocationOnMock invocation) throws Throwable {
				if  (count==0) {
					count++;
					return Mono.just(item);
				}
				item.setType("comment");
				return Mono.just(item);
			}
		});
		Users user = new Users();
		user.setId("jl");
		user.setCreated(123456789);
		Mockito.when(restAdapter.getObject(Mockito.anyString(), Mockito.any(), Mockito.same(Users.class)))
		.thenReturn(Mono.just(user));
		
		StepVerifier.create(hackerNewsService.getComments("123"))
		.expectNextMatches(resp -> {
			assertNotNull(resp, "Response not null");
			return true;
		}).expectComplete().verify();
		
	}
	
}
