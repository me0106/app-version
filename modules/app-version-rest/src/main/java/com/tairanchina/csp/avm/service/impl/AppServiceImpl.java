package com.tairanchina.csp.avm.service.impl;

import com.tairanchina.csp.avm.entity.App;
import com.tairanchina.csp.avm.mapper.AppMapper;
import com.tairanchina.csp.avm.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hzlizx on 2018/6/25 0025
 */
@Service
public class AppServiceImpl implements AppService {


    @Autowired
    private AppMapper appMapper;

    @Override
    public App findAppByTenantAppId(String tenantAppId) {
        App app = new App();
        app.setTenantAppId(tenantAppId);
        app.setDelFlag(0);
        return appMapper.selectOne(app).orElse(null);
    }
}
