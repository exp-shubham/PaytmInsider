package com.test.hackernews.api.hnw.service.impl;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.hackernews.api.hnw.adapter.RestAdapter;
import com.test.hackernews.api.hnw.configuration.ConfigurationHelper;
import com.test.hackernews.api.hnw.constants.CommonConstants;
import com.test.hackernews.api.hnw.dto.CommentResponseDto;
import com.test.hackernews.api.hnw.helper.EhCacheHelper;
import com.test.hackernews.api.hnw.model.ItemResponse;
import com.test.hackernews.api.hnw.model.Items;
import com.test.hackernews.api.hnw.model.MappingDocument;
import com.test.hackernews.api.hnw.model.Users;
import com.test.hackernews.api.hnw.repository.ItemsRepository;
import com.test.hackernews.api.hnw.service.HackerNewsService;
import com.test.hackernews.api.hnw.service.MappingDocumentService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The Class HackerNewsServiceImpl.
 */
@Service

/** The Constant log. */
@Slf4j
public class HackerNewsServiceImpl implements HackerNewsService {

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
	 * Gets the top stories.
	 *
	 * @return the top stories
	 */
	public Mono<List<ItemResponse>> getTopStories() {
		return checkValidCache().flatMap(time -> extractTopStoriesFromCache())
				.switchIfEmpty(Mono.defer(() -> fetchTopStories()));
				
	}



	/**
	 * Check valid cache.
	 *
	 * @return the mono
	 */
	private Mono<Object> checkValidCache() {
		return ehCacheHelper.getObject(CommonConstants.LAST_MODIFIED).filter(time -> {
			DateTime currentTime = DateTime.now();
			DateTime lastModified = (DateTime) time;
			log.debug("currentTime {} and lastModified {} ", currentTime, lastModified);
			int diff = Minutes.minutesBetween(currentTime, lastModified).getMinutes();
			return diff < 10;
		});
	}

	/**
	 * Gets the top stories.
	 *
	 * @return the top stories
	 */
	private Mono<List<ItemResponse>> fetchTopStories() {
		Map<String, String> headersMap = new HashMap<>();
		return fetchAllItemsArr(headersMap).flatMap(arr -> {
			return ehCacheHelper.getObject(CommonConstants.ALLSTORIES).filter(stories -> { 
				log.debug("arr {} and stories {}" , arr, stories );
				return stories.equals(arr);
			}).flatMap(stories -> extractTopStoriesFromCache())
			.switchIfEmpty(Mono.defer(() -> getItemList(arr, headersMap)));
		});

	}

	/**
	 * Fetch all items arr.
	 *
	 * @param headersMap the headers map
	 * @return the mono
	 */
	private Mono<Integer[]> fetchAllItemsArr(Map<String, String> headersMap) {
		String getStoriesUri = buildGetItemUri(CommonConstants.TOPSTORIES_CONSTANT);
		log.debug("Invoked fetchTopStories for uri {}", getStoriesUri);

		return restAdapter.getObject(getStoriesUri, headersMap, Integer[].class)
				.flatMap(arr -> {
					return mappingService.createMappingDocument(CommonConstants.PAST_STORIES, Arrays.asList(arr))
							.thenReturn(arr);
				});
	}

	/**
	 * Extract top stories from cache.
	 *
	 * @return the mono
	 */
	private Mono<List<ItemResponse>> extractTopStoriesFromCache() {
		log.debug("Invoked extractTopStoriesFromCache ");
		return ehCacheHelper.getObject(CommonConstants.TOP_STORIES).map(obj -> {
			ObjectMapper objMapper = new ObjectMapper();
			
			ItemResponse[] itemList = objMapper.convertValue(obj, ItemResponse[].class);
			return Arrays.stream(itemList)
					.collect(Collectors.toList());
		}).switchIfEmpty(Mono.empty()).doOnError(Exceptions::propagate);
	}

	/**
	 * Gets the item list.
	 *
	 * @param itemArr    the item arr
	 * @param headersMap the headers map
	 * @return the item list
	 */
	private Mono<List<ItemResponse>> getItemList(Integer[] itemArr, Map<String, String> headersMap) {
		log.debug("Inovked getItemList");
		return ehCacheHelper.addObject(CommonConstants.ALLSTORIES, itemArr, 600).thenReturn(itemArr)
				.flatMap(arr -> getItemDetails(Arrays.asList(arr), headersMap))
				.map(itemList -> {
					return prepareSortedItemResponse(itemList);
				});
	}



	private List<ItemResponse> prepareSortedItemResponse(List<Items> itemList) {
		List<ItemResponse> itemListResponse = itemList.stream()
				.filter(item -> CommonConstants.STORY.equalsIgnoreCase(item.getType()))
				.sorted(Comparator.comparing(Items::getTime).reversed()).limit(configHelper.getLimit())
				.sorted(Comparator.comparing(Items::getScore).reversed()).map(item -> {
					ItemResponse itemResponse = new ItemResponse();
					BeanUtils.copyProperties(item, itemResponse);
					ehCacheHelper.addObject(String.valueOf(itemResponse.hashCode()),
							itemResponse)
					.subscribe();
					return itemResponse;
				}).collect(Collectors.toList());

		return itemListResponse;
	}

	/**
	 * Builds the get stories uri.
	 *
	 * @param param the param
	 * @return the string
	 */
	private String buildGetItemUri(String param) {

		return new StringBuilder(configHelper.getHackerNewsBaseUri()).append(configHelper.getHackerNewsApiVersion())
				.append(CommonConstants.URI_SEPERATOR).append(param).append(CommonConstants.JSON_PRETTY_URI).toString();
	}

	/**
	 * Gets the item details.
	 *
	 * @param itemList   the item list
	 * @param headersMap the headers map
	 * @return the item details
	 */
	private Mono<List<Items>> getItemDetails(List<Integer> itemList, Map<String, String> headersMap) {
		log.debug("Invoked getItemDetails.");
		return Flux.fromIterable(itemList).map(itemId -> {
			String param = new StringBuilder(CommonConstants.ITEM).append(CommonConstants.URI_SEPERATOR).append(itemId).toString();
			return buildGetItemUri(param);
		}).flatMap(getItemUri -> restAdapter.getObject(getItemUri, headersMap, Items.class))
				.flatMap(item -> itemsRepository.upsert(String.valueOf(item.getId()), item, Items.class)).collectList();

	}

	/**
	 * Gets the past stories.
	 *
	 * @return the past stories
	 */
	@Override
	public Flux<Items> getPastStories() {
	return	mappingService.getMappingDocumentById(CommonConstants.PAST_STORIES)
		.filter(mappingDoc -> CollectionUtils.isNotEmpty(mappingDoc.getDocumentCollections()))
		.map(MappingDocument::getDocumentCollections)
		.map(LinkedHashSet::new)
		.flatMapMany(ids -> itemsRepository.findAllById(ids, Items.class));
		
	}

	/**
	 * Gets the comments.
	 *
	 * @param storyId the story id
	 * @return the comments
	 */
	@Override
	public Mono<List<CommentResponseDto>> getComments(String storyId) {
		Map<String,String> headersMap = new HashMap<>();
		return getItemDetails(Arrays.asList(Integer.parseInt(storyId)), headersMap)
				.filter(CollectionUtils::isNotEmpty )
				.flatMap(str -> {
					if (CollectionUtils.isEmpty(str.get(0).getKids())) {
						return Mono.empty();
					}
					return Mono.just(str.get(0).getKids());
				})
		.flatMap(list -> getItemDetails(list, headersMap))		
		.flatMap(items -> fetchUserAndPrepareResponse(items));
	}

	/**
	 * Fetch user and prepare response.
	 *
	 * @param items the items
	 * @return the mono<? extends list< comment response dto>>
	 */
	private Mono<? extends List<CommentResponseDto>> fetchUserAndPrepareResponse(List<Items> items) {
		List<Items> sortedList = items.stream().map(item -> {
			item.setTotalComments(CollectionUtils.size(item.getKids())+1);
			return item;
		}).sorted(Comparator.comparing(Items::getTotalComments).reversed())
		.limit(10).collect(Collectors.toList());
		
		return Flux.fromIterable(sortedList)
				.flatMapSequential(item -> {
					log.debug("total Comments for item {} {} ", item.getAuthor(), item.getTotalComments());
					return getUserDetails(item.getAuthor())
							.map(user -> {
								return prepareCommentResponse(item, user);
								
							});
				}).switchIfEmpty(Mono.just(new CommentResponseDto()))
				.collectList();
	}

	/**
	 * Prepare comment response.
	 *
	 * @param item the item
	 * @param user the user
	 * @return the comment response dto
	 */
	private CommentResponseDto prepareCommentResponse( Items item, Users user) {
		CommentResponseDto respDto = new CommentResponseDto();
		respDto.setText(item.getText());
		respDto.setUserHandle(user.getId());
		LocalDate current = LocalDate.now();
		LocalDate createdDate = LocalDate.ofEpochDay(user.getCreated());
		Period period = Period.between(current,createdDate);
		long age = period.get(ChronoUnit.DAYS);
		respDto.setAge(String.valueOf(age));
		return respDto;
	}
	
	/**
	 * Gets the user details.
	 *
	 * @param userId the user id
	 * @return the user details
	 */
	private Mono<Users> getUserDetails(String userId) {
		String param = new StringBuilder(CommonConstants.USER).append(CommonConstants.URI_SEPERATOR).append(userId).toString();
		String getUserDetailsUri = buildGetItemUri(param);
		return restAdapter.getObject(getUserDetailsUri, new HashMap<String, String>(), Users.class);
	}


}
