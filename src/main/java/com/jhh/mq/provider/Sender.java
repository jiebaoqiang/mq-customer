package com.jhh.mq.provider;

import com.alibaba.fastjson.JSONObject;
import com.jhh.mq.common.MqConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 2018/11/21.
 */
@Component
@Slf4j
public class Sender implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        log.info("消息发送成功:" + correlationData);
    }

    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        log.error("消息发送失败:" + new String(message.getBody()));
    }

    public void send(JSONObject json) {
        rabbitTemplate.convertAndSend(MqConstant.RETRY_LETTER_ROUTING, json.toString());
    }

}
