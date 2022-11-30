package com.tairanchina.csp.avm.controller;

import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ChatBotReq;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.enums.ChatBotEventType;
import com.tairanchina.csp.avm.service.ChatBotService;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hzlizx on 2018/9/27 0027
 */
@Tag(name = "钉钉机器人管理")
@RestController
@RequestMapping("/chatbot")
public class ChatBotController {
    private static final Logger logger = LoggerFactory.getLogger(ChatBotController.class);

    @Autowired
    private ChatBotService chatBotService;

    @Operation(description = "根据AppId找到绑定的机器人", summary = "根据AppId找到绑定的机器人")
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "用户凭证", required = true)
    })
    @GetMapping("/getByAppId/{appId}")
    public ServiceResult getByAppId(@PathVariable Integer appId) {
        if (appId == null || appId == 0) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return chatBotService.getByAppId(appId);
    }

    @Operation(description = "获取所有可以绑定的事件", summary = "获取所有可以绑定的事件")
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "用户凭证", required = true)
    })
    @GetMapping("/event")
    public ServiceResult listEvent() {
        List<HashMap<String, String>> collect = Arrays.stream(ChatBotEventType.values()).map(chatBotEventType -> {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("event", chatBotEventType.name());
            hashMap.put("message", chatBotEventType.getMessage());
            return hashMap;
        }).collect(Collectors.toList());
        return ServiceResult.ok(collect);
    }


    @Operation(description = "绑定一个钉钉机器人到应用", summary = "绑定一个钉钉机器人到应用，https://open-doc.dingtalk.com/docs/doc.htm?spm=a219a.7629140.0.0.21364a972AacKR&treeId=257&articleId=105735&docType=1")
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "用户凭证", required = true)
    })
    @PostMapping
    public ServiceResult create(@RequestBody ChatBotReq chatBot) {
        List<ChatBotEventType> events = this.eventTypeFormat(chatBot.getEvents());
        if (events.size() == 0) {
            return ServiceResultConstants.CHAT_BOT_EVENT_NOT_EXIST;
        }
        return chatBotService.createChatBot(chatBot.getAppId(), chatBot.getWebhook(), chatBot.getName(), events);
    }

    @Operation(description = "修改一个应用的机器人信息", summary = "修改一个应用的机器人信息")
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "用户凭证", required = true)
    })
    @PutMapping
    public ServiceResult edit(@RequestBody ChatBotReq chatBot) {
        List<ChatBotEventType> events = this.eventTypeFormat(chatBot.getEvents());
        if (events.size() == 0) {
            return ServiceResultConstants.CHAT_BOT_EVENT_NOT_EXIST;
        }
        return chatBotService.editChatBot(chatBot.getAppId(), chatBot.getWebhook(), chatBot.getName(), events);
    }

    @Operation(description = "删除一个应用的机器人信息", summary = "删除一个应用的机器人信息")
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "用户凭证", required = true)
    })
    @DeleteMapping("/{appId}")
    public ServiceResult delete(@PathVariable Integer appId) {
        return chatBotService.deleteChatBot(appId);
    }

    private List<ChatBotEventType> eventTypeFormat(List<String> events) {
        return events.stream().map(event -> {
            try {
                return ChatBotEventType.valueOf(event);
            } catch (Exception e) {
                logger.error("渲染事件枚举类[ChatBotEventType]失败:" + event, e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
