package com.skillup.application.order;

import org.apache.rocketmq.client.producer.TransactionSendResult;

public interface MQSendRepo {
    void sendMsgToTopic(String topic, String originMsg);

    void sendDelayMsgToTopic(String topic, String originMsg);

    TransactionSendResult sendTxnMsg(String topic, String originMsg);
}
