package com.test.hackernews.api.hnw.service.impl;

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

import com.couchbase.client.java.error.DocumentAlreadyExistsException;
import com.test.hackernews.api.hnw.model.MappingDocument;
import com.test.hackernews.api.hnw.repository.MappingDocumentRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class MappingDocumentServiceImplTest {

	@InjectMocks
	private MappingDocumentServiceImpl mappingDocumentServiceImpl;
	
	@Mock
	private MappingDocumentRepository mappingDocumentRepository;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testCreateMappingDocument() {
		
		List<Integer> collections = new ArrayList<Integer>();
		collections.add(123);
		MappingDocument mapD = new MappingDocument();
		mapD.setId("124");
		mapD.setDocumentCollections(Arrays.asList(123));
		
		Mockito.when(mappingDocumentRepository.insert(Mockito.anyString(), Mockito.any(),Mockito.any()))
		.thenReturn(Mono.just(mapD));
		
		StepVerifier.create(mappingDocumentServiceImpl.createMappingDocument("124", collections))
		.expectNextMatches(resp -> {
			assertNotNull(resp, "Expect resp not null");
			return true;
		}).expectComplete().verify();
	}
	
	@Test
	public void testCreateMappingDocumentForPrepend() {
		
		List<Integer> collections = new ArrayList<Integer>();
		collections.add(123);
		MappingDocument mapD = new MappingDocument();
		mapD.setId("124");
		mapD.setDocumentCollections(Arrays.asList(123));
		
		Mockito.when(mappingDocumentRepository.insert(Mockito.anyString(), Mockito.any(),Mockito.any()))
		.thenReturn(Mono.error(new DocumentAlreadyExistsException()));
		
		Mockito.when(mappingDocumentRepository.prependArray(Mockito.anyString(), Mockito.any(),Mockito.any()))
		.thenReturn(Mono.just(Boolean.TRUE));
		
		StepVerifier.create(mappingDocumentServiceImpl.createMappingDocument("124", collections))
		.expectNextMatches(resp -> {
			assertNotNull(resp, "Expect resp not null");
			return true;
		}).expectComplete().verify();
	}
	
	
	@Test
	public void testGetMappingDocument() {
		
		List<Integer> collections = new ArrayList<Integer>();
		collections.add(123);
		MappingDocument mapD = new MappingDocument();
		mapD.setId("124");
		mapD.setDocumentCollections(Arrays.asList(123));
		
		Mockito.when(mappingDocumentRepository.findById(Mockito.anyString()))
		.thenReturn(Mono.just(mapD));
		
		StepVerifier.create(mappingDocumentServiceImpl.getMappingDocumentById("124"))
		.expectNextMatches(resp -> {
			assertNotNull(resp, "Expect resp not null");
			return true;
		}).expectComplete().verify();
	}
}
