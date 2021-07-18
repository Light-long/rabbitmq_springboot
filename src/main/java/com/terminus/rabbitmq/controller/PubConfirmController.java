package com.terminus.rabbitmq.controller;

import com.terminus.rabbitmq.config.PubConfirmConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/confirm")
@RequiredArgsConstructor
public class PubConfirmController {

    private final RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMsg/{message}")
    public void sendConfirmMsg(@PathVariable("message") String message) {
        // 构建一个CorrelationData, 把消息的id告诉交换机
        CorrelationData correlationData1 = new CorrelationData("1");
        rabbitTemplate.convertAndSend(PubConfirmConfig.CONFIRM_EXCHANGE,
                PubConfirmConfig.CONFIRM_ROUTING_KEY, message+" success", correlationData1);
        log.info("发送消息内容： {} " , message+" success");

        CorrelationData correlationData2 = new CorrelationData("2");
        rabbitTemplate.convertAndSend(PubConfirmConfig.CONFIRM_EXCHANGE,
                PubConfirmConfig.CONFIRM_ROUTING_KEY+"22222", message+" return callback", correlationData2);
        log.info("发送消息内容： {} " , message+" return callback");
    }
}
