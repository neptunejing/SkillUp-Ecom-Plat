package com.skillup.application.order;


public interface MQSendRepo {
    void sendMsgToTopic(String topic, String originMsg);

    void sendDelayMsgToTopic(String topic, String originMsg, int delaySeconds);
}
