package com.terminus.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

// 基于插件的延迟队列
@Configuration
public class DelayedQueueConfig {

    private static final String  DELAY_QUEUE_NAME = "delayQueue";
    private static final String  DELAY_EXCHANGE_NAME = "delayExchange";
    private static final String  DELAY_ROUTING_KEY = "delayRoutingKey";

    @Bean
    public Queue delayQueue() {
        return new Queue(DELAY_QUEUE_NAME);
    }

    @Bean
    public CustomExchange delayExchange() {
        Map<String, Object> agrs = new HashMap<>();
        agrs.put("x-delayed-type", "direct");
        /**
         * 1. 交换机名称
         * 2. 交换机类型（基于插件的延迟队列类型）
         * 3. 是否持久化
         * 4. 是否自动删除
         * 5. 定义延迟队列类型（直接延迟）
         */
        return new CustomExchange(DELAY_EXCHANGE_NAME, "x-delayed-message",
                true, false, agrs);
    }

    // 绑定交换机和队列
    @Bean
    public Binding bindingDelayQueueToExchange(@Qualifier("delayQueue") Queue queue,
                                               @Qualifier("delayExchange") CustomExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DELAY_ROUTING_KEY).noargs();
    }
}
