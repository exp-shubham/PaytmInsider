package com.test.hackernews.api.hnw.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.test.hackernews.api.hnw.adapter.RestAdapter;
import com.test.hackernews.api.hnw.configuration.ConfigurationHelper;
import com.test.hackernews.api.hnw.helper.EhCacheHelper;
import com.test.hackernews.api.hnw.model.Items;
import com.test.hackernews.api.hnw.model.MappingDocument;
import com.test.hackernews.api.hnw.repository.ItemsRepository;
import com.test.hackernews.api.hnw.service.MappingDocumentService;

import reactor.core.publisher.Mono;

public class ScheduledTaskTest {
	
	@InjectMocks
	private ScheduledTask scheduledTask;
	
	@Mock
	private EhCacheHelper ehCacheHelper;
	
	@Mock
	private ConfigurationHelper configHelper;
	
	/** The rest adapter. */
	@Mock
	private RestAdapter restAdapter;
	
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
	public void testUpdateTopStories() {
		Mockito.when(ehCacheHelper.getObject(Mockito.anyString()))
		.thenReturn(Mono.empty());
		Mockito.when(restAdapter.getObject(Mockito.anyString(), Mockito.any(), Mockito.same(Integer[].class)))
		.thenReturn(Mono.just(new Integer[] {123, 234}));
		Integer [] arr = new Integer[] {123, 234};
		Mockito.when(ehCacheHelper.addObject(Mockito.anyString(), Mockito.any(), Mockito.anyInt()))
		.thenReturn(Mono.just(arr));
		Mockito.when(ehCacheHelper.addObject(Mockito.anyString(), Mockito.any()))
		.thenReturn(Mono.just(arr));
		Mockito.when(mappingService.createMappingDocument(Mockito.anyString(), Mockito.any()))
		.thenReturn(Mono.just(new MappingDocument()));
		
		Items item = new Items();
		item.setId(123);
		item.setScore(9);
		item.setTime(System.currentTimeMillis());
		item.setType("story");
		Mockito.when(restAdapter.getObject(Mockito.anyString(), Mockito.any(), Mockito.same(Items.class)))
		.thenReturn(Mono.just(item));
		Mockito.when(itemsRepository.upsert(Mockito.anyString(), Mockito.any(), Mockito.same(Items.class)))
		.thenReturn(Mono.just(item));
		
		scheduledTask.updateTopStoriesInDb();
	}

}
