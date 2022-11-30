package com.tairanchina.csp.avm.controller;

import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ChangePasswordReq;
import com.tairanchina.csp.avm.dto.LoginReq;
import com.tairanchina.csp.avm.dto.RegisterReq;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.OperationRecordLog;
import com.tairanchina.csp.avm.annotation.OperationRecord;
import com.tairanchina.csp.avm.service.UserService;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Created by hzlizx on 2018/7/4 0004
 */
@Tag(name = "用户相关接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(description = "登录")
    @PostMapping("/login")
    public ServiceResult login(@RequestBody LoginReq loginReq) {
        return userService.login(loginReq.getPhone(), loginReq.getPassword());
    }


    @Operation(description = "注册")
    @PostMapping("/register")
    public ServiceResult register(@RequestBody RegisterReq registerReq) {
        if (!registerReq.getPassword().trim().equals(registerReq.getPasswordConfirm().trim())) {
            return ServiceResultConstants.PASSWORD_CONFIRM_ERROR;
        }
        int length = registerReq.getPassword().trim().length();
        if (length < 6 || length > 32) {
            return ServiceResultConstants.PASSWORD_ERROR;
        }
        return userService.register(registerReq.getPhone(), registerReq.getPassword());
    }

    @Operation(description = "修改密码")
    @PostMapping("/changePassword")
    public ServiceResult register(@RequestBody ChangePasswordReq changePasswordReq) {
        if (!changePasswordReq.getPassword().trim().equals(changePasswordReq.getPasswordConfirm().trim())) {
            return ServiceResultConstants.PASSWORD_CONFIRM_ERROR;
        }
        int length = changePasswordReq.getPassword().trim().length();
        if (length < 6 || length > 32) {
            return ServiceResultConstants.PASSWORD_ERROR;
        }
        return userService.changePassword(changePasswordReq.getOldPassword(), changePasswordReq.getPassword());
    }


    @Operation(
        description = "暂不支持"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping("/set")
    public ServiceResult set(@RequestParam(required = false, defaultValue = "") String password,
                             @RequestParam(required = false, defaultValue = "") String phone) {
        return ServiceResultConstants.SERVICE_NOT_SUPPORT;
    }

    @Operation(
        description = "暂不支持"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
    })
    @GetMapping("/nick")
    public ServiceResult change(@RequestParam(required = false, defaultValue = "") String password,
                                @RequestParam(required = false, defaultValue = "") String phone,
                                @RequestParam(required = false, defaultValue = "") String nickName) {
        return ServiceResultConstants.SERVICE_NOT_SUPPORT;
    }

    @Operation(
        description = "修改用户昵称（当前登录用户操作）",
        summary = "修改用户昵称"
    )
    @Parameters({
        @Parameter(name = "Authorization", description = "用户登录凭证", in = ParameterIn.HEADER, required = true),
        @Parameter(name = "nickName", description = "用户昵称", required = true),
    })
    @PutMapping("/update/{nickName}")
    @OperationRecord(type = OperationRecordLog.OperationType.UPDATE, resource = OperationRecordLog.OperationResource.USER, description = OperationRecordLog.OperationDescription.UPDATE_USER)
    public ServiceResult changeNickName(@PathVariable String nickName) {
        String userId = ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId();
        return userService.updateUserNickNameByUserId(userId, nickName);
    }

}
