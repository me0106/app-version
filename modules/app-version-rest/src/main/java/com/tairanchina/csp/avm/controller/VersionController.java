package com.tairanchina.csp.avm.controller;

import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.Apk;
import com.tairanchina.csp.avm.service.AndroidVersionService;
import com.tairanchina.csp.avm.service.IosVersionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by hzlizx on 2018/6/14 0014
 */
@Tag(name = "版本相关接口")
@RestController
@RequestMapping("/v")
@PropertySource("classpath:application.yml")
public class VersionController {
    private static final Logger logger = LoggerFactory.getLogger(VersionController.class);

    @Autowired
    private AndroidVersionService androidVersionService;

    @Autowired
    private IosVersionService iosVersionService;

    @Value("${rest.base.url}")
    private String baseUrl;

    /**
     * 查询新版本
     *
     * @param tenantAppId 应用ID
     * @param version     应用当前版本号
     * @param channelCode 渠道号
     * @param platform    平台（ios/android）
     * @return
     */
    @Operation(description = "查询新版本", summary = "根据当前应用的版本号，渠道号，平台获取当前应用的最新版本")
    @Parameters({
        @Parameter(name = "tenantAppId", description = "应用appId", example = "uc28ec7f8870a6e785", required = true),
        @Parameter(name = "version", description = "版本号", required = true),
        @Parameter(name = "channelCode", description = "渠道码，官方渠道的渠道码为official", required = true),
        @Parameter(name = "platform", description = "平台，值应为 ios 或 android", required = true),
    })
    @GetMapping("/{tenantAppId}-{version}-{channelCode}-{platform}")
    public ServiceResult<?> version(@PathVariable String tenantAppId,
                                 @PathVariable String version,
                                 @PathVariable String channelCode,
                                 @PathVariable String platform) {
        logger.info("appId={},version={},channelCode={},platform={}", tenantAppId, version, channelCode, platform);
        if ("android".equalsIgnoreCase(platform)) {
            ServiceResult<?> serviceResult = androidVersionService.findNewestVersion(tenantAppId, version, channelCode);
            if (serviceResult.getCode() == 200) {
                HashMap<String, Object> data = (HashMap<String, Object>) serviceResult.getData();
                String downloadUrl = (String) data.get("downloadUrl");
//                if("prod".equals(active)){
//                    downloadUrl = baseUrl + downloadUrl;
//                }else {
//
//                    String protocol = request.getScheme();
//                    if(request.getServerPort()==80){
//                        downloadUrl = protocol.toLowerCase()+"://"+request.getServerName()+downloadUrl;
//                    }else {
//                        downloadUrl = protocol.toLowerCase()+"://"+request.getServerName()+":"+request.getServerPort()+downloadUrl;
//                    }
//                }
                downloadUrl = baseUrl + downloadUrl;
                data.put("downloadUrl", downloadUrl);
                return ServiceResult.ok(data);
            }
            return androidVersionService.findNewestVersion(tenantAppId, version, channelCode);
        } else if ("ios".equalsIgnoreCase(platform)) {
            return iosVersionService.findNewestVersion(tenantAppId, version);
        } else {
            return ServiceResultConstants.PLATFORM_ERROR;
        }
    }

    /**
     * 下载某个版本
     *
     * @param apkId APK的ID
     * @return 跳转下载
     */
    @Operation(description = "下载APK，根据APK包ID", summary = "根据给定的APK_ID下载相应的APK包")
    @Parameters({
        @Parameter(name = "apkId", description = "APK包的ID", required = true),
    })
    @GetMapping("/download/{apkId}")
    public ServiceResult<?> download(@PathVariable int apkId) throws IOException {
        logger.info("下载了APK[{}]", apkId);

        if (apkId < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        } else {
            ServiceResult<?> serviceResult = androidVersionService.getDownloadUrl(apkId);
            if (serviceResult.getCode() != 200) {
                return serviceResult;
            } else {
                //跳转下载
                Apk apk = (Apk) serviceResult.getData();
                HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
                response.sendRedirect(apk.getOssUrl());
                return ServiceResultConstants.STARTED_DOWNLOAD;
            }
        }
    }

    /**
     * 下载最新的APK包
     *
     * @return 跳转下载
     */
    @Operation(description = "直接下载应用的最新APK包", summary = "根据应用ID下载最新的APK包，默认下载官方渠道的最新APK包")
    @GetMapping("/apk/download/{appId}")
    @Parameters({
        @Parameter(name = "appId", description = "应用的appId", example = "uc28ec7f8870a6e785", required = true),
        @Parameter(name = "channelCode", description = "渠道码，非必填，默认为下载官方渠道的APK包", example = "official"),
    })
    public ServiceResult<?> downloadAPK(@PathVariable String appId, @RequestParam(required = false) String channelCode) {
        return androidVersionService.findNewestApk(appId, channelCode);
    }
}
