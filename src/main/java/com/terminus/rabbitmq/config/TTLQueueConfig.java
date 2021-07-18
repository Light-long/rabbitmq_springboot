package com.terminus.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TTLQueueConfig {

    private static final String X_EXCHANGE = "X";
    private static final String Y_DEAD_EXCHANGE = "Y";
    private static final String QUEUE_A = "QA";
    private static final String QUEUE_B = "QB";
    private static final String DEAD_QUEUE_D = "QD";

    // 优化延迟队列
    private static final String QUEUE_C = "QC";
    // 给队列C绑定死信交换机
    @Bean("queueC")
    public Queue queueC() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", Y_DEAD_EXCHANGE);
        args.put("x-dead-letter-routing-key", "YD");
        return QueueBuilder.durable(QUEUE_C).withArguments(args).build();
    }
    // 绑定交换机和普通队列C
    @Bean
    public Binding bindingQueueCAndXExchange(@Qualifier("queueC") Queue queue,
                                     @Qualifier("xExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("XC");
    }

    // 声明交换机
    @Bean("xExchange")
    public DirectExchange xExchange() {
        return new DirectExchange(X_EXCHANGE);
    }

    @Bean("yExchange")
    public DirectExchange yExchange() {
        return new DirectExchange(Y_DEAD_EXCHANGE);
    }

    // 声明队列A TTL：10 绑定对应的死信交换机
    @Bean("queueA")
    public Queue queueA() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", Y_DEAD_EXCHANGE);
        args.put("x-dead-letter-routing-key", "YD");
        args.put("x-message-ttl", 10000);
        return QueueBuilder.durable(QUEUE_A).withArguments(args).build();
    }

    /**
     * 绑定X交换机和队列A
     */
    @Bean
    public Binding bindingQueueAAndXExchange(@Qualifier("queueA") Queue queue,
                                             @Qualifier("xExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("XA");
    }

    // 声明队列B TTL：40 绑定对应的死信交换机
    @Bean("queueB")
    public Queue queueB() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", Y_DEAD_EXCHANGE);
        args.put("x-dead-letter-routing-key", "YD");
        args.put("x-message-ttl", 40000);
        return QueueBuilder.durable(QUEUE_B).withArguments(args).build();
    }

    /**
     * 绑定X交换机和队列B
     */
    @Bean
    public Binding bindingQueueBAndXExchange(@Qualifier("queueB") Queue queue,
                                             @Qualifier("xExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("XB");
    }

    @Bean("queueD")
    public Queue deadQueueD() {
        return QueueBuilder.durable(DEAD_QUEUE_D).build();
    }

    /**
     * 绑定Y交换机和队列D
     */
    @Bean
    public Binding bindingDeadQueueDAndYExchange(@Qualifier("queueD") Queue queue,
                                                 @Qualifier("yExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("YD");
    }

}
