package com.tairanchina.csp.avm.service;

import com.tairanchina.csp.avm.AppVersionRestApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AppVersionRestApplication.class})
@Transactional
@ComponentScan("com.tairanchina.csp.avm.service")
public class BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    @Test
    public void test() {
        logger.info("BaseTest........");
    }

}
