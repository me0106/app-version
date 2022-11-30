package com.tairanchina.csp.avm.controller;

import com.tairanchina.csp.avm.dto.AppRequestDTO;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.OperationRecordLog;
import com.tairanchina.csp.avm.annotation.OperationRecord;
import com.tairanchina.csp.avm.service.AppService;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by hzlizx on 2018/6/8 0008
 */
@RestController
@RequestMapping("/app")
public class AppController {

    @Autowired
    private AppService appService;

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping
    public ServiceResult app(@RequestParam(required = false, defaultValue = "1") int page,
                             @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return appService.getAppListWithUserId(page, pageSize, ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
    }


    /**
     * 创建APP
     *
     * @param appRequestDTO 应用实体类（参数，主要是appName）
     * @return 是否成功
     */
    @Operation(
        description = "创建APP",
        summary = "创建APP"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @OperationRecord(type = OperationRecordLog.OperationType.CREATE, resource = OperationRecordLog.OperationResource.APP, description = OperationRecordLog.OperationDescription.CREATE_APP)
    @PostMapping
    public ServiceResult createApp(@RequestBody AppRequestDTO appRequestDTO) {
        return appService.createApp(appRequestDTO.getAppName(), appRequestDTO.getTenantAppId());
    }


    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping("/bind")
    public ServiceResult myApp() {
        return appService.getMyApp();
    }

}
