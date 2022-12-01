package com.tairanchina.csp.avm.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.RnPackage;
import com.tairanchina.csp.avm.mapper.RnPackageMapper;
import com.tairanchina.csp.avm.service.BasicService;
import com.tairanchina.csp.avm.service.RnPackageService;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import com.tairanchina.csp.avm.utils.VersionCompareUtils;
import io.mybatis.mapper.example.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

/**
 * Created by hzlizx on 2018/6/20 0020
 */
@Service
public class RnPackageServiceImpl implements RnPackageService {

    @Autowired
    private RnPackageMapper rnPackageMapper;

    @Autowired
    private BasicService basicService;

    @Override
    public ServiceResult<?> create(RnPackage rnPackage) {
        rnPackage.setId(null);
        rnPackage.setAppId(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId());
        rnPackage.setCreatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        int insert = rnPackageMapper.insert(rnPackage);
        if (insert > 0) {
            return ServiceResult.ok(rnPackage);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult<?> delete(int id) {
        RnPackage rnPackage = rnPackageMapper.selectById(id);
        if (rnPackage == null) {
            return ServiceResultConstants.RN_ROUTE_NOT_EXISTS;
        }
        if (!rnPackage.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        rnPackage.setDelFlag(1);
        int integer = rnPackageMapper.updateById(rnPackage);
        if (integer > 0) {
            return ServiceResult.ok(rnPackage);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult<?> update(RnPackage rnPackage) {
        if (rnPackage.getId() == null || rnPackage.getId() < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        RnPackage rnPackageSelected = rnPackageMapper.selectById(rnPackage.getId());
        if (rnPackageSelected == null) {
            return ServiceResultConstants.RN_PACKAGE_NOT_EXISTS;
        }
        if (!rnPackageSelected.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        rnPackage.setAppId(null);
        rnPackage.setCreatedBy(null);
        int integer = rnPackageMapper.updateById(rnPackage);
        if (integer > 0) {
            return ServiceResult.ok(rnPackage);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult<?> list(int page, int pageSize, Example<RnPackage> wrapper) {
        final Integer appId = ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId();
        wrapper.createCriteria().andEqualTo(RnPackage::getAppId, appId);
        final Page<RnPackage> packages = rnPackageMapper.selectPage(PageRequest.of(page, pageSize), wrapper);
        basicService.formatCreatedBy(packages);
        return ServiceResult.ok(packages);
    }

    @Override
    public ServiceResult<?> listSort(int page, int pageSize, Example<RnPackage> wrapper) {
        final Integer appId = ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId();
        wrapper.createCriteria().andEqualTo(RnPackage::getAppId, appId);
        List<RnPackage> rnPackages = rnPackageMapper.selectByExample(wrapper);
        rnPackages.sort((o1, o2) -> VersionCompareUtils.compareVersion(o2.getRnVersion(), o1.getRnVersion()));
        List<RnPackage> pageList = rnPackages.subList((page - 1) * pageSize, Math.min(pageSize * page, rnPackages.size()));
        final Page<RnPackage> packages = PageableExecutionUtils.getPage(pageList, PageRequest.of(page, pageSize), pageList::size);
        basicService.formatCreatedBy(packages);
        return ServiceResult.ok(packages);
    }

    @Override
    public ServiceResult<?> findBetweenVersionList(String version1, String version2, Example<RnPackage> wrapper) {
        List<RnPackage> rnPageVersions = rnPackageMapper.selectByExample(wrapper);
        String max = VersionCompareUtils.compareVersion(version1, version2) >= 0 ? version1 : version2;
        String min = VersionCompareUtils.compareVersion(version1, version2) <= 0 ? version1 : version2;
        List<RnPackage> versionList =
            rnPageVersions.stream().filter(o -> VersionCompareUtils.compareVersion(o.getRnVersion(), min) >= 0 && VersionCompareUtils.compareVersion(max, o.getRnVersion()) >= 0)
                .sorted((o1, o2) -> VersionCompareUtils.compareVersion(o2.getRnVersion(), o1.getRnVersion())).collect(Collectors.toList());
        return ServiceResult.ok(versionList);
    }

    @Override
    public ServiceResult<?> find(int id) {
        RnPackage rnPackage = rnPackageMapper.selectById(id);
        if (rnPackage == null) {
            return ServiceResultConstants.RN_ROUTE_NOT_EXISTS;
        }
        if (!rnPackage.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        basicService.formatCreatedBy(rnPackage);
        return ServiceResult.ok(rnPackage);
    }
}
