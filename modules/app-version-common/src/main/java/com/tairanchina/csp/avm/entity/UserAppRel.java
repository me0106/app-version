package com.tairanchina.csp.avm.entity;

import java.util.Date;

import io.mybatis.provider.Entity;

/**
 * 用户和应用绑定关系
 * Created by hzlizx on 2018/6/6 0006
 */
@Entity.Table
public class UserAppRel {
    @Entity.Column(id = true)
    private String userId;
    private Integer appId;
    @Entity.Column(insertable = false)
    private Date createdTime;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
