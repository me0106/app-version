package com.tairanchina.csp.avm.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.tairanchina.csp.avm.common.Json;
import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.App;
import com.tairanchina.csp.avm.entity.RnPackage;
import com.tairanchina.csp.avm.entity.RnRoute;
import com.tairanchina.csp.avm.mapper.RnPackageMapper;
import com.tairanchina.csp.avm.mapper.RnRouteMapper;
import com.tairanchina.csp.avm.service.AppService;
import com.tairanchina.csp.avm.service.RnService;
import com.tairanchina.csp.avm.utils.VersionCompareUtils;
import com.tairanchina.csp.avm.wapper.Ordered;
import io.mybatis.mapper.example.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hzlizx on 2018/6/22 0022
 */
@Service
public class RnServiceImpl implements RnService {

    private static final Logger logger = LoggerFactory.getLogger(RnServiceImpl.class);

    @Autowired
    private RnRouteMapper rnRouteMapper;

    @Autowired
    private RnPackageMapper rnPackageMapper;

    @Autowired
    private AppService appService;

    @Override
    public ServiceResult<?> route(String version, String appId, String platform, int routeStatus) {
        App appSelected = appService.findAppByTenantAppId(appId);
        if (appSelected == null) {
            return ServiceResultConstants.APP_NOT_EXISTS;
        }
        Example<RnRoute> wrapper = new Example<>();
        final Example.Criteria<RnRoute> criteria = wrapper.createCriteria();
        criteria.andEqualTo(RnRoute::getAppId, appSelected.getId());
        criteria.andEqualTo(RnRoute::getRouteStatus, routeStatus);
        criteria.andEqualTo(RnRoute::getDelFlag, 0);
        if ("android".equals(platform)) {
            criteria.andEqualTo(RnRoute::getAndroidEnabled, 1);
        } else if ("ios".equals(platform)) {
            criteria.andEqualTo(RnRoute::getIosEnabled, 1);
        } else {
            return ServiceResultConstants.PLATFORM_ERROR;
        }
        wrapper.orderByDesc(RnRoute::getCreatedTime);
        List<RnRoute> rnRoutes = rnRouteMapper.selectByExample(wrapper);

        List<RnRoute> rnRoutesResult = new ArrayList<>();
        if ("ios".equalsIgnoreCase(platform)) {
            for (RnRoute r : rnRoutes) {
                if (VersionCompareUtils.compareVersion(r.getIosMax(), version) > 0 && VersionCompareUtils.compareVersion(version, r.getIosMin()) >= 0) {
                    rnRoutesResult.add(r);
                }
            }
        } else {
            for (RnRoute r : rnRoutes) {
                if (VersionCompareUtils.compareVersion(r.getAndroidMax(), version) > 0 && VersionCompareUtils.compareVersion(version, r.getAndroidMin()) >= 0) {
                    rnRoutesResult.add(r);
                }
            }
        }

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        rnRoutesResult.forEach(action -> map.put(action.getRouteKey(), action.getRouteValue()));
        return ServiceResult.ok(map);
    }

    @Override
    public ServiceResult<?> bundles(String version, String appId, String platform, int rnStatus) {
        App appSelected = appService.findAppByTenantAppId(appId);
        if (appSelected == null) {
            return ServiceResultConstants.APP_NOT_EXISTS;
        }
        Example<RnPackage> example = new Example<>();
        final Example.Criteria<RnPackage> wrapper = example.createCriteria();
        if ("android".equals(platform)) {
            wrapper.andEqualTo(RnPackage::getRnType, 1);
        } else if ("ios".equals(platform)) {
            wrapper.andEqualTo(RnPackage::getRnType, 2);
        } else {
            return ServiceResultConstants.PLATFORM_ERROR;
        }
        wrapper.andEqualTo(RnPackage::getDelFlag, 0);
        Ordered.orderByVersion(example,"rn_version");
        wrapper.andEqualTo(RnPackage::getAppId, appSelected.getId());
        wrapper.andEqualTo(RnPackage::getRnStatus, rnStatus);
        List<RnPackage> rnPackages = rnPackageMapper.selectByExample(example);

        List<RnPackage> rnPackagesResult = new ArrayList<>();
        for (RnPackage r : rnPackages) {
            if (VersionCompareUtils.compareVersion(r.getVersionMax(), version) > 0 && VersionCompareUtils.compareVersion(version, r.getVersionMin()) >= 0) {
                rnPackagesResult.add(r);
            }
        }
        logger.debug("rnPackagesResult={}", Json.toJsonString(rnPackagesResult));
        HashSet<String> set = new HashSet<>();
        List<HashMap<String, Object>> collect = rnPackagesResult.stream().filter(mapper -> !set.contains(mapper.getRnName())).map(mapper -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", mapper.getRnName());
            map.put("version", mapper.getRnVersion());
            map.put("url", mapper.getResourceUrl());
            set.add(mapper.getRnName());
            return map;
        }).collect(Collectors.toList());
        return ServiceResult.ok(collect);
    }
}
