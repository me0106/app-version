package com.tairanchina.csp.avm.service.impl;

import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.OperationRecordLogExt;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.OperationRecordLog;
import com.tairanchina.csp.avm.mapper.OperationRecordLogMapper;
import com.tairanchina.csp.avm.service.BasicService;
import com.tairanchina.csp.avm.service.OperationRecordLogService;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static com.tairanchina.csp.avm.mapper.OperationRecordLogMapper.*;

@Service
public class OperationRecordLogServiceImpl implements OperationRecordLogService {

    @Autowired
    private OperationRecordLogMapper operationRecordLogMapper;

    @Autowired
    private BasicService basicService;

    @Override
    public ServiceResult createOperationRecordLog(OperationRecordLog operationRecordLog) {
        operationRecordLog.setCreatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        operationRecordLog.setAppId(ThreadLocalUtils.USER_THREAD_LOCAL.get().getAppId());
        int result = operationRecordLogMapper.insert(operationRecordLog);
        if (result > 0) {
            return ServiceResult.ok(operationRecordLog);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult deleteOperationRecordLog(Integer id) {
        OperationRecordLog operationRecordLog = operationRecordLogMapper.selectById(id);
        if (null == operationRecordLog) {
            return ServiceResultConstants.OPERATION_LOG_NOT_EXISTS;
        }
        operationRecordLog.setDelFlag(1);
        operationRecordLog.setUpdatedBy(ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId());
        operationRecordLog.setUpdatedTime(null);
        int result = operationRecordLogMapper.updateById(operationRecordLog);
        if (result > 0) {
            return ServiceResult.ok(operationRecordLog);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult deleteOperationRecordLogForever(Integer id) {
        OperationRecordLog operationRecordLog = operationRecordLogMapper.selectById(id);
        if (null == operationRecordLog) {
            return ServiceResultConstants.OPERATION_LOG_NOT_EXISTS;
        }
        int result = operationRecordLogMapper.deleteById(operationRecordLog.getId());
        if (result > 0) {
            return ServiceResult.ok(operationRecordLog);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult getOperationRecordLogById(Integer id) {
        OperationRecordLog operationRecordLog = operationRecordLogMapper.selectById(id);
        return ServiceResult.ok(operationRecordLog);
    }


    @Override
    public ServiceResult getListByQuery(int page, int pageSize, String phone, String nickName, Integer appId, String operationResource, String operationDescription, String operationType,
                                        String startDate, String endDate) {
        final Query query = new Query(phone, nickName, appId, operationResource, operationDescription, operationType, startDate, endDate);
        Page<OperationRecordLogExt> list = operationRecordLogMapper.selectLogExtByQuery(PageRequest.of(page, pageSize), query);
        return ServiceResult.ok(list);
    }


}
