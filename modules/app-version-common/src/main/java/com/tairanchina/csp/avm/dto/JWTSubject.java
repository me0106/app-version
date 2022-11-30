package com.tairanchina.csp.avm.dto;


import com.tairanchina.csp.avm.common.Json;

/**
 * Created by hzlizx on 2019/2/21
 */
public class JWTSubject {

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return Json.toJsonString(this);
    }
}
