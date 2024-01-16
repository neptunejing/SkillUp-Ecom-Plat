package com.skillup.infrastructure.rocketMQ;

import com.skillup.application.order.MQSendRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;

@Repository
@Slf4j
public class RocketMQRepo implements MQSendRepo {
    @Autowired
    RocketMQTemplate rocketMQTemplate;

    @Override
    public void sendMsgToTopic(String topic, String originMsg) {
        // 1. create msg
        Message message = new Message(topic, originMsg.getBytes(StandardCharsets.UTF_8));
        // 2. send msg to related topic
        try {
            rocketMQTemplate.getProducer().send(message);
            log.info("-- send a message to rocketMQ. Topic: " + topic + " --");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendDelayMsgToTopic(String topic, String originMsg) {

    }
}
