package com.test.hackernews.api.hnw.helper;

import java.util.StringJoiner;

import org.springframework.stereotype.Component;

@Component
public class Helper {

	public String prepareItemKey(String author, String title) {
		return new StringJoiner("::")
				.add(author)
				.add(title)
				.toString();
	}
}
