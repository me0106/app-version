package com.tairanchina.csp.avm.config;


import com.dingtalk.chatbot.DingtalkChatbotClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hzlizx on 2018/5/4 0004
 */
@Configuration
public class BeanConfig {

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Bean
    @ConditionalOnMissingBean
    public DingtalkChatbotClient dingtalkChatbotClient() {
        return new DingtalkChatbotClient();
    }
}
