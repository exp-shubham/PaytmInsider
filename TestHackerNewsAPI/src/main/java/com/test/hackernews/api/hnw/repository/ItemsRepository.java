package com.test.hackernews.api.hnw.repository;

import org.springframework.data.couchbase.core.query.ViewIndexed;
import org.springframework.stereotype.Repository;

import com.test.hackernews.api.hnw.model.Items;

@Repository
@ViewIndexed(designDoc = "items")
public interface ItemsRepository extends BaseRepository<Items, Integer>{

}
