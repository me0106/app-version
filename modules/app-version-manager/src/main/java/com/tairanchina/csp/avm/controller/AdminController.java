package com.tairanchina.csp.avm.controller;

import com.tairanchina.csp.avm.annotation.OperationRecord;
import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.AdminUpdateNickNameRequestDTO;
import com.tairanchina.csp.avm.dto.AppRequestDTO;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.App;
import com.tairanchina.csp.avm.entity.LoginInfo;
import com.tairanchina.csp.avm.entity.OperationRecordLog;
import com.tairanchina.csp.avm.entity.User;
import com.tairanchina.csp.avm.service.AdminService;
import com.tairanchina.csp.avm.service.UserService;
import com.tairanchina.csp.avm.utils.StringUtilsExt;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import io.mybatis.mapper.example.Example;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 管理员操作
 */
@Tag(name = "管理员操作相关接口")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Operation(
        description = "判断当前登录用户是否是管理员",
        summary = "判断当前登录用户是否是管理员"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping("/isAdmin")
    public ServiceResult isAdmin() {
        LoginInfo loginInfo = ThreadLocalUtils.USER_THREAD_LOCAL.get();
        return adminService.isAdmin(loginInfo.getUserId());
    }

    /**
     * 列出所有用户
     *
     * @return 用户列表
     */
    @Operation(
        description = "列出所有用户（可分页，查询）",
        summary = "APP版本管理系统的全部用户列表"
    )
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "用户凭证", required = true),
        @Parameter(name = "page", description = "页数", example = "1"),
        @Parameter(name = "pageSize", description = "每页显示数据条数", example = "10"),
        @Parameter(name = "admin", description = "是否是管理员，0：管理员，1：管理员"),
        @Parameter(name = "phone", description = "手机号"),
    })
    @GetMapping("/user/list")
    public ServiceResult listUser(@RequestParam(required = false, defaultValue = "1") int page,
                                  @RequestParam(required = false, defaultValue = "10") int pageSize,
                                  @RequestParam(required = false, defaultValue = "0") int admin,
                                  @RequestParam(required = false, defaultValue = "") String phone) {
        Example<User> example = new Example<>();
        final Example.Criteria<User> wrapper = example.createCriteria();
        if (admin == 1) {
            wrapper.andEqualTo(User::getIsAdmin, 1);
        }
        if (StringUtils.isNotBlank(phone)) {
            wrapper.andLike(User::getPhone, "%" + phone + "%");
        }
        wrapper.andIsNotNull(User::getFirstLoginTime);
        example.orderByDesc(User::getFirstLoginTime);
        return adminService.listUser(page, pageSize, example);
    }

    /**
     * 列出所有应用
     *
     * @return 应用列表
     */
    @Operation(
        description = "列出所有应用（可分页，查询）",
        summary = "应用列表"
    )
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "用户凭证", required = true),
        @Parameter(name = "page", description = "页数", example = "1"),
        @Parameter(name = "pageSize", description = "每页显示数据条数", example = "10"),
        @Parameter(name = "appName", description = "应用名称"),
        @Parameter(name = "isAll", description = "是否查询全部应用(包括已软删的)，1是0否"),
    })
    @GetMapping("/app/list")
    public ServiceResult listApp(@RequestParam(required = false, defaultValue = "1") int page,
                                 @RequestParam(required = false, defaultValue = "10") int pageSize,
                                 @RequestParam(required = false, defaultValue = "") String appName,
                                 @RequestParam(required = false, defaultValue = "false") Boolean isAll) {
        Example<App> example = new Example<>();
        final Example.Criteria<App> wrapper = example.createCriteria();
        if (StringUtils.isNotBlank(appName)) {
            wrapper.andLike(App::getAppName, "%" + appName + "%");
        }
        //是否查询全部的数据（已删和未删的）
        if (!isAll) {
            wrapper.andEqualTo(App::getDelFlag, 0);
        }
        return adminService.listApp(page, pageSize, example);
    }

    /**
     * 根据应用id（int）获取应用信息
     *
     * @return App
     */
    @Operation(
        description = "根据应用id（int）获取应用信息",
        summary = "根据应用id（int）获取应用信息"
    )
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "用户凭证", required = true),
        @Parameter(name = "id", description = "应用ID", required = true),
    })
    @GetMapping("/app/{id}")
    public ServiceResult app(@PathVariable int id) {
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return adminService.getApp(id);
    }

    /**
     * 列出所有应用（带绑定信息）
     *
     * @return 应用列表
     */
    @Operation(
        description = "列出用户与所有应用的绑定关系（带绑定信息）",
        summary = "列出所有应用（带绑定信息）"
    )
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "用户凭证", required = true),
        @Parameter(name = "page", description = "页数", example = "1"),
        @Parameter(name = "pageSize", description = "每页显示数据条数", example = "10"),
        @Parameter(name = "userId", description = "用户ID", required = true),
    })
    @GetMapping("/app/list/bind")
    public ServiceResult listAppWithBindInfo(@RequestParam(required = false, defaultValue = "1") int page,
                                             @RequestParam(required = false, defaultValue = "10") int pageSize,
                                             @RequestParam String userId) {
        Example<App> wrapper = new Example<>();
        if (StringUtils.isEmpty(userId)) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        wrapper.createCriteria().andEqualTo(App::getDelFlag, 0);
        return adminService.listAppWithBindInfo(page, pageSize, wrapper, userId);
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
    @PostMapping("/app")
    @OperationRecord(type = OperationRecordLog.OperationType.CREATE, resource = OperationRecordLog.OperationResource.APP, description = OperationRecordLog.OperationDescription.CREATE_APP)
    public ServiceResult createApp(@RequestBody AppRequestDTO appRequestDTO) {
        if (appRequestDTO == null || StringUtils.isBlank(appRequestDTO.getAppName()) || StringUtils.isBlank(appRequestDTO.getTenantAppId())) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        if (appRequestDTO.getAppName().length() > 32) {
            return ServiceResultConstants.APP_NAME_TOO_LONG;
        }
        if (appRequestDTO.getTenantAppId().length() > 32) {
            return ServiceResultConstants.TENANT_APP_ID_TOO_LONG;
        }

        App app = new App();
        app.setTenantAppId(appRequestDTO.getTenantAppId());
        app.setAppName(appRequestDTO.getAppName());
        return adminService.createApp(app.getAppName(), app.getTenantAppId());
    }

    /**
     * 修改APP
     *
     * @param appRequestDTO 应用实体类（参数，主要是appId和appName）
     * @return 是否成功
     */
    @Operation(
        description = "修改APP",
        summary = "修改APP"
    )
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "用户凭证", required = true),
        @Parameter(name = "id", description = "appId(int型)", required = true),
    })
    @PutMapping("/app/{id}")
    @OperationRecord(type = OperationRecordLog.OperationType.UPDATE, resource = OperationRecordLog.OperationResource.APP, description = OperationRecordLog.OperationDescription.UPDATE_APP)
    public ServiceResult editApp(@PathVariable int id, @RequestBody AppRequestDTO appRequestDTO) {
        if (appRequestDTO == null || StringUtils.isBlank(appRequestDTO.getAppName()) || StringUtils.isBlank(appRequestDTO.getTenantAppId())) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        if (id < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        if (appRequestDTO.getAppName().length() > 32) {
            return ServiceResultConstants.APP_NAME_TOO_LONG;
        }
        if (appRequestDTO.getTenantAppId().length() > 32) {
            return ServiceResultConstants.TENANT_APP_ID_TOO_LONG;
        }
        App app = new App();
        app.setTenantAppId(appRequestDTO.getTenantAppId());
        app.setAppName(appRequestDTO.getAppName());
        app.setId(id);
        return adminService.editApp(app.getId(), app.getAppName(), app.getTenantAppId());
    }

    /**
     * 删除某个APP
     *
     * @param appId 应用ID
     * @return 是否成功
     */
    @Operation(
        description = "删除某个APP",
        summary = "删除某个APP"
    )
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "用户凭证", required = true),
        @Parameter(name = "appId", description = "appid(int型)", required = true),
    })
    @DeleteMapping("/app/{appId}")
    @OperationRecord(type = OperationRecordLog.OperationType.DELETE, resource = OperationRecordLog.OperationResource.APP, description = OperationRecordLog.OperationDescription.DELETE_APP)
    public ServiceResult deleteApp(@PathVariable int appId) {
        if (appId < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return adminService.deleteApp(appId);
    }

    /**
     * 绑定某个用户和APP
     *
     * @param userId 用户ID
     * @param appId  应用ID
     * @return 是否成功
     */
    @Operation(
        description = "绑定某个用户和APP",
        summary = "绑定某个用户和APP"
    )
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "用户凭证", required = true),
        @Parameter(name = "userId", description = "用户ID", required = true),
        @Parameter(name = "appId", description = "appId，应用ID(int型)", required = true),
    })
    @PutMapping("/{userId}/{appId}/bind")
    @OperationRecord(type = OperationRecordLog.OperationType.CREATE, resource = OperationRecordLog.OperationResource.USER_APP_REL, description = OperationRecordLog.OperationDescription.CREATE_USER_APP_REL)
    public ServiceResult bind(@PathVariable String userId, @PathVariable int appId) {
        if (StringUtils.isEmpty(userId) || appId < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return adminService.bindUserAndApp(userId, appId);
    }


    /**
     * 列出所有绑定应用
     *
     * @return 应用列表
     */
    @Operation(
        description = "列出用户所有绑定应用",
        summary = "列出所有绑定应用"
    )
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "用户凭证", required = true),
        @Parameter(name = "userId", description = "用户ID", required = true),
    })
    @GetMapping("/app/list/only/bind")
    public ServiceResult listBindApp(@RequestParam String userId) {
        if (StringUtils.isBlank(userId)) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return adminService.listBindApp(userId);
    }


    /**
     * 解绑某个用户和APP
     *
     * @param userId 用户ID
     * @param appId  应用ID
     * @return 是否成功
     */
    @Operation(
        description = "解绑某个用户和APP",
        summary = "解绑某个用户和APP"
    )
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "用户凭证", required = true),
        @Parameter(name = "userId", description = "用户ID", required = true),
        @Parameter(name = "appId", description = "appId，应用ID(int型)", required = true),
    })
    @PutMapping("/{userId}/{appId}/unBind")
    @OperationRecord(type = OperationRecordLog.OperationType.DELETE_FOREVER, resource = OperationRecordLog.OperationResource.USER_APP_REL, description = OperationRecordLog.OperationDescription.DELETE_FOREVER_USER_APP_REL)
    public ServiceResult unBind(@PathVariable String userId, @PathVariable int appId) {
        if (StringUtils.isEmpty(userId) || appId < 1) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return adminService.unbindUserAndApp(userId, appId);
    }

    /**
     * 管理员
     * 根据userId修改用户昵称
     *
     * @return
     */
    @Operation(
        description = "根据userId修改用户昵称（仅管理员才可操作）",
        summary = "根据userId修改用户昵称"
    )
    @Parameters({
        @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "用户凭证", required = true),
    })
    @PutMapping("/user")
    @OperationRecord(type = OperationRecordLog.OperationType.UPDATE, resource = OperationRecordLog.OperationResource.USER, description = OperationRecordLog.OperationDescription.UPDATE_USER)
    public ServiceResult changeNickName(@RequestBody AdminUpdateNickNameRequestDTO user) {
        String userId = user.getUserId();
        String nickName = user.getNickName();
        if (StringUtilsExt.hasBlank(userId, nickName)) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        return userService.updateUserNickNameByUserId(userId, nickName);
    }
}
