package com.skillup.infrastructure.rocketMQ;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RocketMQRepoTest {
    @Autowired
    RocketMQRepo rocketMQRepo;

    @Test
    void sendMsgToTopicTest() {
        rocketMQRepo.sendMsgToTopic("test-topic", "Hello, rocketMQ!");
    }
}