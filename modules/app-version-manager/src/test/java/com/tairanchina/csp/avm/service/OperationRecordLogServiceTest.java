package com.tairanchina.csp.avm.service;

import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.OperationRecordLog;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class OperationRecordLogServiceTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(OperationRecordLogServiceTest.class);

    @Autowired
    private OperationRecordLogService operationRecordLogService;

    @Test
    public void createOperationRecordLog() throws Exception {
        OperationRecordLog operationRecordLog = new OperationRecordLog();
        operationRecordLog.setOperationContent("'a':'b'")
            .setAppId(24)
            .setOperationResult(OperationRecordLog.OperationResult.OTHER)
            .setOperationType(OperationRecordLog.OperationType.CREATE)
            .setOperationResource(OperationRecordLog.OperationResource.ANDROID_VERSION)
            .setOperator("test")
            .setResultMessage("test")
            .setCreatedBy("test");
        ServiceResult<?> result = operationRecordLogService.createOperationRecordLog(operationRecordLog);
        if (result.getData() != null) {
            System.out.println(result.getData());
        }
    }

    @Test
    public void deleteOperationRecordLog() throws Exception {
        Integer id = 50;
        ServiceResult<?> result = operationRecordLogService.deleteOperationRecordLogForever(id);
        if (result.getData() != null) {
            if (result.getData() != null) {
                System.out.println(result.getData());
            }
        }
    }

    @Test
    public void deleteOperationRecordLogForever() throws Exception {
        Integer id = 50;
        ServiceResult<?> result = operationRecordLogService.deleteOperationRecordLogForever(id);
        if (result.getData() != null) {
            System.out.println(result.getData());
        }
    }

    @Test
    public void getOperationRecordLogById() throws Exception {
        Integer id = 54;
        ServiceResult<?> result = operationRecordLogService.getOperationRecordLogById(id);
        OperationRecordLog operationRecordLog = (OperationRecordLog) result.getData();
        Object jsonObject = operationRecordLog.getOperationContent();
        System.out.println(jsonObject);
    }


    @Test
    public void getListByQuery() throws Exception {
        ServiceResult<?> result = operationRecordLogService.getListByQuery(1, 10, null, null, null, null, null, null, null, null);
        if (result.getData() != null) {
            logger.info(result.getData().toString());
        }
    }

}