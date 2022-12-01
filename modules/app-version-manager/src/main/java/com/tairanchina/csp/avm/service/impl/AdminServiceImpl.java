package com.tairanchina.csp.avm.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.App;
import com.tairanchina.csp.avm.entity.Channel;
import com.tairanchina.csp.avm.entity.User;
import com.tairanchina.csp.avm.entity.UserAppRel;
import com.tairanchina.csp.avm.mapper.AppMapper;
import com.tairanchina.csp.avm.mapper.ChannelMapper;
import com.tairanchina.csp.avm.mapper.UserAppRelMapper;
import com.tairanchina.csp.avm.mapper.UserMapper;
import com.tairanchina.csp.avm.service.AdminService;
import com.tairanchina.csp.avm.service.BasicService;
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
 * 管理员服务 实现
 * Created by hzlizx on 2018/6/6 0006
 */
@Service
public class AdminServiceImpl implements AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private BasicService basicService;

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private UserAppRelMapper userAppRelMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ChannelMapper channelMapper;

    @Override
    public ServiceResult<?> bindUserAndApp(String userId, int appId) {
        App app = appMapper.selectById(appId);
        if (app == null) {
            return ServiceResultConstants.APP_NOT_EXISTS;
        }
        logger.info("绑定用户[{}]与App[{}:{}]", userId, appId, app.getAppName());
        UserAppRel userAppRel = new UserAppRel();
        userAppRel.setAppId(appId);
        userAppRel.setUserId(userId);
        if (userAppRelMapper.selectOne(userAppRel).isPresent()) {
            return ServiceResult.failed(
                ServiceResultConstants.REL_EXISTS.getCode(),
                "绑定用户[" + userId + "]与App[" + appId + "]失败，因为该关系已经存在");
        }
        int insert = userAppRelMapper.insert(userAppRel);
        if (insert > 0) {
            logger.info("绑定用户[{}]与App[{}]成功", userId, appId);
            return ServiceResult.ok(userAppRel);
        } else {
            logger.error("绑定用户[{}]与App[{}]失败", userId, appId);
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult<?> unbindUserAndApp(String userId, int appId) {
        logger.info("解绑用户[{}]与App[{}]", userId, appId);
        int delete = userAppRelMapper.wrapper().eq(UserAppRel::getAppId, appId)
            .eq(UserAppRel::getUserId, userId).delete();
        if (delete > 0) {
            logger.info("解绑用户[{}]与App[{}]成功", userId, appId);
            return ServiceResult.ok(null);
        } else {
            logger.error("解绑用户[{}]与App[{}]失败", userId, appId);
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    @Transactional
    public ServiceResult<?> createApp(String appName, String tenantAppId) {
        if (!appMapper.selectAppForCreate(appName, tenantAppId).isEmpty()) {
            return ServiceResultConstants.TENANT_APP_ID_OR_APP_NAME_EXISTS;
        }
        App app = new App();
        app.setDelFlag(0);
        app.setTenantAppId(tenantAppId);
        app.setAppName(appName);
        app.setCreatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        int insert = appMapper.insert(app);

        if (insert > 0) {
            logger.info("创建App[{}]成功", appName);
            //创建官方渠道
            Channel channel = new Channel();
            channel.setAppId(app.getId());
            channel.setCreatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
            channel.setChannelCode("official");
            channel.setChannelName("官方渠道");
            channel.setChannelStatus(1);
            int insert1 = channelMapper.insert(channel);
            if (insert1 > 0) {
                logger.info("创建官方渠道成功");
                return ServiceResult.ok(app);
            } else {
                throw new RuntimeException("创建官方渠道失败");
            }
        } else {
            logger.error("创建App[{}]失败", appName);
            throw new RuntimeException("创建App失败");
        }
    }

    @Override
    public ServiceResult<?> editApp(int appId, String appName, String tenantAppId) {
        List<App> apps = appMapper.selectAppUpdate(appName, tenantAppId, appId);
        if (!apps.isEmpty()) {
            return ServiceResult.failed(
                ServiceResultConstants.APP_EXISTS.getCode(),
                "已经有其他应用取名为[" + appName + "]，或AppId为[ " + tenantAppId + " ]，请换一个名称或AppId");
        }
        App app = new App();
        app.setId(appId);
        app.setAppName(appName);
        app.setTenantAppId(tenantAppId);
        int integer = appMapper.updateById(app);
        if (integer > 0) {
            logger.info("修改App[{}]成功", appName);
            return ServiceResult.ok(app);
        } else {
            logger.error("修改App[{}]失败", appName);
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult<?> deleteApp(int appId) {
        App app = appMapper.selectById(appId);
        if (app == null) {
            return ServiceResultConstants.APP_NOT_EXISTS;
        }
        app.setDelFlag(1);
        int integer = appMapper.updateById(app);
        if (integer > 0) {
            logger.info("删除应用[{}:{}]成功", app.getId(), app.getAppName());
            return ServiceResult.ok(app);
        } else {
            logger.error("删除应用[{}:{}]失败", app.getId(), app.getAppName());
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult<?> getApp(int appId) {
        App app = appMapper.selectById(appId);
        if (app == null) {
            return ServiceResultConstants.APP_NOT_EXISTS;
        }
        return ServiceResult.ok(app);
    }

    @Override
    public ServiceResult<?> deleteAppForever(int appId) {
        App app = appMapper.selectById(appId);
        if (app == null) {
            return ServiceResultConstants.APP_NOT_EXISTS;
        }
        if (app.getDelFlag() != 1) {
            return ServiceResultConstants.APP_NOT_SOFT_DELETE;
        }
        int integer = appMapper.deleteById(appId);
        if (integer > 0) {
            logger.info("彻底删除应用[{}:{}]成功", app.getId(), app.getAppName());
            return ServiceResult.ok(app);
        } else {
            logger.error("彻底删除应用[{}:{}]失败", app.getId(), app.getAppName());
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult<?> listApp(int page, int pageSize, Example<App> wrapper) {
        final Page<App> apps = appMapper.selectPage(PageRequest.of(page, pageSize), wrapper);
        basicService.formatCreatedBy(apps);
        return ServiceResult.ok(apps);
    }

    @Override
    public ServiceResult<?> listAppWithBindInfo(int page, int pageSize, Example<App> wrapper, String userId) {
        final Page<App> apps = appMapper.selectPage(PageRequest.of(page, pageSize), wrapper);
        Page<HashMap<String, Object>> collect = apps.map(mapper -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("appName", mapper.getAppName());
            map.put("id", mapper.getId());
            UserAppRel userAppRel = new UserAppRel();
            userAppRel.setUserId(userId);
            userAppRel.setAppId(mapper.getId());
            if (userAppRelMapper.selectOne(userAppRel).isPresent()) {
                map.put("bind", true);
            } else {
                map.put("bind", false);
            }
            return map;
        });
        return ServiceResult.ok(collect);
    }

    @Override
    public ServiceResult<?> listBindApp(String userId) {
        ArrayList<HashMap> hashMaps = userAppRelMapper.listBindApp(userId);
        List<HashMap> collect = hashMaps.stream().map(mapper -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("appName", mapper.get("app_name"));
            map.put("appId", mapper.get("id"));
            map.put("tenantAppId", mapper.get("tenant_app_id"));
            return map;
        }).collect(Collectors.toList());
        return ServiceResult.ok(collect);
    }

    @Override
    public ServiceResult<?> listUser(int page, int pageSize, Example<User> wrapper) {
        return ServiceResult.ok(userMapper.selectPage(PageRequest.of(page, pageSize), wrapper));
    }

    @Override
    public ServiceResult<?> isAdmin(String userId) {
        User user1 = new User();
        user1.setUserId(userId);
        List<User> users = userMapper.selectList(user1);
        if (!users.isEmpty() && users.get(0).getIsAdmin() == 1) {
            return ServiceResult.ok(true);
        } else {
            return ServiceResult.ok(false);
        }
    }
}
