package com.terminus.rabbitmq.consumer;

import com.terminus.rabbitmq.config.PubConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PubConfirmConsumer {

    @RabbitListener(queues = PubConfirmConfig.CONFIRM_QUEUE)
    public void receiveConfirmMsg(Message message) {
        String msg = new String(message.getBody());
        log.info("PubConfirmConsumer接收到的消息为：{}", msg);
    }
}
