package com.tairanchina.csp.avm.dto;


import com.tairanchina.csp.avm.common.Json;

/**
 * Service返回
 * Created by hzlizx on 2018/4/11 0011
 */
public class ServiceResult<T> {

    /**
     * Code     错误码，200为正常
     * message  文字消息
     * data     需要封装往前台传的对象
     */
    private final int code;
    private final String message;
    private final T data;

    public ServiceResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }


    public static <T> ServiceResult<T> ok(T data) {
        return new ServiceResult<>(200, "请求成功", data);
    }

    public static <T> ServiceResult<T> ok(String message, T data) {
        return new ServiceResult<>(200, message, data);
    }

    public static <T> ServiceResult<T> failed(int code, String message) {
        return new ServiceResult<>(code, message, null);
    }

    public static <T> ServiceResult<T> of(int code, String message, T data) {
        return new ServiceResult<>(code, message, data);
    }


    @Override
    public String toString() {
        return Json.toJsonString(this);
    }

}
