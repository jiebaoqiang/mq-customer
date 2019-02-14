package com.jhh.mq.config;

import com.jhh.mq.common.MqConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 2018/10/26.
 */
@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue maintainQueue() {
        Map<String,Object> args=new HashMap<>();
        // 设置该Queue的死信的信箱
        args.put("x-dead-letter-exchange", MqConstant.DEAD_LETTER_EXCHANGE);
        // 设置死信routingKey
        args.put("x-dead-letter-routing-key", MqConstant.DEAD_LETTER_ROUTING);
        return new Queue(MqConstant.RETRY_LETTER_ROUTING,true,false,false,args);
    }

    @Bean
    public Binding maintainBinding() {
        return BindingBuilder.bind(maintainQueue()).to(DirectExchange.DEFAULT)
                .with(MqConstant.RETRY_LETTER_ROUTING);
    }

    @Bean
    public Queue deadLetterQueue(){
        return new Queue(MqConstant.DEAD_LETTER_ROUTING);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(MqConstant.DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Binding deadLetterBindding(){
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(MqConstant.DEAD_LETTER_ROUTING);
    }

    @Bean
    public Queue borrowQuery() {
        return new Queue(MqConstant.BORROW_LETTER_ROUTING,true);
    }
}
