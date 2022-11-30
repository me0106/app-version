package com.tairanchina.csp.avm.mapper;

import java.util.List;

import com.tairanchina.csp.avm.common.BaseMapper;
import com.tairanchina.csp.avm.dto.OperationRecordLogExt;
import com.tairanchina.csp.avm.entity.OperationRecordLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Mapper
public interface OperationRecordLogMapper extends BaseMapper<OperationRecordLog, Integer> {

    default Page<OperationRecordLogExt> selectLogExtByQuery(Pageable page, Query query) {
        final long total = this.countLogExtByQuery(query);
        final List<OperationRecordLogExt> content = selectLogExtByQueryWithOffset(query, (page.getPageNumber() - 1) * page.getPageSize(), page.getPageSize());
        return new PageImpl<>(content, page, total);
    }

    List<OperationRecordLogExt> selectLogExtByQueryWithOffset(Query query, int offset, int size);

    long countLogExtByQuery(Query query);

    final class Query {
        String phone;
        String nickName;
        Integer appId;
        String operationResource;
        String operationDescription;
        String operationType;
        String startDate;
        String endDate;

        public Query(String phone, String nickName, Integer appId, String operationResource, String operationDescription, String operationType, String startDate, String endDate) {
            this.phone = phone;
            this.nickName = nickName;
            this.appId = appId;
            this.operationResource = operationResource;
            this.operationDescription = operationDescription;
            this.operationType = operationType;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String getPhone() {
            return phone;
        }

        public String getNickName() {
            return nickName;
        }

        public Integer getAppId() {
            return appId;
        }

        public String getOperationResource() {
            return operationResource;
        }

        public String getOperationDescription() {
            return operationDescription;
        }

        public String getOperationType() {
            return operationType;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }
    }
}
