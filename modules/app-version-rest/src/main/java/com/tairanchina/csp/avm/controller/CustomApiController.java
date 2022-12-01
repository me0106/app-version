package com.tairanchina.csp.avm.controller;

import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.utils.StringUtilsExt;
import com.tairanchina.csp.avm.service.CustomApiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Created by hzlizx on 2018/6/14 0014
 */
@Tag( name = "自定义接口相关")
@RestController
@RequestMapping("/c")
public class CustomApiController {

    private static final Logger logger = LoggerFactory.getLogger(CustomApiController.class);

    @Autowired
    private CustomApiService customApiService;

    @Operation(
            description = "查询自定义接口",
            summary = "根据应用ID、接口KEY、版本、平台获取自定义接口信息"
    )
    @Parameters({
            @Parameter(name = "tenantAppId", description = "应用appId",  example = "uc28ec7f8870a6e785", required = true),
            @Parameter(name = "key", description = "自定义接口的key", required = true),
            @Parameter(name = "platform", description = "平台，值应为 ios 或 android", required = true),
            @Parameter(name = "version", description = "版本号", required = true),
    })
    @GetMapping("/{tenantAppId}/{key}/{version}/{platform}")
    public ServiceResult<?> custom(@PathVariable String tenantAppId,
                                @PathVariable String key,
                                @PathVariable String platform,
                                @PathVariable String version) {
        logger.info("version: " + version);
        if (StringUtilsExt.hasBlank(tenantAppId, key, platform, version)) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        if (!platform.equalsIgnoreCase("ios") && !platform.equalsIgnoreCase("android")) {
            return ServiceResultConstants.PLATFORM_ERROR;
        }

        return customApiService.getCustomContent(tenantAppId, key, platform, version);
    }

}
