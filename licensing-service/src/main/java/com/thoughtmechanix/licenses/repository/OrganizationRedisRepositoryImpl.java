package com.thoughtmechanix.licenses.repository;

import com.thoughtmechanix.licenses.events.handlers.OrganizationChangeHandler;
import com.thoughtmechanix.licenses.model.Organization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanName;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
public class OrganizationRedisRepositoryImpl implements OrganizationRedisRepository {
    private static final String HASH_NAME ="organization";
    private static final Logger logger = LoggerFactory.getLogger(OrganizationRedisRepositoryImpl.class);

    @Autowired
    Tracer tracer;
    
    private RedisTemplate<String, Organization> redisTemplate;
    private HashOperations hashOperations;

    public OrganizationRedisRepositoryImpl(){
        super();
    }

    @Autowired
    private OrganizationRedisRepositoryImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }


    @Override
    public void saveOrganization(Organization org) {
        hashOperations.put(HASH_NAME, org.getId(), org);
    }

    @Override
    public void updateOrganization(Organization org) {
        hashOperations.put(HASH_NAME, org.getId(), org);
    }

    @Override
    public void deleteOrganization(String organizationId) {
    	 Span newSpan = tracer.createSpan("deleteOrganizationDataFromRedis");
        try{
    	 hashOperations.delete(HASH_NAME, organizationId);
        }
        finally {
            newSpan.tag("peer.service", "redis");
            newSpan.logEvent(org.springframework.cloud.sleuth.Span.CLIENT_RECV);
            tracer.close(newSpan);
          }
    }

    @Override
    public Organization findOrganization(String organizationId) {
       return (Organization) hashOperations.get(HASH_NAME, organizationId);
    }
}
