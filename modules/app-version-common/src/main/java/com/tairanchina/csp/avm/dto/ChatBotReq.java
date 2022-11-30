package com.tairanchina.csp.avm.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Created by hzlizx on 2018/9/27 0027
 */
@Schema(description = "钉钉机器人请求信息")
public class ChatBotReq {

    @Schema(description = "机器人名称")
    private String name;

    @Schema(description = "WebHook")
    private String webhook;

    @Schema(description = "应用ID")
    private Integer appId;

    @Schema(description = "事件列表")
    private List<String> events;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }
}
