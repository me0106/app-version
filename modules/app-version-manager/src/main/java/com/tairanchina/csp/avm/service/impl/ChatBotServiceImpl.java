package com.tairanchina.csp.avm.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.tairanchina.csp.avm.ServiceException;
import com.tairanchina.csp.avm.common.Mq;
import com.tairanchina.csp.avm.constants.MQKey;
import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.App;
import com.tairanchina.csp.avm.entity.ChatBot;
import com.tairanchina.csp.avm.enums.ChatBotEventType;
import com.tairanchina.csp.avm.mapper.AppMapper;
import com.tairanchina.csp.avm.mapper.ChatBotMapper;
import com.tairanchina.csp.avm.mq.ChatBotMQEvent;
import com.tairanchina.csp.avm.service.ChatBotService;
import com.tairanchina.csp.avm.utils.StringUtilsExt;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import io.mybatis.mapper.example.Example;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hzlizx on 2018/9/27 0027
 */
@Service
public class ChatBotServiceImpl implements ChatBotService {
    private static final Logger logger = LoggerFactory.getLogger(ChatBotServiceImpl.class);

    @Autowired
    private ChatBotMapper chatBotMapper;

    @Autowired
    private AppMapper appMapper;

    @Override
    @Transactional
    public ServiceResult<?> createChatBot(Integer appId, String webhook, String name, List<ChatBotEventType> events) {
        if (StringUtilsExt.hasEmpty(webhook)) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        if (appId == null || appId < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        App app = appMapper.selectById(appId);
        if (app == null) {
            return ServiceResultConstants.APP_NOT_EXISTS;
        }
        ChatBot chatBot = new ChatBot();
        chatBot.setAppId(appId);
        long integer = chatBotMapper.wrapper().eq(ChatBot::getAppId, appId).count();
        if (integer > 0) {
            return ServiceResultConstants.CHAT_BOT_EXISTS;
        }
        chatBot.setWebhook(webhook);
        chatBot.setName(name);
        List<String> collect = events.stream().map(ChatBotEventType::name).collect(Collectors.toList());
        String eventStr = StringUtils.join(collect, ',');
        chatBot.setActiveEvent(eventStr);
        chatBot.setCreatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        chatBot.setCreatedTime(new Date());
        int insert = chatBotMapper.insert(chatBot);
        if (insert < 1) {
            throw new ServiceException("ChatBot插入失败"); // 回滚
        }
        return ServiceResult.ok(chatBot);
    }

    @Override
    @Transactional
    public ServiceResult<?> editChatBot(Integer appId, String webhook, String name, List<ChatBotEventType> events) {
        ChatBot chatBot = new ChatBot();
        chatBot.setAppId(appId);
        ChatBot chatBotSelected = chatBotMapper.selectOne(chatBot).orElse(null);
        if (chatBotSelected == null) {
            return ServiceResultConstants.CHAT_BOT_NOT_EXISTS;
        }
        if (StringUtils.isNotEmpty(webhook)) {
            chatBotSelected.setWebhook(webhook);
        }
        if (StringUtils.isNotEmpty(name)) {
            chatBotSelected.setName(name);
        }
        List<String> collect = events.stream().map(ChatBotEventType::name).collect(Collectors.toList());
        String eventStr = StringUtils.join(collect, ',');
        chatBotSelected.setActiveEvent(eventStr);
        chatBotSelected.setUpdatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        chatBotSelected.setUpdatedTime(new Date());
        int integer = chatBotMapper.updateById(chatBotSelected);
        if (integer < 1) {
            throw new ServiceException("ChatBot修改失败"); // 回滚
        }
        return ServiceResult.ok(chatBotSelected);
    }

    @Override
    public ServiceResult<?> deleteChatBot(Integer appId) {
        final Example<ChatBot> example = chatBotMapper.example();
        example.createCriteria().andEqualTo(ChatBot::getAppId, appId);
        chatBotMapper.deleteByExample(example);
        return ServiceResult.ok(null);
    }

    @Override
    public ServiceResult<?> getByAppId(int appId) {
        ChatBot chatBot = new ChatBot();
        chatBot.setAppId(appId);
        ChatBot chatBotSelected = chatBotMapper.selectOne(chatBot).orElse(null);
        if (chatBotSelected == null) {
            return ServiceResultConstants.APP_NOT_BIND_BOT;
        }
        return ServiceResult.ok(chatBotSelected);
    }

    @Override
    public void send(ChatBotEventType event, String text) {
        this.send(event, null, text, "TEXT");
    }

    @Override
    public void sendMarkdown(ChatBotEventType event, String title, String text) {
        this.send(event, title, text, "MARKDOWN");
    }

    private void send(ChatBotEventType event, String title, String text, String type) {
        // 该操作成功
        String operator = ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId();
        Integer appId = ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId();
        ChatBot chatBot = new ChatBot();
        chatBot.setAppId(appId);
        ChatBot chatBotSelected = chatBotMapper.selectOne(chatBot).orElse(null);
        if (chatBotSelected != null) {
            // 存在机器人
            String activeEvent = chatBotSelected.getActiveEvent();
            String[] split = activeEvent.split(",");
            if (Arrays.asList(split).contains(event.name())) {
                // 存在绑定事件
                ChatBotMQEvent chatBotMQEvent = new ChatBotMQEvent();
                chatBotMQEvent.setAppId(appId);
                chatBotMQEvent.setTitle(title);
                chatBotMQEvent.setChatBotEventType(event);
                chatBotMQEvent.setEventTime(new Date());
                chatBotMQEvent.setTriggeredBy(operator);
                chatBotMQEvent.setWebhook(chatBotSelected.getWebhook());
                chatBotMQEvent.setMessage(text);
                chatBotMQEvent.setType(type);
                // 发送MQ，消费者去发送机器人消息
                Mq.request(MQKey.CHAT_BOT_MQ, chatBotMQEvent.toString());
            } else {
                logger.info("机器人未绑定该事件:{}", event.name());
            }
        } else {
            logger.info("找不到机器人:appId[{}]", appId);
        }
    }


}
