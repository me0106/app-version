package com.tairanchina.csp.avm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class AdminUpdateNickNameRequestDTO {

    @Schema(description = "用户Id")
    @NotBlank(message = "userId不能为空")
    private String userId;

    @Schema(description = "用户昵称")
    @NotBlank(message = "nickName不能为空")
    private String nickName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
