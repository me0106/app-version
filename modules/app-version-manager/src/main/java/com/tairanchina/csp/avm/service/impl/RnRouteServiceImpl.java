package com.tairanchina.csp.avm.service.impl;

import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.RnRoute;
import com.tairanchina.csp.avm.mapper.RnRouteMapper;
import com.tairanchina.csp.avm.service.BasicService;
import com.tairanchina.csp.avm.service.RnRouteService;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import io.mybatis.mapper.example.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Created by hzlizx on 2018/6/20 0020
 */
@Service
public class RnRouteServiceImpl implements RnRouteService {

    @Autowired
    private RnRouteMapper rnRouteMapper;

    @Autowired
    private BasicService basicService;

    @Override
    public ServiceResult<?> create(RnRoute rnRoute) {
        rnRoute.setId(null);
        rnRoute.setCreatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        rnRoute.setAppId(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId());
        int insert = rnRouteMapper.insert(rnRoute);
        if (insert > 0) {
            return ServiceResult.ok(rnRoute);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult<?> delete(int id) {
        RnRoute rnRoute = rnRouteMapper.selectById(id);
        if (rnRoute == null) {
            return ServiceResultConstants.RN_ROUTE_NOT_EXISTS;
        }
        if (!rnRoute.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        rnRoute.setDelFlag(1);
        int integer = rnRouteMapper.updateById(rnRoute);
        if (integer > 0) {
            return ServiceResult.ok(rnRoute);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult<?> update(RnRoute rnRoute) {
        if (rnRoute.getId() == null || rnRoute.getId() < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        RnRoute rnRouteSelected = rnRouteMapper.selectById(rnRoute.getId());
        if (rnRouteSelected == null) {
            return ServiceResultConstants.RN_ROUTE_NOT_EXISTS;
        }
        if (!rnRouteSelected.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        rnRoute.setAppId(null);
        rnRoute.setCreatedBy(null);
        int integer = rnRouteMapper.updateById(rnRoute);
        if (integer > 0) {
            return ServiceResult.ok(rnRoute);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult<?> list(int page, int pageSize, Example<RnRoute> wrapper) {
        final Integer appId = ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId();
        wrapper.createCriteria().andEqualTo(RnRoute::getAppId, appId);
        final Page<RnRoute> routePage = rnRouteMapper.selectPage(PageRequest.of(page, pageSize), wrapper);
        basicService.formatCreatedBy(routePage);
        return ServiceResult.ok(routePage);
    }

    @Override
    public ServiceResult<?> find(int id) {
        RnRoute rnRoute = rnRouteMapper.selectById(id);
        if (rnRoute == null) {
            return ServiceResultConstants.RN_ROUTE_NOT_EXISTS;
        }
        if (!rnRoute.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        basicService.formatCreatedBy(rnRoute);
        return ServiceResult.ok(rnRoute);
    }
}
