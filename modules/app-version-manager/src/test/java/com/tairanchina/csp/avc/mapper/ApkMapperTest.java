package com.tairanchina.csp.avc.mapper;


import com.tairanchina.csp.avm.dto.ApkExt;
import com.tairanchina.csp.avm.mapper.ApkMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class ApkMapperTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(ApkMapperTest.class);

    @Autowired
    ApkMapper apkMapper;

    @Test
    public void selectApkWithChannelCode() throws Exception {
        Integer versionId = 14;
        String channelCode = "";
        String md5 = "";
        Integer deliveryStatus = null;
        Page<ApkExt> apkExts = apkMapper.selectApkWithChannelCode(PageRequest.of(1,10), versionId, channelCode, md5, deliveryStatus);
        logger.info("apkExts..." + apkExts.getContent().size());

        apkExts = apkMapper.selectApkWithChannelCode(PageRequest.of(1,10), null, "", "", null);
        logger.info("apkExts..." + apkExts.getContent().size());
        assert apkExts.getContent().size() > 0;
    }

}