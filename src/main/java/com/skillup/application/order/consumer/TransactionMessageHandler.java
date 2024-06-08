package com.skillup.application.order.consumer;

import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;

public interface TransactionMessageHandler {
    RocketMQLocalTransactionState executeLocalTransaction(Object payload, Object arg);

    RocketMQLocalTransactionState checkLocalTransaction(Object payload);
}
