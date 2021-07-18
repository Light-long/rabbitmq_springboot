package com.terminus.rabbitmq.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    /**
     * 需要将自定义的回调 注入rabbitmq
     * 1. 首先注入rabbitmq template
     * 2. 将自定义回调注入rabbitmq template 内部
     */
    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }


    /**
     * 交换机对生产者发送消息回调
     * 1. 交换机成功收到消息 回调
     *  1.1 回调的相关信息 (这个参数需要 消息生产者传过来)
     *  1.2 true 接收到了消息
     *  1.3 null
     * 2. 交换机没有收到消息 也会回调
     *  2.1 回调的相关信息
     *  2.2 false 没有收到消息
     *  2.3 错误原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        // 获取消息的编号id
        String id = correlationData != null ? correlationData.getId() : "";
        if (b) {
            log.info("收到了编号为: {} 的消息", id);
        } else {
            log.info("没有收到编号为: {} 的消息， 原因是：{}", id, s);
        }
    }


    /**
     * 当交换机收到消息，但是由于 路由不到队列（路由错误，队列不存在）就会回调这个方法 回退消息
     * @param message 消息
     * @param replyCode 错误码
     * @param replyText 错误原因
     * @param exchange 哪个交换机
     * @param routingKey 路由
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("消息：{}，被交换机{}退回,退回code：{},退回原因：{},路由{}",new String(message.getBody()),
                exchange, replyCode, replyText, routingKey);
    }
}
