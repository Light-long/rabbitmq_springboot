package com.terminus.rabbitmq.controller;

import com.terminus.rabbitmq.config.DelayedQueueConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/ttl")
@RequiredArgsConstructor
public class SendMsgController {

    // 自动注入
    private final RabbitTemplate rabbitTemplate;

    private static final String  DELAY_EXCHANGE_NAME = "delayExchange";
    private static final String  DELAY_ROUTING_KEY = "delayRoutingKey";

    /**
     * 发送消息，通过死信队列 实现 延时队列
     * @param message 消息
     */
    @GetMapping("/sendMsg/{message}")
    public void sendMsg(@PathVariable("message") String message) {
        log.info("当前时间：{},发送一条信息给两个 TTL 队列:{}", new Date(), message);
        rabbitTemplate.convertAndSend("X", "XA",  "消息来自 ttl 为 10S 的队列: "+message);
        rabbitTemplate.convertAndSend("X", "XB",  "消息来自 ttl 为 40S 的队列: "+message);
    }

    /**
     * 发送消息，通过死信队列 实现 延时队列（优化）
     * @param message 消息
     * @param ttl 消息存活时间
     */
    @GetMapping("/sendExpireMsg/{message}/{ttl}")
    public void sendExpireMsg(@PathVariable("message") String message,
                              @PathVariable("ttl") String ttl) {
        log.info("当前时间：{},发送一条时长{}毫秒 TTL 信息给队列 C:{}", new Date(),ttl, message);
        rabbitTemplate.convertAndSend("X", "XC", message, msg -> {
            // 根据需求设置过期时间
            msg.getMessageProperties().setExpiration(ttl);
            return msg;
        });
    }

    /**
     * 基于 插件的 延迟队列
     * @param message 消息
     * @param delayTime 延迟时间（在交换机位置延迟）
     */
    @GetMapping("/delayMsg/{message}/{delayTime}")
    public void sendDelayMsg(@PathVariable("message") String message,
                             @PathVariable("delayTime") Integer delayTime) {
        log.info(" 当 前 时 间 ： {}, 发 送 一 条 延 迟 {} 毫秒的信息给队列 delayQueue:{}", new
                Date(),delayTime, message);
        rabbitTemplate.convertAndSend(DELAY_EXCHANGE_NAME, DELAY_ROUTING_KEY, message, msg -> {
            // 设置延迟时间
            msg.getMessageProperties().setDelay(delayTime);
            return msg;
        });
    }
}
