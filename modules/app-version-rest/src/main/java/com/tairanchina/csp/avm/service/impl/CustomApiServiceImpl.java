package com.tairanchina.csp.avm.service.impl;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.tairanchina.csp.avm.common.Json;
import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.App;
import com.tairanchina.csp.avm.entity.CustomApi;
import com.tairanchina.csp.avm.mapper.CustomApiMapper;
import com.tairanchina.csp.avm.service.AppService;
import com.tairanchina.csp.avm.service.CustomApiService;
import com.tairanchina.csp.avm.utils.VersionCompareUtils;
import io.mybatis.mapper.example.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hzlizx on 2018/6/28 0028
 */
@Service
public class CustomApiServiceImpl implements CustomApiService {
    private static final Logger logger = LoggerFactory.getLogger(CustomApiServiceImpl.class);

    @Autowired
    private CustomApiMapper customApiMapper;

    @Autowired
    private AppService appService;

    @Override
    public ServiceResult<?> getCustomContent(String tenantAppId, String key, String platform, String version) {
        App app = appService.findAppByTenantAppId(tenantAppId);
        if (app == null) {
            return ServiceResultConstants.APP_NOT_EXISTS;
        }
        final Example<CustomApi> example = customApiMapper.example();
        final Example.Criteria<CustomApi> criteria = example.createCriteria();
        criteria.andEqualTo(CustomApi::getAppId, app.getId())
            .andEqualTo(CustomApi::getCustomKey, key)
            .andEqualTo(CustomApi::getCustomStatus, 1)
            .andEqualTo(CustomApi::getDelFlag, 0);
        if ("ios".equalsIgnoreCase(platform)) {
            criteria.andEqualTo(CustomApi::getIosEnabled, 1);
        } else if ("android".equalsIgnoreCase(platform)) {
            criteria.andEqualTo(CustomApi::getAndroidEnabled, 1);
        }
        example.orderByDesc(CustomApi::getCreatedTime);
        List<CustomApi> customApis = customApiMapper.selectByExample(example);
        if (customApis.isEmpty()) {
            return ServiceResultConstants.CUSTOM_API_NOT_FOUND;
        }

        CustomApi customApiNew = null;
        if ("ios".equalsIgnoreCase(platform)) {
            for (CustomApi c : customApis) {
                if (VersionCompareUtils.compareVersion(c.getIosMax(), version) > 0 && VersionCompareUtils.compareVersion(version, c.getIosMin()) >= 0) {
                    customApiNew = c;
                    break;
                }
            }
        } else if ("android".equalsIgnoreCase(platform)) {
            for (CustomApi c : customApis) {
                if (VersionCompareUtils.compareVersion(c.getAndroidMax(), version) > 0 && VersionCompareUtils.compareVersion(version, c.getAndroidMin()) >= 0) {
                    customApiNew = c;
                    break;
                }
            }
        }
        if (null == customApiNew) {
            return ServiceResultConstants.CUSTOM_API_NOT_FOUND;
        }

        String customContent = customApiNew.getCustomContent();
        try {
            JsonNode jsonNode = Json.getMapper().readTree(customContent);
            return ServiceResult.ok(jsonNode);
        } catch (IOException e) {
            logger.error("渲染JSON出错", e);
            return ServiceResultConstants.ERROR_500;
        }
    }
}
