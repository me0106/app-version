package com.tairanchina.csp.avc.mapper;


import com.tairanchina.csp.avm.dto.OperationRecordLogExt;
import com.tairanchina.csp.avm.entity.OperationRecordLog;
import com.tairanchina.csp.avm.mapper.OperationRecordLogMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class OperationRecordLogMapperTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(OperationRecordLogMapperTest.class);

    @Autowired
    OperationRecordLogMapper operationRecordLogMapper;

    @Test
    public void selectLogExtByQuery() throws Exception {
        String endDate = "2018-06-27 19:10:27";

        final OperationRecordLogMapper.Query query = new OperationRecordLogMapper.Query(null, null, null, null, null, null, null, endDate);
        Page<OperationRecordLogExt> list = operationRecordLogMapper.selectLogExtByQuery(PageRequest.of(1, 10), query);
        logger.info("size: " + list.getContent().size());

        final OperationRecordLogMapper.Query query1 = new OperationRecordLogMapper.Query("", "", null, "", null, "", null, null);
        list = operationRecordLogMapper.selectLogExtByQuery(PageRequest.of(1, 10), query1);
        logger.info("size: " + list.getContent().size());
    }

}