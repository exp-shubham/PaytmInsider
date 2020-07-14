package com.test.hackernews.api.hnw.configuration;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractReactiveCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories;
import org.springframework.data.couchbase.repository.support.IndexManager;
import org.springframework.util.ObjectUtils;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.cluster.ClusterInfo;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;

import lombok.extern.slf4j.Slf4j;


@Configuration
@Slf4j
@EnableReactiveCouchbaseRepositories(basePackages = {"com.test.hackernews.api.hnw"})
public class CouchbaseConfiguration extends AbstractReactiveCouchbaseConfiguration
		{
	@Value("#{'${couchbaseserver.hostNames:}'.split(',')}")
	private List<String> bootstrapHosts;
	@Value("${couchbaseserver.bucketName:}")
	private String bucketName;
	private String bucketCred;
	@Value("${couchbaseserver.cluster.username:}")
	private String clusterUsername;
	@Value("${couchbaseserver.cluster.password:}")
	private String clusterPassword;

	private Bucket couchbaseBucketRef;
	private ClusterInfo clusterInfo;

	private CouchbaseEnvironment couchbaseEnvironment;


	protected CouchbaseEnvironment getEnvironment() {
		if (couchbaseEnvironment == null) {
			couchbaseEnvironment = DefaultCouchbaseEnvironment.create();
		}
		return couchbaseEnvironment;
	}

	protected String getBucketName() {
		return bucketName;
	}

	protected String getBucketPassword() {
		return bucketCred;
	}

	protected List<String> getBootstrapHosts() {
		return Collections.unmodifiableList(bootstrapHosts);
	}

	@Bean(name = {"couchbaseClusterInfo"})
	public ClusterInfo couchbaseClusterInfo() throws Exception {
		if ((!ObjectUtils.isEmpty(clusterUsername)) && (!ObjectUtils.isEmpty(clusterPassword))) {
			clusterInfo = couchbaseCluster().authenticate(clusterUsername, clusterPassword).clusterManager().info();
		} 
		return clusterInfo;
	}
	
	@Bean(destroyMethod = "close", name = {"couchbaeBucket"})
	public Bucket couchbaseClient() throws Exception {
		if ((!ObjectUtils.isEmpty(clusterUsername)) && (!ObjectUtils.isEmpty(clusterPassword))) {
			couchbaseBucketRef = couchbaseCluster().authenticate(clusterUsername, clusterPassword)
					.openBucket(getBucketName());
		} 
		return couchbaseBucketRef;
	}
	
	@Bean(name = {"couchbaseIndexManager"})
	public IndexManager indexManagerDev() {
		log.info("creating index manager for local or dev env");
		return new IndexManager(true, true, true);
	}


}