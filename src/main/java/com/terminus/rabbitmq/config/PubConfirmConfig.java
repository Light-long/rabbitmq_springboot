package com.terminus.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PubConfirmConfig {

    public static final String CONFIRM_EXCHANGE = "confirmExchange";
    public static final String CONFIRM_QUEUE = "confirmQueue";
    public static final String CONFIRM_ROUTING_KEY = "confirmRouingKey";

    @Bean("confirmExchange")
    public DirectExchange confirmExchange() {
        return new DirectExchange(CONFIRM_EXCHANGE);
    }

    @Bean("confirmQueue")
    public Queue confirmQueue() {
        return QueueBuilder.durable(CONFIRM_QUEUE).build();
    }

    @Bean
    public Binding bindingConfirmQueueAndExchange(@Qualifier("confirmQueue") Queue queue,
                                                  @Qualifier("confirmExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CONFIRM_ROUTING_KEY);
    }
}
