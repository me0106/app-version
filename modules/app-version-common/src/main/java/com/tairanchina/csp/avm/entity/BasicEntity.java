package com.tairanchina.csp.avm.entity;


import java.io.Serializable;
import java.util.Date;

import com.tairanchina.csp.avm.common.Json;
import io.mybatis.provider.Entity;

/**
 * Created by hzlizx on 2018/6/11 0011
 */
public class BasicEntity implements Serializable {

    private String createdBy;

    @Entity.Column(insertable = false,updatable = false)
    private Date createdTime;

    private String updatedBy;

    @Entity.Column(insertable = false,updatable = false)
    private Date updatedTime;

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return Json.toJsonString(this);
    }
}
