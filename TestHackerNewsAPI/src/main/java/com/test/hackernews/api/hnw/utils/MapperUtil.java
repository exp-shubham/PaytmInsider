package com.test.hackernews.api.hnw.utils;

import java.io.Reader;
import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;

public class MapperUtil {
	private static final Gson gson = (new GsonBuilder()).disableHtmlEscaping()
			.setDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00").create();

	private MapperUtil() {
		throw new UnsupportedOperationException("Cannot instantiate private constructor of MapperUtil");
	}

	public static <T> T createObjectfromFile(Reader read, Class<T> clazz) {
		return gson.fromJson(read, clazz);
	}

	public static <T> T createObjectfromJson(String json, Class<T> classOfT) {
		return StringUtils.isBlank(json) ? null : gson.fromJson(json, classOfT);
	}

	public static String createJsonfromObject(Object src) {
		return gson.toJson(src);
	}

	public static <T> T createTypefromJson(String json, Type typeOfT) {
		return StringUtils.isBlank(json) ? null : gson.fromJson(json, typeOfT);
	}

	public static <T> JsonElement convertValuetoTree(Object src) {
		return gson.toJsonTree(src);
	}

	public static <T> T convertTreetoValue(JsonElement json, Type typeOfT) {
		return gson.fromJson(json, typeOfT);
	}

	public static <T> T createObjectfromReader(Reader json, Type typeOfT) {
		return gson.fromJson(json, typeOfT);
	}

	public static <T> T createObjectfromJsonReader(JsonReader json, Type typeOfT) {
		return gson.fromJson(json, typeOfT);
	}
}
