package com.tairanchina.csp.avm.controller;

import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.AndroidVersionRequestDTO;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.AndroidVersion;
import com.tairanchina.csp.avm.entity.OperationRecordLog;
import com.tairanchina.csp.avm.utils.StringUtilsExt;
import com.tairanchina.csp.avm.wapper.ExtWrapper;
import com.tairanchina.csp.avm.annotation.OperationRecord;
import com.tairanchina.csp.avm.service.AndroidVersionService;
import com.tairanchina.csp.avm.service.BasicService;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import io.mybatis.mapper.example.Example;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Created by hzlizx on 2018/6/11 0011
 */
@Tag(name = "安卓版本相关接口")
@RestController
@RequestMapping("/android")
public class AndroidController {

    @Autowired
    private AndroidVersionService androidVersionService;

    @Autowired
    private BasicService basicService;

    @Operation(
        description = "版本列表查询和分页",
        summary = "版本列表查询和分页"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
        @Parameter(name = "page", description = "页数", example = "1"),
        @Parameter(name = "pageSize", description = "每页显示数据条数", example = "10"),
        @Parameter(name = "appVersion", description = "版本号"),
        @Parameter(name = "updateType", description = "更新类型，0：强制更新 1：一般更新 2：静默更新 3：可忽略更新 4：静默可忽略更新"),
        @Parameter(name = "versionStatus", description = "上架状态，0-未上架；1-已上架"),
    })
    @GetMapping
    public ServiceResult list(@RequestParam(required = false, defaultValue = "1") int page,
                              @RequestParam(required = false, defaultValue = "10") int pageSize,
                              @RequestParam(required = false, defaultValue = "") String appVersion,
                              @RequestParam(required = false, defaultValue = "") Integer updateType,
                              @RequestParam(required = false, defaultValue = "") Integer versionStatus) {
        Example<AndroidVersion> example = new Example<>();
        final Example.Criteria<AndroidVersion> wrapper = example.createCriteria();
        wrapper.andEqualTo(AndroidVersion::getAppId, ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId());
        wrapper.andEqualTo(AndroidVersion::getDelFlag, 0);
        ExtWrapper.orderByVersion(example, "app_version");
        if (StringUtils.hasLength(appVersion)) {
            wrapper.andLike(AndroidVersion::getAppVersion, "%" + appVersion + "%");
        }
        if (updateType != null) {
            wrapper.andEqualTo(AndroidVersion::getUpdateType, updateType);
        }
        if (versionStatus != null) {
            wrapper.andEqualTo(AndroidVersion::getVersionStatus, versionStatus);
        }
        return androidVersionService.listSort(page, pageSize, example);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PostMapping
    @OperationRecord(type = OperationRecordLog.OperationType.CREATE, resource = OperationRecordLog.OperationResource.ANDROID_VERSION, description = OperationRecordLog.OperationDescription.CREATE_ANDROID_VERSION)
    public ServiceResult create(@RequestBody AndroidVersionRequestDTO androidVersionRequestDTO) {
        String appVersion = androidVersionRequestDTO.getAppVersion();
        String allowLowestVersion = androidVersionRequestDTO.getAllowLowestVersion();
        String versionDescription = androidVersionRequestDTO.getVersionDescription();
        Integer updateType = androidVersionRequestDTO.getUpdateType();
        if (StringUtilsExt.hasEmpty(appVersion, allowLowestVersion, versionDescription) || updateType == null) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        if (appVersion.length() > 32) {
            return ServiceResultConstants.VERSION_TOO_LONG;
        }
        if (allowLowestVersion.length() > 32) {
            return ServiceResultConstants.VERSION_TOO_LONG;
        }
        //校验版本区间
        if (StringUtilsExt.hasNotBlank(allowLowestVersion, appVersion)) {
            if (basicService.compareVersion(appVersion, allowLowestVersion) < 0) {
                return ServiceResultConstants.ALLOWLOWESTVERSION_BIG_THAN_APPVERSION;
            }
        }
        AndroidVersion androidVersion = new AndroidVersion();
        BeanUtils.copyProperties(androidVersionRequestDTO, androidVersion);
        androidVersion.setId(null); //使用数据库自增ID
        return androidVersionService.createAndroidVersion(androidVersion);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PutMapping("/{id}")
    @OperationRecord(type = OperationRecordLog.OperationType.UPDATE, resource = OperationRecordLog.OperationResource.ANDROID_VERSION, description = OperationRecordLog.OperationDescription.UPDATE_ANDROID_VERSION)
    public ServiceResult update(@RequestBody AndroidVersionRequestDTO androidVersionRequestDTO, @PathVariable int id) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }

        String appVersion = androidVersionRequestDTO.getAppVersion();
        String allowLowestVersion = androidVersionRequestDTO.getAllowLowestVersion();
        //校验版本区间
        if (StringUtilsExt.hasNotBlank(allowLowestVersion, appVersion) && basicService.compareVersion(appVersion, allowLowestVersion) < 0) {
            return ServiceResultConstants.ALLOWLOWESTVERSION_BIG_THAN_APPVERSION;
        }
        AndroidVersion androidVersion = new AndroidVersion();
        BeanUtils.copyProperties(androidVersionRequestDTO, androidVersion);
        androidVersion.setId(id);
        return androidVersionService.updateAndroidVersion(androidVersion);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @DeleteMapping("/{id}")
    @OperationRecord(type = OperationRecordLog.OperationType.DELETE, resource = OperationRecordLog.OperationResource.ANDROID_VERSION, description = OperationRecordLog.OperationDescription.DELETE_ANDROID_VERSION)
    public ServiceResult delete(@PathVariable int id) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return androidVersionService.deleteAndroidVersion(id);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping("/versions")
    public ServiceResult versions() {
        return androidVersionService.listAllVersion();
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping("/{id}")
    public ServiceResult get(@PathVariable int id) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return androidVersionService.findById(id);
    }


    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PutMapping("/{id}/delivery")
    @OperationRecord(type = OperationRecordLog.OperationType.DELIVERY, resource = OperationRecordLog.OperationResource.ANDROID_VERSION, description = OperationRecordLog.OperationDescription.DELIVERY_ANDROID_VERSION)
    public ServiceResult delivery(@PathVariable int id) {
        return androidVersionService.delivery(id);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PutMapping("/{id}/undelivery")
    @OperationRecord(type = OperationRecordLog.OperationType.UNDELIVERY, resource = OperationRecordLog.OperationResource.ANDROID_VERSION, description = OperationRecordLog.OperationDescription.UNDELIVERY_ANDROID_VERSION)
    public ServiceResult undelivery(@PathVariable int id) {
        return androidVersionService.undelivery(id);
    }
}
