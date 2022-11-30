package com.tairanchina.csp.avm.config;

import com.tairanchina.csp.avm.common.Mq;
import com.tairanchina.csp.avm.constants.MQKey;
import com.tairanchina.csp.avm.mq.ChatBotSender;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hzlizx on 2018/9/27 0027
 */
@Configuration
public class MqConfig {

    @Autowired
    private ChatBotSender chatBotSender;

    /**
     * 机器人消息消费者
     */
    @PostConstruct
    public void chatBotResponse() {
        Mq.response(MQKey.CHAT_BOT_MQ, chatBotSender);
    }

}
