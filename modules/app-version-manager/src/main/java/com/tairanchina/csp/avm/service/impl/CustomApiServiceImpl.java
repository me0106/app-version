package com.tairanchina.csp.avm.service.impl;

import java.util.ArrayList;

import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.CustomApi;
import com.tairanchina.csp.avm.enums.ChatBotEventType;
import com.tairanchina.csp.avm.mapper.CustomApiMapper;
import com.tairanchina.csp.avm.service.BasicService;
import com.tairanchina.csp.avm.service.ChatBotService;
import com.tairanchina.csp.avm.service.CustomApiService;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import io.mybatis.mapper.example.Example;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class CustomApiServiceImpl implements CustomApiService {

    @Autowired
    private BasicService basicService;

    @Autowired
    private CustomApiMapper customApiMapper;

    @Autowired
    private ChatBotService chatBotService;

    @Override
    public ServiceResult createCustomApi(CustomApi customApi) {
        customApi.setCreatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        customApi.setAppId(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId());
        int result = customApiMapper.insert(customApi);
        if (result > 0) {
            chatBotService.sendMarkdown(ChatBotEventType.CUSTOM_API_CREATED, "创建自定义接口提醒", makeMarkdown(customApi));
            return ServiceResult.ok(customApi);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult updateCustomApi(CustomApi customApi) {
        CustomApi checkExisit = customApiMapper.selectById(customApi.getId());
        if (null == checkExisit) {
            return ServiceResultConstants.CUSTOM_API_NOT_EXISTS;
        }
        if (!checkExisit.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        customApi.setUpdatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        customApi.setUpdatedTime(null);
        int result = customApiMapper.updateById(customApi);
        if (result > 0) {
            return ServiceResult.ok(customApi);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult deleteCustomApi(int id) {
        CustomApi customApi = customApiMapper.selectById(id);
        if (null == customApi) {
            return ServiceResultConstants.CUSTOM_API_NOT_EXISTS;
        }
        if (!customApi.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        customApi.setDelFlag(1);
        customApi.setUpdatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        customApi.setUpdatedTime(null);
        int result = customApiMapper.updateById(customApi);
        if (result > 0) {
            return ServiceResult.ok(customApi);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult deleteCustomApiForver(int id) {
        CustomApi customApi = customApiMapper.selectById(id);
        if (null == customApi) {
            return ServiceResultConstants.CUSTOM_API_NOT_EXISTS;
        }
        if (!customApi.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        int result = customApiMapper.deleteById(id);
        if (result > 0) {
            return ServiceResult.ok(customApi);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult getCustomApiByOne(CustomApi customApi) {
        CustomApi result = customApiMapper.selectOne(customApi).orElse(null);
        if (result == null) {
            return ServiceResultConstants.CUSTOM_API_NOT_EXISTS;
        }
        if (!result.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        return ServiceResult.ok(result);
    }

    @Override
    public ServiceResult getCustomApiByKeyAndAppId(String customKey) {
        CustomApi customApi = new CustomApi();
        customApi.setCustomKey(customKey);
        customApi.setAppId(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId());
        customApi.setDelFlag(0);
        CustomApi result = customApiMapper.selectOne(customApi).orElse(null);
        if (result == null) {
            return ServiceResultConstants.CUSTOM_API_NOT_EXISTS;
        }
        if (!result.getAppId().equals(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId())) {
            return ServiceResultConstants.RESOURCE_NOT_BELONG_APP;
        }
        return ServiceResult.ok(result);
    }

    @Override
    public ServiceResult list(int page, int pageSize, Example<CustomApi> wrapper) {
        final Page<CustomApi> customApis = customApiMapper.selectPage(PageRequest.of(page, pageSize), wrapper);
        basicService.formatCreatedBy(customApis);
        return ServiceResult.ok(customApis);
    }

    private String makeMarkdown(CustomApi customApi) {
        StringBuilder sb = new StringBuilder();
        sb.append("#### 发布了自定义接口[ ").append(customApi.getCustomName()).append(" ]\n\n");
        sb.append("> **接口地址** ：").append(customApi.getCustomKey()).append("\n\n");

        Integer customStatus = customApi.getCustomStatus();
        switch (customStatus) {
            case 0:
                sb.append("> **接口状态** ：关闭\n\n");
                break;
            case 1:
                sb.append("> **接口状态** ：线上开启\n\n");
                break;
            case 2:
                sb.append("> **接口状态** ：测试需要\n\n");
                break;
        }
        ArrayList arrayList = new ArrayList<>();
        String androidRange = null;
        String iosRange = null;
        if (customApi.getAndroidEnabled() == 1) {
            arrayList.add("Android");
            androidRange = customApi.getAndroidMin() + " - " + customApi.getAndroidMax();
        }

        if (customApi.getIosEnabled() == 1) {
            arrayList.add("iOS");
            iosRange = customApi.getIosMin() + " - " + customApi.getIosMax();
        }
        sb.append("> **开启设备** ：" + StringUtils.join(arrayList, ',') + "\n\n");
        if (StringUtils.isNotEmpty(androidRange)) {
            sb.append("> **Android范围** ：" + androidRange + "\n\n");
        }
        if (StringUtils.isNotEmpty(iosRange)) {
            sb.append("> **iOS范围** ：" + iosRange + "\n\n");
        }
        return sb.toString();
    }
}
