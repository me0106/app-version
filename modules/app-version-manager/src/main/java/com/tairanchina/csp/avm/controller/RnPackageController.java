package com.tairanchina.csp.avm.controller;

import com.tairanchina.csp.avm.annotation.OperationRecord;
import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.RnPackageRequestDTO;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.OperationRecordLog;
import com.tairanchina.csp.avm.entity.RnPackage;
import com.tairanchina.csp.avm.service.BasicService;
import com.tairanchina.csp.avm.service.RnPackageService;
import com.tairanchina.csp.avm.utils.StringUtilsExt;
import com.tairanchina.csp.avm.wapper.Ordered;
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
@Tag(name = "RN包相关接口")
@RestController
@RequestMapping("/package")
public class RnPackageController {

    @Autowired
    private BasicService basicService;

    @Autowired
    private RnPackageService rnPackageService;

    @Operation(
        description = "列出当前选择的APP内所有RN包（可分页，查询）",
        summary = "列出当前选择的APP内所有RN包信息（可分页，查询）"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
        @Parameter(name = "page", description = "页数", example = "1"),
        @Parameter(name = "pageSize", description = "每页显示数据条数", example = "10"),
        @Parameter(name = "rnName", description = "模块名称（约定）"),
        @Parameter(name = "rnNickName", description = "RN包模块通用昵称"),
        @Parameter(name = "rnStatus", description = "RN包状态，0:关闭 1:线上开启 2:测试需要"),
    })
    @GetMapping
    public ServiceResult<?> list(@RequestParam(required = false, defaultValue = "1") int page,
                              @RequestParam(required = false, defaultValue = "10") int pageSize,
                              @RequestParam(required = false, defaultValue = "") String rnName,
                              @RequestParam(required = false, defaultValue = "") String rnNickName,
                              @RequestParam(required = false, defaultValue = "") Integer rnStatus) {
        Example<RnPackage> example = new Example<>();
        final Example.Criteria<RnPackage> wrapper = example.createCriteria();
        wrapper.andEqualTo(RnPackage::getDelFlag, 0);
        if (StringUtils.hasLength(rnName)) {
            wrapper.andLike(RnPackage::getRnName, "%" + rnName + "%");
        }
        if (StringUtils.hasLength(rnNickName)) {
            wrapper.andLike(RnPackage::getRnNickName, "%" + rnNickName + "%");
        }
        if (rnStatus != null) {
            wrapper.andEqualTo(RnPackage::getRnStatus, rnStatus);
        }
        Ordered.orderByVersion(example, "rn_version");
        return rnPackageService.listSort(page, pageSize, example);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PostMapping
    @OperationRecord(type = OperationRecordLog.OperationType.CREATE, resource = OperationRecordLog.OperationResource.RN_PACKAGE, description = OperationRecordLog.OperationDescription.CREATE_RN_PACKAGE)
    public ServiceResult<?> create(@RequestBody RnPackageRequestDTO rnPackageRequestDTO) {
        if (StringUtilsExt.hasEmpty(
            rnPackageRequestDTO.getRnName(),
            rnPackageRequestDTO.getRnNickName(),
            rnPackageRequestDTO.getResourceUrl(),
            rnPackageRequestDTO.getRnVersion(),
            rnPackageRequestDTO.getRnUpdateLog()
        )) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        //校验版本区间
        if (StringUtilsExt.hasNotBlank(rnPackageRequestDTO.getVersionMin(), rnPackageRequestDTO.getVersionMax())) {
            if (basicService.compareVersion(rnPackageRequestDTO.getVersionMax(), rnPackageRequestDTO.getVersionMin()) <= 0) {
                return ServiceResultConstants.MIN_BIG_THAN_MAX;
            }
        }

        RnPackage rnPackage = new RnPackage();
        BeanUtils.copyProperties(rnPackageRequestDTO, rnPackage);
        return rnPackageService.create(rnPackage);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @PutMapping("/{id}")
    @OperationRecord(type = OperationRecordLog.OperationType.UPDATE, resource = OperationRecordLog.OperationResource.RN_PACKAGE, description = OperationRecordLog.OperationDescription.UPDATE_RN_PACKAGE)
    public ServiceResult<?> update(@PathVariable int id, @RequestBody RnPackageRequestDTO rnPackageRequestDTO) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        //校验版本区间
        if (StringUtilsExt.hasNotBlank(rnPackageRequestDTO.getVersionMin(), rnPackageRequestDTO.getVersionMax()) &&
            basicService.compareVersion(rnPackageRequestDTO.getVersionMax(), rnPackageRequestDTO.getVersionMin()) <= 0) {
            return ServiceResultConstants.MIN_BIG_THAN_MAX;
        }
        RnPackage rnPackage = new RnPackage();
        BeanUtils.copyProperties(rnPackageRequestDTO, rnPackage);
        rnPackage.setId(id);
        return rnPackageService.update(rnPackage);
    }


    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @DeleteMapping("/{id}")
    @OperationRecord(type = OperationRecordLog.OperationType.DELETE, resource = OperationRecordLog.OperationResource.RN_PACKAGE, description = OperationRecordLog.OperationDescription.DELETE_RN_PACKAGE)
    public ServiceResult<?> delete(@PathVariable int id) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return rnPackageService.delete(id);
    }

    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping("/{id}")
    public ServiceResult<?> find(@PathVariable int id) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return rnPackageService.find(id);
    }
}
