package com.jhh.mq.customer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jhh.mq.common.MqConstant;
import com.jhh.mq.exception.MqException;
import com.jhh.mq.provider.Sender;
import com.jhh.mq.util.HttpUrlPost;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@Slf4j
public class BorrowStatusReceiver {

    @Autowired
    private Sender sender;

    @RabbitListener(queues = MqConstant.BORROW_LETTER_ROUTING)
    @RabbitHandler
    public void syncBorrow(String context, Message message, Channel channel) {
        log.info("同步合同信息获取参数 context = {}", context);
        JSONObject json = JSONObject.parseObject(context);
        try {
            if (sendBorrow(context, json)) return;
        } catch (Exception e) {
            log.error("该消息发送出现异常",e);
            //将消息发送至重试队列进行重试l
            sender.send(json);
        }
    }


    /**
     * 重试队列
     */
    @RabbitListener(queues = MqConstant.RETRY_LETTER_ROUTING)
    @RabbitHandler
    public void retryBorrow(String context, Message message, Channel channel) throws Exception {
        log.info("重试同步合同信息获取参数 context = {}", context);
        JSONObject json = JSONObject.parseObject(context);
        try {
            if (sendBorrow(context, json)) return;
        } catch (Exception e) {
            log.error("该消息发送出现异常",e);
            throw new Exception("发送失败，重新发送 context"+context);
        }
    }

    /**
     *  发送消息
     */
    private boolean sendBorrow(String context, JSONObject json) throws MqException {
        if (StringUtils.isEmpty(context)) {
            log.error("队列参数为空");
            return true;
        }
        String url = json.getString("url");
        if (StringUtils.isEmpty(url)) {
            log.error("发送地址信息有误，数据异常");
            return true;
        }
        boolean state = post(json.getString("borrow"), url);
        if (!state) {
            throw new MqException(201,"合同同步A公司失败");
        }
        return false;
    }


    private boolean post(String obj, String url) {
        Map<String,String> maps = (Map) JSON.parse(obj);
        log.info("同步公司请求参数map = "+maps);
        String result = HttpUrlPost.sendPost(url, maps);
        log.info("同步公司请求数响应-----------------\n" + result);
        JSONObject json = JSONObject.parseObject(result);
        return "200".equals(json.getString("code"));
    }

}