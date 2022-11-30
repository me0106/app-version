package com.tairanchina.csp.avm.service.impl;

import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.App;
import com.tairanchina.csp.avm.entity.Channel;
import com.tairanchina.csp.avm.entity.UserAppRel;
import com.tairanchina.csp.avm.mapper.AppMapper;
import com.tairanchina.csp.avm.mapper.ChannelMapper;
import com.tairanchina.csp.avm.mapper.UserAppRelMapper;
import com.tairanchina.csp.avm.service.AdminService;
import com.tairanchina.csp.avm.service.AppService;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import io.mybatis.mapper.example.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hzlizx on 2018/6/8 0008
 */
@Service
public class AppServiceImpl implements AppService {
    private static final Logger logger = LoggerFactory.getLogger(AppServiceImpl.class);

    @Autowired
    private UserAppRelMapper userAppRelMapper;

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ChannelMapper channelMapper;

    @Override
    public ServiceResult getAppListWithUserId(int page, int pageSize, String userId) {
        final Example<UserAppRel> example = userAppRelMapper.example();
        example.createCriteria().andEqualTo(UserAppRel::getUserId, userId);
        final Page<UserAppRel> appRels = userAppRelMapper.selectPage(PageRequest.of(page, pageSize), example);
        Page<App> collect = appRels.map(mapper -> {
            Integer appId = mapper.getAppId();
            App app = new App();
            app.setId(appId);
            app.setDelFlag(0);
            return appMapper.selectOne(app).orElse(null);
        });
        return ServiceResult.ok(collect);
    }

    @Override
    @Transactional
    public ServiceResult createApp(String appName, String tenantAppId) {
        if (!appMapper.selectAppForCreate(appName,tenantAppId).isEmpty()) {
            return ServiceResultConstants.TENANT_APP_ID_OR_APP_NAME_EXISTS;
        }
        App app = new App();
        app.setDelFlag(0);
        app.setTenantAppId(tenantAppId);
        app.setAppName(appName);
        app.setCreatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        app.setUpdatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        Integer insert = appMapper.insert(app);

        UserAppRel userAppRel = new UserAppRel();
        userAppRel.setAppId(app.getId());
        userAppRel.setUserId(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        Integer insert2 = userAppRelMapper.insert(userAppRel);
        logger.info("创建App[{}]成功", appName);
        //创建官方渠道
        Channel channel = new Channel();
        channel.setAppId(app.getId());
        channel.setCreatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        channel.setUpdatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        channel.setChannelCode("official");
        channel.setChannelName("官方渠道");
        channel.setChannelStatus(1);
        channel.setChannelType(1);
        Integer insert1 = channelMapper.insert(channel);
        if (insert > 0 && insert2 > 0 && insert1 > 0) {
            return ServiceResult.ok(app);
        } else {
            logger.error("创建App[{}]失败", appName);
            throw new RuntimeException("创建App失败");
        }
    }

    @Override
    public ServiceResult getMyApp() {
        return adminService.listBindApp(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
    }
}
