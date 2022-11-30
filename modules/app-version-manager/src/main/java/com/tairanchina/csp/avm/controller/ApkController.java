package com.tairanchina.csp.avm.controller;

import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.dto.UploadFileEntity;
import com.tairanchina.csp.avm.entity.Apk;
import com.tairanchina.csp.avm.entity.Channel;
import com.tairanchina.csp.avm.entity.OperationRecordLog;
import com.tairanchina.csp.avm.annotation.OperationRecord;
import com.tairanchina.csp.avm.service.ApkService;
import com.tairanchina.csp.avm.service.ChannelService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


/**
 * Created by hzlizx on 2018/6/13 0013
 */


@RestController
@RequestMapping("/apk")
public class ApkController {
    private static final Logger logger = LoggerFactory.getLogger(ApkController.class);

    @Autowired
    private ApkService apkService;

    @Autowired
    private ChannelService channelService;


    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PostMapping("/upload")
    public ServiceResult upload() {
        return ServiceResult.ok(null);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
        @Parameter(name = "page", description = "页数", example = "1"),
        @Parameter(name = "pageSize", description = "每页显示数据条数", example = "10"),
        @Parameter(name = "versionId", description = "版本id"),
        @Parameter(name = "channelCode", description = "渠道码"),
        @Parameter(name = "md5", description = "md5"),
        @Parameter(name = "deliveryStatus", description = "上架状态，0-未上架；1-已上架"),
    })
    @GetMapping
    public ServiceResult list(@RequestParam(required = false, defaultValue = "1") int page,
                              @RequestParam(required = false, defaultValue = "10") int pageSize,
                              @RequestParam(defaultValue = "0") int versionId,
                              @RequestParam(required = false, defaultValue = "") String channelCode,
                              @RequestParam(required = false, defaultValue = "") String md5,
                              @RequestParam(required = false, defaultValue = "") Integer deliveryStatus) {
        if (versionId < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return apkService.getApkPageWithChannelCode(page, pageSize, versionId, channelCode, md5, deliveryStatus);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PostMapping
    @RequestMapping(produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
    @OperationRecord(type = OperationRecordLog.OperationType.CREATE, resource = OperationRecordLog.OperationResource.APK, description = OperationRecordLog.OperationDescription.CREATE_APK)
    public ServiceResult create(@RequestBody UploadFileEntity uploadFileEntity) {
        Apk apk = new Apk();
        ServiceResult serviceResult = channelService.findByChannelCode(uploadFileEntity.getChannel());
        if (serviceResult.getCode() == 200) {
            Channel channel = (Channel) serviceResult.getData();
            apk.setChannelId(channel.getId());
            apk.setVersionId(uploadFileEntity.getVersionId());
            apk.setOssUrl(uploadFileEntity.getOssUrl());
            apk.setMd5(uploadFileEntity.getMd5());
            return apkService.create(apk);
        } else {
            logger.info("找不到Channel");
            return ServiceResult.failed(
                ServiceResultConstants.APK_SAVE_ERROR.getCode(),
                "文件[ " + uploadFileEntity.getFileName() + " ]录入失败，原因：" + serviceResult.getMessage()
            );
        }
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PutMapping("/{id}/delivery")
    @OperationRecord(type = OperationRecordLog.OperationType.DELIVERY, resource = OperationRecordLog.OperationResource.APK, description = OperationRecordLog.OperationDescription.DELIVERY_APK)
    public ServiceResult delivery(@PathVariable int id) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return apkService.delivery(id);
    }


    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PutMapping("/{id}/undelivery")
    @OperationRecord(type = OperationRecordLog.OperationType.UNDELIVERY, resource = OperationRecordLog.OperationResource.APK, description = OperationRecordLog.OperationDescription.UNDELIVERY_APK)
    public ServiceResult undelivery(@PathVariable int id) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return apkService.undelivery(id);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @DeleteMapping("/{id}")
    @OperationRecord(type = OperationRecordLog.OperationType.DELETE, resource = OperationRecordLog.OperationResource.APK, description = OperationRecordLog.OperationDescription.DELETE_APK)
    public ServiceResult delete(@PathVariable int id) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return apkService.delete(id);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping("/channel/check")
    public ServiceResult checkChannel(@RequestParam String channelCode) {
        if (StringUtils.isEmpty(channelCode)) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        ServiceResult channel = channelService.findByChannelCode(channelCode);
        if (channel.getCode() == 200) {
            return ServiceResult.ok(true);
        } else {
            return ServiceResult.ok(false);
        }
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping("/exists")
    public ServiceResult exists(@RequestParam String channelCode,
                                @RequestParam Integer versionId) {
        if (StringUtils.isEmpty(channelCode) || versionId == null || versionId < 0) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return apkService.exists(channelCode, versionId);
    }
}
