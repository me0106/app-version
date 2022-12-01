package com.tairanchina.csp.avm.service.impl;

import java.util.List;

import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.Channel;
import com.tairanchina.csp.avm.mapper.ChannelMapper;
import com.tairanchina.csp.avm.service.BasicService;
import com.tairanchina.csp.avm.service.ChannelService;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import io.mybatis.mapper.example.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * 渠道管理实现
 * Created by hzlizx on 2018/6/6 0006
 */
@Service
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    private BasicService basicService;

    @Autowired
    private ChannelMapper channelMapper;

    @Override
    public ServiceResult<?> createChannel(String channelName, String channelCode, Integer channelType) {
        final Integer appId = ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId();
        List<Channel> channels = channelMapper.selectChannelsForCreate(appId, channelCode, channelName);
        if (!channels.isEmpty()) {
            return ServiceResultConstants.CHANNEL_CODE_OR_NAME_EXISTS;
        }

        Channel channel = new Channel();
        channel.setChannelName(channelName);
        channel.setChannelCode(channelCode);
        channel.setChannelType(channelType);
        channel.setAppId(appId);
        channel.setCreatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        int insert = channelMapper.insert(channel);
        if (insert > 0) {
            return ServiceResult.ok(channel);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult<?> deleteChannel(int channelId) {
        return ServiceResultConstants.SERVICE_NOT_SUPPORT;
    }

    @Override
    public ServiceResult<?> deleteChannelForever(int channelId) {
        return ServiceResultConstants.SERVICE_NOT_SUPPORT;
    }

    @Override
    public ServiceResult<?> scrapChannel(int channelId) {
        Channel channel = channelMapper.selectById(channelId);
        if (channel == null) {
            return ServiceResultConstants.CHANNEL_NOT_EXISTS;
        }
        if (!channel.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        if ("official".equals(channel.getChannelCode())) {
            return ServiceResultConstants.CHANNEL_OFFICIAL;
        }
        channel.setChannelStatus(2);//标记为废弃
        channel.setUpdatedTime(null);
        channel.setUpdatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        int integer = channelMapper.updateById(channel);
        if (integer > 0) {
            return ServiceResult.ok(channel);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult<?> openChannel(int channelId) {
        Channel channel = channelMapper.selectById(channelId);
        if (channel == null) {
            return ServiceResultConstants.CHANNEL_NOT_EXISTS;
        }
        if (!channel.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        channel.setChannelStatus(1);//标记为正常
        channel.setUpdatedTime(null);
        channel.setUpdatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        int integer = channelMapper.updateById(channel);
        if (integer > 0) {
            return ServiceResult.ok(channel);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult<?> list(int page, int pageSize, Example<Channel> wrapper) {
        final Page<Channel> channels = channelMapper.selectPage(PageRequest.of(page, pageSize), wrapper);
        basicService.formatCreatedBy(channels);
        return ServiceResult.ok(channels);
    }

    @Override
    public ServiceResult<?> findChannel(int channelId) {
        Channel channel = channelMapper.selectById(channelId);
        if (channel == null) {
            return ServiceResultConstants.CHANNEL_NOT_EXISTS;
        }
        if (!channel.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        return ServiceResult.ok(channel);
    }

    @Override
    public ServiceResult<?> findByChannelCode(String channelCode) {
        final Integer appId = ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId();
        List<Channel> channels = channelMapper.wrapper().eq(Channel::getChannelCode, channelCode).eq(Channel::getAppId, appId).endSql("LIMIT 1").list();
        if (!channels.isEmpty()) {
            Channel channel1 = channels.get(0);
            if (!channel1.getAppId().equals(appId)) {
                return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
            }
            return ServiceResult.ok(channel1);
        } else {
            return ServiceResultConstants.CHANNEL_NOT_EXISTS;
        }
    }
}
