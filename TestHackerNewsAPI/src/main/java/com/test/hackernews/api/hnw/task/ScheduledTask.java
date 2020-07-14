package com.test.hackernews.api.hnw.task;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.test.hackernews.api.hnw.adapter.RestAdapter;
import com.test.hackernews.api.hnw.configuration.ConfigurationHelper;
import com.test.hackernews.api.hnw.constants.CommonConstants;
import com.test.hackernews.api.hnw.helper.EhCacheHelper;
import com.test.hackernews.api.hnw.model.ItemResponse;
import com.test.hackernews.api.hnw.model.Items;
import com.test.hackernews.api.hnw.repository.ItemsRepository;
import com.test.hackernews.api.hnw.service.MappingDocumentService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@EnableAsync
@Component
@Slf4j
public class ScheduledTask {

	/** The config helper. */
	@Autowired
	private ConfigurationHelper configHelper;
	
	/** The rest adapter. */
	@Autowired
	private RestAdapter restAdapter;
	
	/** The eh cache helper. */
	@Autowired
	private EhCacheHelper ehCacheHelper;
	
	@Autowired
	private MappingDocumentService mappingService;
	
	@Autowired
	private ItemsRepository itemsRepository;
	
	/**
	 * Update top stories in db.
	 */
	@Scheduled(fixedDelay =580000 )
	@Async
	public void updateTopStoriesInDb() {
		log.debug("in updateTopStoriesInDb ");
		getTopStories()
		.flatMap(items -> ehCacheHelper.addObject(CommonConstants.TOP_STORIES, items, 600))
		.subscribe();
	}
	
	/**
	 * Gets the top stories.
	 *
	 * @return the top stories
	 */
	private Mono<List<ItemResponse>> getTopStories() {
		String getStoriesUri =  buildGetStoriesUri("topstories");
		log.debug("in getTopStories , {}", getStoriesUri);
		Map<String,String> headersMap = new HashMap<>();
		return restAdapter.getObject(getStoriesUri, headersMap, Integer[].class)
				.flatMap(arr -> {
					
				return	ehCacheHelper.addObject("allStories", arr)
						.flatMap(array -> 
						mappingService.createMappingDocument(CommonConstants.PAST_STORIES, Arrays.asList(array)))
						.thenReturn(arr);
				})
				.flatMap(arr -> getItemDetails(arr, headersMap))
				.map(itemList -> {
					List<ItemResponse> itemListResponse = itemList.stream()
							.filter(item -> "story".equalsIgnoreCase(item.getType()))
							.sorted(Comparator.comparing(Items::getTime).reversed())
							 .limit(configHelper.getLimit())
							 .sorted(Comparator.comparing(Items::getScore).reversed())
							 .map(item -> {
									ItemResponse itemResponse=  new ItemResponse();
									BeanUtils.copyProperties(item, itemResponse);
										ehCacheHelper.addObject(String.valueOf(itemResponse.hashCode()),
												itemResponse)
										.subscribe();
									return itemResponse;
								})
							 .collect(Collectors.toList());
					
					return itemListResponse;
				}).doOnError(err -> {
					log.error("error {}" , err);
				});
	}

	/**
	 * Builds the get stories uri.
	 *
	 * @param param the param
	 * @return the string
	 */
	private String buildGetStoriesUri(String param) {
		
		return new StringBuilder(configHelper.getHackerNewsBaseUri())
				.append(configHelper.getHackerNewsApiVersion())
				.append(CommonConstants.URI_SEPERATOR)
				.append(param)
				.append(CommonConstants.JSON_PRETTY_URI)
				.toString();
	}
	
	/**
	 * Gets the item details.
	 *
	 * @param itemList the item list
	 * @param headersMap the headers map
	 * @return the item details
	 */
	private Mono<List<Items>> getItemDetails(Integer[] itemList,Map<String,String> headersMap){
		return Flux.fromArray(itemList)
		.map(itemId -> {
			String param = new StringBuilder("item")
					.append(CommonConstants.URI_SEPERATOR)
					.append(itemId).toString();
			return buildGetStoriesUri(param);
		}).flatMap(getItemUri -> restAdapter.getObject(getItemUri, headersMap, Items.class))
		.flatMap(item -> itemsRepository.upsert(String.valueOf(item.getId()), item, Items.class))
		.collectList()
		.map(list -> {
			ehCacheHelper.addObject("allItems", list)
			.subscribe();
			return list;
		});
		
	}

}
