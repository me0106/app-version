package com.tairanchina.csp.avm.controller;

import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.OperationRecordLog;
import com.tairanchina.csp.avm.service.OperationRecordLogService;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "操作日志相关接口")
@RestController
@RequestMapping("/log")
public class OperationRecordLogController {

    @Autowired
    OperationRecordLogService operationRecordLogService;


    @Operation(
        description = "根据操作日志ID查询单条操作日志信息",
        summary = "根据操作日志ID查询单条操作日志信息"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
        @Parameter(name = "id", description = "操作日志id"),
    })
    @GetMapping("/{id}")
    public ServiceResult getOperationRecordLog(@PathVariable Integer id) {
        return operationRecordLogService.getOperationRecordLogById(id);
    }

    @Operation(
        summary = "列出全部操作日志（可分页，查询）",
        description = "列出全部操作日志（可分页，查询）"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
        @Parameter(name = "page", description = "页数", example = "1"),
        @Parameter(name = "pageSize", description = "每页显示数据条数", example = "10"),
        @Parameter(name = "operationType", description = "操作描述，这里的操作类型实际为操作描述（中文的），是根据操作资源查对应的操作类型查出来的"),
        @Parameter(name = "appId", description = "appId，int型的"),
        @Parameter(name = "phone", description = "手机号"),
        @Parameter(name = "nickName", description = "用户昵称"),
        @Parameter(name = "startDate", description = "查询开始时间"),
        @Parameter(name = "endDate", description = "查询结束时间"),
    })
    @GetMapping
    public ServiceResult list(@RequestParam(required = false, defaultValue = "1") int page,
                              @RequestParam(required = false, defaultValue = "10") int pageSize,
                              @RequestParam(required = false) String operationType, //这里的操作类型实际为操作描述（中文的），是根据操作资源查对应的操作类型查出来的
                              @RequestParam(required = false) String operationResource,
                              @RequestParam(required = false) Integer appId,
                              @RequestParam(required = false) String phone,
                              @RequestParam(required = false) String nickName,
                              @RequestParam(required = false) String startDate,
                              @RequestParam(required = false) String endDate) {
        return operationRecordLogService.getListByQuery(page, pageSize, phone, nickName, appId, operationResource, operationType, null, startDate, endDate);
    }


    @PutMapping("/delete/{id}")
//    @OperationRecord(type = OperationRecordLog.OperationType.DELETE, resource = OperationRecordLog.OperationResource.OPETATION_RECORD_LOG, description = OperationRecordLog.OperationDescription.DELETE_OPETATION_RECORD_LOG)
    public ServiceResult delete(@PathVariable Integer id) {
        return operationRecordLogService.deleteOperationRecordLog(id);
    }

    @DeleteMapping("/{id}")
//    @OperationRecord(type = OperationRecordLog.OperationType.DELETE_FOREVER, resource = OperationRecordLog.OperationResource.OPETATION_RECORD_LOG, description = OperationRecordLog.OperationDescription.DELETE_FOREVER_OPETATION_RECORD_LOG)
    public ServiceResult deleteForever(@PathVariable Integer id) {
        return operationRecordLogService.deleteOperationRecordLogForever(id);
    }

    @Operation(
        summary = "操作类型显示",
        description = "详细接口说明：https://doc.trc.com/#/module/BB4C6A2C8B654BB096519D35C667585F/service/DEE2FECC554B40D3A50525C9386F39B1 "
    )
    @GetMapping("/type")
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    public ServiceResult getOperationType() {
        Map<Enum, String> typeMap = new HashMap<>();
        for (OperationRecordLog.OperationType t : OperationRecordLog.OperationType.values()) {
            typeMap.put(t, t.getName());
        }
        return ServiceResult.ok(typeMap);
    }

    @Operation(
        summary = "操作资源显示",
        description = "详细接口说明：https://doc.trc.com/#/module/BB4C6A2C8B654BB096519D35C667585F/service/3857DF13437A41E08FA1B6B17756574F "
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping("/resource")
    public ServiceResult getOperationResource() {
        Map<Enum, String> resourceMap = new HashMap<>();
        for (OperationRecordLog.OperationResource r : OperationRecordLog.OperationResource.values()) {
            resourceMap.put(r, r.getName());
        }
        return ServiceResult.ok(resourceMap);
    }

    @Operation(
        summary = "根据操作资源显示操作类型",
        description = "详细接口说明：https://doc.trc.com/#/module/BB4C6A2C8B654BB096519D35C667585F/service/56A3F4A40B094CA3A77D38AF33032E3B "
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping("/{resource}/type")
    public ServiceResult getOperationResourceType(@PathVariable String resource) {
        Map<Enum, String> resourceMap = new HashMap<>();
        for (OperationRecordLog.OperationDescription r : OperationRecordLog.OperationDescription.values()) {
            if (r.toString().endsWith("_" + resource.toUpperCase())) {
                resourceMap.put(r, r.getDescription());
            }
        }
        return ServiceResult.ok(resourceMap);
    }

}
