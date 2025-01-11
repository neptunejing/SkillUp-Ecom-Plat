package com.skillup.infrastructure.rocketMQ;

import com.skillup.application.order.MQSendRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;

@Repository
@Slf4j
public class RocketMQRepo implements MQSendRepo {
    @Autowired
    RocketMQTemplate rocketMQTemplate;

    @Override
    public void sendMsgToTopic(String topic, String originMsg) {
        try {
            rocketMQTemplate.send(topic, MessageBuilder.withPayload(originMsg).build());
            log.info("Send message [{}].", topic);
        } catch (Exception e) {
            log.error("Send message failed [{}]. {}", topic, e.getMessage());
        }
    }

    @Override
    public void sendDelayMsgToTopic(String topic, String originMsg, int delaySeconds) {
        Message message = new Message(topic, originMsg.getBytes(StandardCharsets.UTF_8));
        message.setDelayTimeLevel(secondsToRocketMQLevel(delaySeconds));
        try {
            rocketMQTemplate.getProducer().send(message);
            log.info("Send delayed message [{}].", topic);
        } catch (Exception e) {
            log.error("Send delayed message failed [{}].{}", topic, e.getMessage());
        }
    }

    private Integer secondsToRocketMQLevel(Integer delaySeconds) {
        if (delaySeconds <= 1) return 1;
        if (delaySeconds <= 5) return 2;
        if (delaySeconds <= 10) return 3;
        if (delaySeconds <= 30) return 4;
        if (delaySeconds <= 60) return 5;
        else return 6;
    }
}