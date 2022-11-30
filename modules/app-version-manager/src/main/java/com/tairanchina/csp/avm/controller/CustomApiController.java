package com.tairanchina.csp.avm.controller;

import com.tairanchina.csp.avm.annotation.OperationRecord;
import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.CustomApiRequestDTO;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.CustomApi;
import com.tairanchina.csp.avm.entity.OperationRecordLog;
import com.tairanchina.csp.avm.service.BasicService;
import com.tairanchina.csp.avm.service.CustomApiService;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import io.mybatis.mapper.example.Example;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "自定义接口相关")
@RestController
@RequestMapping("/capi")
public class CustomApiController {

    @Autowired
    private BasicService basicService;

    @Autowired
    private CustomApiService customApiService;

    @Operation(
        description = "列出当前选择的APP内所有RN包（可分页，查询）",
        summary = "列出当前选择的APP内所有RN包信息（可分页，查询）"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
        @Parameter(name = "page", description = "页数", example = "1"),
        @Parameter(name = "pageSize", description = "每页显示数据条数", example = "10"),
        @Parameter(name = "osType", description = "适用终端， ios / android"),
        @Parameter(name = "customName", description = "自定义接口名称"),
    })
    @GetMapping
    public ServiceResult list(@RequestParam(required = false, defaultValue = "1") int page,
                              @RequestParam(required = false, defaultValue = "10") int pageSize,
                              @RequestParam(required = false) String osType,
                              @RequestParam(required = false) String customName) {
        Example<CustomApi> example = new Example<>();
        final Example.Criteria<CustomApi> wrapper = example.createCriteria();
        wrapper.andEqualTo(CustomApi::getAppId, ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId());

        if ("android".equals(osType)) {
            wrapper.andEqualTo(CustomApi::getAndroidEnabled, 1);
        } else if ("ios".equals(osType)) {
            wrapper.andEqualTo(CustomApi::getIosEnabled, 1);
        }
        if (StringUtils.isNotBlank(customName)) {
            wrapper.andLike(CustomApi::getCustomName, "%" + customName + "%");
        }
        wrapper.andEqualTo(CustomApi::getDelFlag, 0);
        example.orderByDesc(CustomApi::getCreatedTime);
        return customApiService.list(page, pageSize, example);
    }

    @Operation(
        description = "根据自定义接口ID查找对应的自定义接口信息",
        summary = "根据ID查找对应的自定义接口信息"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping("/{id}")
    public ServiceResult findCustomApi(@PathVariable Integer id) {
        CustomApi customApi = new CustomApi();
        customApi.setId(id);
        return customApiService.getCustomApiByOne(customApi);
    }

    @Operation(
        description = "添加自定义接口",
        summary = "添加自定义接口"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PostMapping("/add")
    @OperationRecord(type = OperationRecordLog.OperationType.CREATE, resource = OperationRecordLog.OperationResource.CUSTOM_API, description = OperationRecordLog.OperationDescription.CREATE_CUSTOM_API)
    public ServiceResult addCustomApi(@Valid @RequestBody CustomApiRequestDTO customApiRequestDTO) {
        if (StringUtils.isBlank(customApiRequestDTO.getCustomKey())) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        //校验版本区间
        ServiceResult serviceResult = basicService.checkVersion(customApiRequestDTO);
        if (serviceResult.getCode() != 200) {
            return serviceResult;
        }
        CustomApi customApi = new CustomApi();
        BeanUtils.copyProperties(customApiRequestDTO, customApi);
        customApi.setId(null);
        customApi.setDelFlag(null);
        return customApiService.createCustomApi(customApi);
    }

    @Operation(
        description = "编辑自定义接口",
        summary = "修改自定义接口"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PutMapping("/update/{id}")
    @OperationRecord(type = OperationRecordLog.OperationType.UPDATE, resource = OperationRecordLog.OperationResource.CUSTOM_API, description = OperationRecordLog.OperationDescription.UPDATE_CUSTOM_API)
    public ServiceResult updateCustomApi(@PathVariable Integer id, @Valid @RequestBody CustomApiRequestDTO customApiRequestDTO) {
        if (1 > id) {
            return ServiceResult.failed(40001, "id不正确");
        }
        //校验版本区间
        ServiceResult serviceResult = basicService.checkVersion(customApiRequestDTO);
        if (serviceResult.getCode() != 200) {
            return serviceResult;
        }

        CustomApi customApi = new CustomApi();
        BeanUtils.copyProperties(customApiRequestDTO, customApi);
        customApi.setId(id);
        return customApiService.updateCustomApi(customApi);
    }

    /**
     * 硬删
     *
     * @param id
     * @return
     */
    @Operation(
        description = "删除自定义接口（硬删）",
        summary = "删除自定义接口"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @DeleteMapping("/{id}")
    @OperationRecord(type = OperationRecordLog.OperationType.DELETE_FOREVER, resource = OperationRecordLog.OperationResource.CUSTOM_API, description = OperationRecordLog.OperationDescription.DELETE_FOREVER_CUSTOM_API)
    public ServiceResult deleteCustomApiForver(@PathVariable Integer id) {
        return customApiService.deleteCustomApiForver(id);
    }

    /**
     * 软删
     *
     * @param id
     * @return
     */

    @Operation(
        description = "删除自定义接口（软删）",
        summary = "删除自定义接口"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PutMapping("/{id}")
    @OperationRecord(type = OperationRecordLog.OperationType.DELETE, resource = OperationRecordLog.OperationResource.CUSTOM_API, description = OperationRecordLog.OperationDescription.DELETE_CUSTOM_API)
    public ServiceResult deleteCustomApi(@PathVariable Integer id) {
        return customApiService.deleteCustomApi(id);
    }


}
