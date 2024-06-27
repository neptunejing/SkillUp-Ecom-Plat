package com.skillup.application.order.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;

import java.util.Objects;

@RocketMQTransactionListener
@Slf4j
public class TxnMsgListener implements RocketMQLocalTransactionListener {
    @Autowired
    @Qualifier("CreateOrderTxnMsgHandler")
    TransactionMessageHandler createOrderTxnMsgHandler;

    @Autowired
    @Qualifier("PayOrderTxnMsgHandler")
    TransactionMessageHandler payCheckTxnMsgHandler;

    @Value("${order.topic.create-order}")
    String createOrderTopic;

    @Value("${order.topic.pay-order}")
    String payOrderTopic;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        String listenerName = (String) message.getHeaders().get("TXN_MESSAGE_HEADER");
        RocketMQLocalTransactionState state;
        Object payload = message.getPayload();
        try {
            TransactionMessageHandler messageHandler = Objects.equals(listenerName, createOrderTopic)
                    ? createOrderTxnMsgHandler
                    : payCheckTxnMsgHandler;
            if (null == messageHandler) {
                throw new RuntimeException("not match condition TransactionMessageHandler");
            }
            state = messageHandler.executeLocalTransaction(payload, null);
        } catch (Exception e) {
            log.error("RocketMQ executeLocalTxn error:{}", e.getMessage());
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        return state;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        String listenerName = (String) message.getHeaders().get("TXN_MESSAGE_HEADER");
        if (null == listenerName) {
            throw new RuntimeException("not params transactionMessageListener");
        }
        RocketMQLocalTransactionState state;
        try {
            TransactionMessageHandler messageHandler = Objects.equals(listenerName, createOrderTopic)
                    ? createOrderTxnMsgHandler
                    : payCheckTxnMsgHandler;
            if (null == messageHandler) {
                throw new RuntimeException("not match condition TransactionMessageHandler");
            }
            state = messageHandler.checkLocalTransaction(message.getPayload());
        } catch (Exception e) {
            log.error("RocketMQ checkLocalTxn error:{}", e.getMessage());
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return state;
    }
}
