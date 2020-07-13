package com.test.hackernews.api.hnw.routes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.test.hackernews.api.hnw.constants.CommonConstants;
import com.test.hackernews.api.hnw.handler.HackerNewsHandler;

/**
 * The Class HackerNewsRoutes.
 * @author shubham.srivastava
 */
@Component
public class HackerNewsRoutes {

	/** The context path. */
	@Value("${server.contextpath}")
	private String contextPath;

	/** The hacker news handler. */
	@Autowired
	private HackerNewsHandler hackerNewsHandler;

	/**
	 * Initiate routes.
	 *
	 * @return the router function
	 */
	@Bean
	public RouterFunction<ServerResponse> initiateRoutes() {
		return RouterFunctions.nest(RequestPredicates.path(contextPath), initRoutes());
	}

	/**
	 * Inits the routes.
	 *
	 * @return the router function
	 */
	private RouterFunction<ServerResponse> initRoutes() {
		return RouterFunctions
				.route(RequestPredicates.GET(CommonConstants.GET_TOP_STORIES_URI), 
						hackerNewsHandler::getTopStories)
				.andRoute(RequestPredicates.GET(CommonConstants.GET_PAST_STORIES_URI),
						hackerNewsHandler::getPastStories)
				.andRoute(RequestPredicates.GET(CommonConstants.GET_COMMENTS_URI), 
						hackerNewsHandler::getComments);
	}

}
