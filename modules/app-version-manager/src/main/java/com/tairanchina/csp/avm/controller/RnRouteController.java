package com.tairanchina.csp.avm.controller;

import com.tairanchina.csp.avm.annotation.OperationRecord;
import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.RnRouteRequestDTO;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.OperationRecordLog;
import com.tairanchina.csp.avm.entity.RnRoute;
import com.tairanchina.csp.avm.service.BasicService;
import com.tairanchina.csp.avm.service.RnRouteService;
import com.tairanchina.csp.avm.utils.StringUtilsExt;
import io.mybatis.mapper.example.Example;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


/**
 * Created by hzlizx on 2018/6/20 0020
 */
@Tag(name = "RN路由相关接口")
@RestController
@RequestMapping("/route")
public class RnRouteController {

    @Autowired
    private RnRouteService rnRouteService;

    @Autowired
    private BasicService basicService;


    @Operation(
        description = "列出当前选择的APP内所有RN路由（可分页，查询）",
        summary = "列出当前选择的APP内所有RN路由信息（可分页，查询）"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
        @Parameter(name = "page", description = "页数", example = "1"),
        @Parameter(name = "pageSize", description = "每页显示数据条数", example = "10"),
        @Parameter(name = "routeName", description = "路由通用昵称"),
        @Parameter(name = "routeKey", description = "被拦截URL（约定）"),
        @Parameter(name = "routeValue", description = "目标URL"),
        @Parameter(name = "routeStatus", description = "RN路由状态，0:关闭 1:线上开启 2:测试需要"),
    })
    @GetMapping
    public ServiceResult list(@RequestParam(required = false, defaultValue = "1") int page,
                              @RequestParam(required = false, defaultValue = "10") int pageSize,
                              @RequestParam(required = false, defaultValue = "") String routeName,
                              @RequestParam(required = false, defaultValue = "") String routeKey,
                              @RequestParam(required = false, defaultValue = "") String routeValue,
                              @RequestParam(required = false, defaultValue = "") Integer routeStatus) {
        Example<RnRoute> example = new Example<>();
        final Example.Criteria<RnRoute> wrapper = example.createCriteria();
        wrapper.andEqualTo(RnRoute::getDelFlag, 0);
        if (StringUtils.hasLength(routeName)) {
            wrapper.andLike(RnRoute::getRouteName, "%" + routeName + "%");
        }
        if (StringUtils.hasLength(routeKey)) {
            wrapper.andLike(RnRoute::getRouteKey, "%" + routeKey + "%");
        }

        if (StringUtils.hasLength(routeValue)) {
            wrapper.andLike(RnRoute::getRouteValue, "%" + routeValue + "%");
        }

        if (routeStatus != null) {
            wrapper.andEqualTo(RnRoute::getRouteStatus, routeStatus);
        }
        example.orderByDesc(RnRoute::getCreatedTime);
        return rnRouteService.list(page, pageSize, example);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PostMapping
    @OperationRecord(type = OperationRecordLog.OperationType.CREATE, resource = OperationRecordLog.OperationResource.RN_ROUTE, description = OperationRecordLog.OperationDescription.CREATE_RN_ROUTE)
    public ServiceResult create(@RequestBody RnRouteRequestDTO rnRouteRequestDTO) {
        if (StringUtilsExt.hasBlank(
            rnRouteRequestDTO.getRouteName(),
            rnRouteRequestDTO.getRouteKey(),
            rnRouteRequestDTO.getRouteValue()
        )) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        //校验版本区间
        ServiceResult serviceResult = basicService.checkVersion(rnRouteRequestDTO);
        if (serviceResult.getCode() != 200) {
            return serviceResult;
        }
        RnRoute rnRoute = new RnRoute();
        BeanUtils.copyProperties(rnRouteRequestDTO, rnRoute);
        return rnRouteService.create(rnRoute);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PutMapping("/{id}")
    @OperationRecord(type = OperationRecordLog.OperationType.UPDATE, resource = OperationRecordLog.OperationResource.RN_ROUTE, description = OperationRecordLog.OperationDescription.UPDATE_RN_ROUTE)
    public ServiceResult update(@PathVariable int id, @RequestBody RnRouteRequestDTO rnRouteRequestDTO) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        //校验版本区间
        ServiceResult serviceResult = basicService.checkVersion(rnRouteRequestDTO);
        if (serviceResult.getCode() != 200) {
            return serviceResult;
        }
        RnRoute rnRoute = new RnRoute();
        BeanUtils.copyProperties(rnRouteRequestDTO, rnRoute);
        rnRoute.setId(id);
        return rnRouteService.update(rnRoute);
    }


    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @DeleteMapping("/{id}")
    @OperationRecord(type = OperationRecordLog.OperationType.DELETE, resource = OperationRecordLog.OperationResource.RN_ROUTE, description = OperationRecordLog.OperationDescription.DELETE_RN_ROUTE)
    public ServiceResult delete(@PathVariable int id) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return rnRouteService.delete(id);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping("/{id}")
    public ServiceResult find(@PathVariable int id) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return rnRouteService.find(id);
    }
}
