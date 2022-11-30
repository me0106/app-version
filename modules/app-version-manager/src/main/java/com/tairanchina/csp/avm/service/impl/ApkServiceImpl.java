package com.tairanchina.csp.avm.service.impl;

import java.util.HashMap;
import java.util.List;

import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ApkExt;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.AndroidVersion;
import com.tairanchina.csp.avm.entity.Apk;
import com.tairanchina.csp.avm.entity.Channel;
import com.tairanchina.csp.avm.mapper.AndroidVersionMapper;
import com.tairanchina.csp.avm.mapper.ApkMapper;
import com.tairanchina.csp.avm.mapper.ChannelMapper;
import com.tairanchina.csp.avm.service.ApkService;
import com.tairanchina.csp.avm.service.BasicService;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import io.mybatis.mapper.example.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Created by hzlizx on 2018/6/14 0014
 */
@Service
public class ApkServiceImpl implements ApkService {
    private static final Logger logger = LoggerFactory.getLogger(ApkServiceImpl.class);

    @Autowired
    private ApkMapper apkMapper;

    @Autowired
    private BasicService basicService;

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    AndroidVersionMapper androidVersionMapper;

    @Override
    public ServiceResult create(Apk apk) {
        apk.setAppId(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId());
        apk.setId(null);
        apk.setCreatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        if (!apkMapper.wrapper()
            .eq(Apk::getChannelId, apk.getChannelId())
            .eq(Apk::getAppId, apk.getAppId())
            .eq(Apk::getVersionId, apk.getVersionId())
            .eq(Apk::getDelFlag, 0)
            .list().isEmpty()) {
            return ServiceResultConstants.APK_EXISTS;
        }
        int insert = apkMapper.insert(apk);
        if (insert > 0) {
            return ServiceResult.ok(apk);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult list(int page, int pageSize, Example<Apk> wrapper, int versionId) {
        AndroidVersion androidVersion = androidVersionMapper.selectById(versionId);
        if (androidVersion == null) {
            return ServiceResultConstants.VERSION_NOT_EXISTS;
        }
        final Page<Apk> apks = apkMapper.selectPage(PageRequest.of(page, pageSize), wrapper);
        basicService.formatCreatedBy(apks);
        Page<ApkExt> collect = apks.map(apk -> {
            ApkExt ext = new ApkExt(apk);
            Channel channel = channelMapper.selectById(apk.getChannelId());
            if (channel != null) {
                ext.setChannelCode(channel.getChannelCode());
            }
            ext.setVersion(androidVersion.getAppVersion());
            return ext;
        });
        return ServiceResult.ok(collect);

    }

    @Override
    public ServiceResult delivery(int apkId) {
        Apk apk = apkMapper.selectById(apkId);
        if (apk == null) {
            return ServiceResultConstants.APK_NOT_EXISTS;
        }

        if (!apk.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        apk.setDeliveryStatus(1);
        int integer = apkMapper.updateById(apk);
        if (integer > 0) {
            return ServiceResult.ok(apk);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult undelivery(int apkId) {
        Apk apk = apkMapper.selectById(apkId);
        if (apk == null) {
            return ServiceResultConstants.APK_NOT_EXISTS;
        }

        if (!apk.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        apk.setDeliveryStatus(0);
        int integer = apkMapper.updateById(apk);
        if (integer > 0) {
            return ServiceResult.ok(apk);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult delete(int apkId) {
        Apk apk = apkMapper.selectById(apkId);
        if (apk == null) {
            return ServiceResultConstants.APK_NOT_EXISTS;
        }
        if (!apk.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        apk.setDelFlag(1);
        int integer = apkMapper.updateById(apk);
        if (integer > 0) {
            return ServiceResult.ok(apk);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult exists(String channelCode, int versionId) {
        List<Channel> channels = channelMapper.wrapper().eq(Channel::getChannelCode, channelCode)
            .eq(Channel::getAppId, ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())
            .eq(Channel::getDelFlag, 0)
            .endSql("LIMIT 1")
            .list();
        if (channels.isEmpty()) {
            return ServiceResultConstants.CHANNEL_NOT_EXISTS;
        }
        Channel channelSelected = channels.get(0);
        if (channelSelected.getChannelStatus() != 1) {
            return ServiceResultConstants.CHANNEL_STATUS_2_3;
        }
        List<Apk> apks = apkMapper.wrapper().eq(Apk::getChannelId, channelSelected.getId())
            .eq(Apk::getVersionId, versionId)
            .eq(Apk::getDelFlag, 0)
            .eq(Apk::getAppId, ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId()).endSql("LIMIT 1").list();
        HashMap<String, Object> map = new HashMap<>();
        String message = "APK不存在";
        if (apks.isEmpty()) {
            map.put("exists", false);
        } else {
            message = "该渠道下APK已经上传包，请勿重复上传";
            map.put("exists", true);
        }
        ServiceResult ok = ServiceResult.ok(map);
        ok.setMessage(message);
        return ok;
    }

    @Override
    public ServiceResult getApkPageWithChannelCode(int page, int pageSize, Integer versionId, String channelCode, String md5, Integer deliveryStatus) {
        AndroidVersion androidVersion = androidVersionMapper.selectById(versionId);
        if (androidVersion == null) {
            return ServiceResultConstants.VERSION_NOT_EXISTS;
        }
        final Page<ApkExt> apkExts = apkMapper.selectApkWithChannelCode(PageRequest.of(page, pageSize), versionId, channelCode, md5, deliveryStatus);
        basicService.formatCreatedBy(apkExts);
        return ServiceResult.ok(apkExts);
    }
}
