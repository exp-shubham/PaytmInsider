package com.test.hackernews.api.hnw.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.test.hackernews.api.hnw.model.MappingDocument;

import reactor.core.publisher.Mono;

@Service
public interface MappingDocumentService {
	
	Mono<MappingDocument> createMappingDocument(String id, List<Integer> collections);
	
	Mono<MappingDocument> getMappingDocumentById(String mappingId);

}
