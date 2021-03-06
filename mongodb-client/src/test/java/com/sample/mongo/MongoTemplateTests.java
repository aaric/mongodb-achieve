package com.sample.mongo;

import com.sample.mongo.entity.UserInfo;
import lombok.Data;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Iterator;
import java.util.List;

/**
 * MongodbTests
 *
 * @author Aaric, created on 2018-05-09T12:00.
 * @since 0.1.0-SNAPSHOT
 */
@Disabled
@SpringBootTest(classes = TestApp.class)
@ExtendWith(SpringExtension.class)
public class MongoTemplateTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testSave() {
        UserInfo userInfo;
        for (int i = 0; i < 10; i++) {
            userInfo = new UserInfo();
            userInfo.setId(i)
                    .setUsername("user" + i)
                    .setEmail("user" + i + "@163.com")
                    .setAge(18 + 5 * i)
                    .setSalary(2000D + 100 * i);
            mongoTemplate.save(userInfo);
        }
    }

    @Test
    public void testFindOne() {
        Query query = new Query(Criteria.where("username").is("user1"));
        UserInfo userInfo = mongoTemplate.findOne(query, UserInfo.class);
        System.out.println(userInfo);
    }

    @Test
    public void testUpdateFirst() {
        Query query = new Query(Criteria.where("id").is(0));
        Update update = new Update().set("username", "admin")
                .set("salary", 5000D);
        mongoTemplate.updateFirst(query, update, UserInfo.class);
        //mongoTemplate.updateMulti(query, update, UserInfo.class);
    }

    @Test
    public void testRemove() {
        Query query = new Query(Criteria.where("id").is(9));
        mongoTemplate.remove(query, UserInfo.class);
    }

    @Test
    public void testPage() {
        Integer pageNum = 2;
        Integer pageSize = 5;
        Query query = new Query(Criteria.where("age").gt(18));

        long count = mongoTemplate.count(query, UserInfo.class);
        System.out.println(count);

        query.skip((pageNum - 1) * pageSize).limit(pageSize);
        List<UserInfo> userInfoList = mongoTemplate.find(query, UserInfo.class);
        System.out.println(userInfoList.size());
    }

    @Data
    @Document
    static class TotalResult {
        private String username;
        private Integer ageTotal;
    }

    @Test
    public void testAggregateUserINfo() {
        Aggregation aggregation = Aggregation.newAggregation(
                //Aggregation.group().sum("age").as("ageTotal")
                Aggregation.group("username")
                        .first("username").as("username")
                        .sum("age").as("ageTotal")
        );

        AggregationResults<TotalResult> aggregationResults = mongoTemplate.aggregate(aggregation, "userInfo", TotalResult.class);
        Iterator<TotalResult> it = aggregationResults.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    @Data
    @Document
    static class TripResult {
        private String deviceId;
        private Long tripId;
        private Double distanceMax;
    }

    @Test
    public void testAggregateDeviceData() {
        AggregationOptions aggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group(
                        Fields.from(
                                Fields.field("deviceId", "deviceId"),
                                Fields.field("tripId", "tripId")
                        )
                ).max("distance").as("distanceMax")
        ).withOptions(aggregationOptions);

        AggregationResults<TripResult> aggregationResults = mongoTemplate.aggregate(aggregation, "deviceData", TripResult.class);
        Iterator<TripResult> it = aggregationResults.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
}
