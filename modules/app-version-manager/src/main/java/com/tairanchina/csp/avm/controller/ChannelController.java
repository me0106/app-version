package com.tairanchina.csp.avm.controller;

import com.tairanchina.csp.avm.annotation.OperationRecord;
import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.Channel;
import com.tairanchina.csp.avm.entity.OperationRecordLog;
import com.tairanchina.csp.avm.service.ChannelService;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import io.mybatis.mapper.example.Example;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Created by hzlizx on 2018/6/8 0008
 */
@RestController
@RequestMapping("/channel")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping
    public ServiceResult<?> list(@RequestParam(required = false, defaultValue = "1") int page,
                              @RequestParam(required = false, defaultValue = "10") int pageSize,
                              @RequestParam(required = false, defaultValue = "") String channelName,
                              @RequestParam(required = false, defaultValue = "") String channelCode,
                              @RequestParam(required = false, defaultValue = "") Integer channelStatus) {
        Example<Channel> example = new Example<>();
        final Example.Criteria<Channel> wrapper = example.createCriteria();
        wrapper.andEqualTo(Channel::getAppId, ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId());
        if (StringUtils.hasLength(channelName)) {
            wrapper.andLike(Channel::getChannelName, "%" + channelName + "%");
        }
        if (StringUtils.hasLength(channelCode)) {
            wrapper.andLike(Channel::getChannelCode, "%" + channelCode + "%");
        }
        if (channelStatus != null && (channelStatus == 1 || channelStatus == 2 || channelStatus == 3)) {
            wrapper.andEqualTo(Channel::getChannelStatus, channelStatus);
        }
        wrapper.andEqualTo(Channel::getDelFlag, 0);
        example.orderByDesc(Channel::getCreatedTime);
        return channelService.list(page, pageSize, example);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PostMapping
    @OperationRecord(type = OperationRecordLog.OperationType.CREATE, resource = OperationRecordLog.OperationResource.CHANNEL, description = OperationRecordLog.OperationDescription.CREATE_CHANNEL)
    public ServiceResult<?> create(@RequestBody Channel channel) {
        if (StringUtils.isEmpty(channel.getChannelName()) || StringUtils.isEmpty(channel.getChannelCode())) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        if (channel.getChannelName().length() > 32) {
            return ServiceResultConstants.CHANNEL_NAME_TOO_LONG;
        }
        if (channel.getChannelCode().length() > 32) {
            return ServiceResultConstants.CHANNEL_CODE_TOO_LONG;
        }
        return channelService.createChannel(channel.getChannelName(), channel.getChannelCode(), channel.getChannelType());
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @DeleteMapping("/{id}")
    @OperationRecord(type = OperationRecordLog.OperationType.DELETE, resource = OperationRecordLog.OperationResource.CHANNEL, description = OperationRecordLog.OperationDescription.DELETE_CHANNEL)
    public ServiceResult<?> delete(@PathVariable int id) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return channelService.deleteChannel(id);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PutMapping("/{id}/scrap")
    @OperationRecord(type = OperationRecordLog.OperationType.SCRAP, resource = OperationRecordLog.OperationResource.CHANNEL, description = OperationRecordLog.OperationDescription.SCRAP_CHANNEL)
    public ServiceResult<?> scrap(@PathVariable int id) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return channelService.scrapChannel(id);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PutMapping("/{id}/open")
    @OperationRecord(type = OperationRecordLog.OperationType.OPEN, resource = OperationRecordLog.OperationResource.CHANNEL, description = OperationRecordLog.OperationDescription.OPEN_CHANNEL)
    public ServiceResult<?> open(@PathVariable int id) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return channelService.openChannel(id);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PutMapping("/{id}/edit")
    @OperationRecord(type = OperationRecordLog.OperationType.UPDATE, resource = OperationRecordLog.OperationResource.CHANNEL, description = OperationRecordLog.OperationDescription.UPDATE_CHANNEL)
    public ServiceResult<?> edit(@PathVariable int id, @RequestBody Channel channel) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return ServiceResultConstants.SERVICE_NOT_SUPPORT;
    }


    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping("/{id}")
    public ServiceResult<?> find(@PathVariable int id) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return channelService.findChannel(id);
    }

}
