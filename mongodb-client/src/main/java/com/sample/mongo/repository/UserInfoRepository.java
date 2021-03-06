package com.sample.mongo.repository;

import com.sample.mongo.entity.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 用户信息DAO
 *
 * @author Aaric, created on 2020-02-29T22:33.
 * @version 0.1.0-SNAPSHOT
 */
public interface UserInfoRepository extends MongoRepository<UserInfo, Integer> {
}
