package com.tairanchina.csp.avm.service;

import com.tairanchina.csp.avm.AppVersionManagerApplication;
import com.tairanchina.csp.avm.entity.LoginInfo;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AppVersionManagerApplication.class})
@Transactional
@ComponentScan("com.tairanchina.csp.avm.service")
public class BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);


    /**
     * 设置登录状态
     *
     * @throws Exception
     */
    @BeforeEach
    public void setUp() throws Exception {
        LoginInfo loginInfo = new LoginInfo(24, "b9e980c1495e4d0582c257901d86b4ff");
        ThreadLocalUtils.USER_THREAD_LOCAL.set(loginInfo);
    }

    @Test
    public void test() {
        logger.info("BaseTest........");
    }

}
