package com.test.hackernews.api.hnw.repository;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.util.ReactiveWrapperConverters;

import com.couchbase.client.core.CouchbaseException;
import com.couchbase.client.core.message.ResponseStatus;
import com.couchbase.client.core.message.kv.subdoc.multi.Mutation;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.error.DocumentAlreadyExistsException;
import com.couchbase.client.java.error.subdoc.MultiMutationException;
import com.couchbase.client.java.error.subdoc.PathNotFoundException;
import com.couchbase.client.java.subdoc.DocumentFragment;
import com.test.hackernews.api.hnw.utils.MapperUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Observable;

/**
 * The Interface BaseRepository.
 *
 * @param <T>
 *          the generic type
 * @param <ID>
 *          the generic type
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable>
    extends ReactiveCouchbaseRepository<T, ID> {

  Logger log = LoggerFactory.getLogger(BaseRepository.class);

  /**
   * Find by id .
   *
   * @param <CT>
   *          the generic type
   * @param id
   *          the id
   * @param clazz
   *          the clazz
   * @return the mono
   */
  @SuppressWarnings("unchecked")
  default <CT> Mono<CT> findById(ID id, Class<CT> clazz) {
    Observable<CT> observable = getCouchbaseOperations().getCouchbaseBucket()
        .async()
        .get(id.toString(), RawJsonDocument.class)
        .map(rawJsonDoc -> rawJsonDoc.content())
        .map(rawJsonString -> MapperUtil.createObjectfromJson(rawJsonString, clazz));

    return ReactiveWrapperConverters.toWrapper(observable, Mono.class)
        .onErrorResume(throwable -> {
          log.error("base repository : error while findById id : {} and class: {} and error :: {}",
              id, clazz.getName(), throwable);
          if (throwable instanceof NullPointerException) {
            return Mono.empty();
          }
          return Mono.just(throwable);
        });
  }

  /**
   * Find all by id.
   *
   * @param <CT>
   *          the generic type
   * @param ids
   *          the ids
   * @param clazz
   *          the clazz
   * @return the flux
   */
  default <CT> Flux<CT> findAllById(Iterable<ID> ids, Class<CT> clazz) {
    return Flux.fromIterable(ids)
        .flatMap(id -> findById(id, clazz));
  }

  /**
   * Prepend array.
   *
   * @param docId
   *          the doc id
   * @param path
   *          the path
   * @param elements
   *          the elements
   * @return the mono
   */
  @SuppressWarnings({ "unchecked" })
  default Mono<Boolean> prependArray(String docId, String path, Integer[] elements) {

    Observable<Boolean> observable = getCouchbaseOperations().getCouchbaseBucket()
        .async()
        .mutateIn(docId)
        .arrayPrependAll(path, elements)
        .execute()
        .flatMap(t -> Observable.just(true));
    return ReactiveWrapperConverters.toWrapper(observable, Mono.class)
        .onErrorResume(throwable -> {
          if (throwable instanceof MultiMutationException 
        		  && ((MultiMutationException) throwable).getCause() instanceof PathNotFoundException) {
        	  return prependtoMap(docId, path, Arrays.asList(elements));
            }
          log.error(
                  "base repository : error while prependArray docId :: {} and path :: {} and elements :: {} and error :: {}",
                  docId, path, Arrays.toString(elements), throwable);
          if (throwable instanceof NullPointerException) {
            return Mono.empty();
          }
          return Mono.just(throwable);
        });
  }

  /**
   * Replace array.
   *
   * @param docId
   *          the doc id
   * @param path
   *          the path
   * @param list
   *          the list
   * @return the mono
   */
  @SuppressWarnings({ "unchecked" })
  default Mono<Boolean> replaceArray(String docId, String path, List<String> list) {

    Observable<Boolean> observable = getCouchbaseOperations().getCouchbaseBucket()
        .async()
        .mutateIn(docId)
        .replace(path, list)
        .execute()
        .flatMap(mutation -> {
          if (mutation.status(0) == ResponseStatus.SUCCESS) {
            return Observable.just(Boolean.TRUE);
          } else {
            throw new CouchbaseException(mutation.status(0)
                .toString());
          }
        });
    return ReactiveWrapperConverters.toWrapper(observable, Mono.class)
        .onErrorResume(throwable -> {
          log.error(
              "base repository : error while replaceArray docId :: {} and path :: {} and list :: {} and error :: {}",
              docId, path, list, throwable);
          if (throwable instanceof NullPointerException) {
            return Mono.empty();
          }
          return Mono.just(throwable);
        });
  }

  /**
   * Insert.
   *
   * @param <CT>
   *          the generic type
   * @param id
   *          the id
   * @param element
   *          the element
   * @param clazz
   *          the clazz
   * @return the mono
   */
  @SuppressWarnings({ "unchecked" })
  default <CT> Mono<CT> insert(String id, CT element, Class<CT> clazz) {

    RawJsonDocument document = RawJsonDocument.create(id, MapperUtil.createJsonfromObject(element));
    Observable<CT> observable = getCouchbaseOperations().getCouchbaseBucket()
        .async()
        .insert(document)
        .map(rawJsonDoc -> rawJsonDoc.content())
        .map(rawJsonString -> MapperUtil.createObjectfromJson(rawJsonString, clazz));

    return ReactiveWrapperConverters.toWrapper(observable, Mono.class)
        .onErrorResume(throwable -> {
        
          if (throwable instanceof DocumentAlreadyExistsException) {
            return Mono.error(new DocumentAlreadyExistsException());
          } else if (throwable instanceof NullPointerException) {
            return Mono.empty();
          }
          return Mono.just(throwable);
        });
  }
  
  @SuppressWarnings({ "unchecked" })
  default <CT> Mono<CT> upsert(String id, CT element, Class<CT> clazz) {

    RawJsonDocument document = RawJsonDocument.create(id, MapperUtil.createJsonfromObject(element));
    Observable<CT> observable = getCouchbaseOperations().getCouchbaseBucket()
        .async()
        .upsert(document)
        .map(rawJsonDoc -> rawJsonDoc.content())
        .map(rawJsonString -> MapperUtil.createObjectfromJson(rawJsonString, clazz));

    return ReactiveWrapperConverters.toWrapper(observable, Mono.class)
        .onErrorResume(throwable -> {
        
          if (throwable instanceof DocumentAlreadyExistsException) {
            return Mono.error(new DocumentAlreadyExistsException());
          } else if (throwable instanceof NullPointerException) {
            return Mono.empty();
          }
          return Mono.just(throwable);
        });
  }

  /**
   * Prepend to Map.
   *
   * @param docId
   *          the doc id
   * @param path
   *          the path
   * @param elements
   *          the elements
   * @return the mono
   */
  @SuppressWarnings({ "unchecked" })
  default Mono<Boolean> prependtoMap(String docId, String path, List <Integer> elements) {

	log.debug("prependtoMap params>> docId : {} , path : {} , elements : {} ",docId, path, elements);  
    Observable<DocumentFragment<Mutation>> observable = getCouchbaseOperations()
        .getCouchbaseBucket()
        .async()
        .mutateIn(docId)
        .upsert(path, elements)
        .execute();
    return ReactiveWrapperConverters.toWrapper(observable, Mono.class)
        .onErrorResume(throwable -> {
          log.error(
              "base repository : error while prependtoMap docId :: {} and path :: {} and elements :: {} and error :: {}",
              docId, path, elements, throwable);
          if (throwable instanceof NullPointerException) {
            return Mono.empty();
          }
          return Mono.just(throwable);
        });
  }
  
  /**
   * Prepend to Map.
   *
   * @param docId
   *          the doc id
   * @param path
   *          the path
   * @return the mono
   */
  @SuppressWarnings({ "unchecked" })
  default Mono<Boolean> removeFromMap(String docId, String path) {

    Observable<DocumentFragment<Mutation>> observable = getCouchbaseOperations()
        .getCouchbaseBucket()
        .async()
        .mutateIn(docId)
        .remove(path)
        .execute();
    return ReactiveWrapperConverters.toWrapper(observable, Mono.class)
        .onErrorResume(throwable -> {
          log.error(
              "base repository : error while removeFromMap docId :: {} and path :: {} and error :: {}",
              docId, path, throwable);
          if (throwable instanceof NullPointerException) {
            return Mono.empty();
          }
          return Mono.just(throwable);
        });
  }

}

