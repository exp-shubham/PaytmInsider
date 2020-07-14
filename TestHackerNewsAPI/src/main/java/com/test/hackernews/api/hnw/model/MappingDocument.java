package com.test.hackernews.api.hnw.model;
import java.util.List;

import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Id;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class MappingDocuments.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MappingDocument {
	
	@Id
	private String id;
	
	/** The document collections. */
	private List<Integer> documentCollections;

}
