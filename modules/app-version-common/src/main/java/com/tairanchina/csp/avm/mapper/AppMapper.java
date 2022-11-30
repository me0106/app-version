package com.tairanchina.csp.avm.mapper;

import java.util.List;

import com.tairanchina.csp.avm.common.BaseMapper;
import com.tairanchina.csp.avm.entity.App;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * Created by hzlizx on 2018/5/17 0017
 */
@Mapper
public interface AppMapper extends BaseMapper<App, Integer> {

    @Select("SELECT * FROM app WHERE (app_name=#{appName} OR tenant_app_id=#{tenantAppId}) AND del_flag=0")
    List<App> selectAppForCreate(String appName, String tenantAppId);

    @Select("SELECT * FROM app WHERE (app_name=#{appName} OR tenant_app_id=#{tenantAppId}) AND del_flag=0 AND id!=#{appId}")
    List<App> selectAppUpdate(String appName, String tenantAppId, int appId);
}
