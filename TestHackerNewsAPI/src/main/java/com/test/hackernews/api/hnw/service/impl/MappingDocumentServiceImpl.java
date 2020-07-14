package com.test.hackernews.api.hnw.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.error.DocumentAlreadyExistsException;
import com.test.hackernews.api.hnw.model.MappingDocument;
import com.test.hackernews.api.hnw.repository.MappingDocumentRepository;
import com.test.hackernews.api.hnw.service.MappingDocumentService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class MappingDocumentServiceImpl implements MappingDocumentService{
	
	@Autowired
	private MappingDocumentRepository mappingDocumentRepository;

	@Override
	public Mono<MappingDocument> createMappingDocument(String id, List<Integer> collections) {
		MappingDocument mappingDoc = new MappingDocument();
		mappingDoc.setId(id);
		List<Integer> collList = new ArrayList<Integer>();
		collList.addAll(collections);
		mappingDoc.setDocumentCollections(collList);
		return createMappingDocumentIfAbsent(mappingDoc, mappingDoc.getDocumentCollections())
				.doOnError(Exceptions::propagate);
	}

	@Override
	public Mono<MappingDocument> getMappingDocumentById(String mappingId) {
		return mappingDocumentRepository.findById(mappingId);
	}
	
	private Mono<MappingDocument> createMappingDocumentIfAbsent(MappingDocument mappingDocument,
			List<Integer> documentCollections) {
		log.debug("Invoked createMappingDocumentIfAbsent, key : {} and collections : {}", mappingDocument.getId(),
				documentCollections);

		return mappingDocumentRepository.insert(mappingDocument.getId(), mappingDocument, MappingDocument.class)
				.onErrorResume(ex -> {
					log.error("Mapping document exist, documentId : {}", mappingDocument.getId());
					if (ex instanceof DocumentAlreadyExistsException) {
						Integer[] elements = documentCollections.toArray(new Integer[documentCollections.size()]);
						return mappingDocumentRepository
								.prependArray(mappingDocument.getId(), "documentCollections", elements)
								.then(Mono.just(mappingDocument));
					}
					return Mono.just(mappingDocument);
				});
		
	}

}
