package com.tairanchina.csp.avm.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.tairanchina.csp.avm.AppVersionRestApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AppVersionRestApplication.class})
@Transactional
@ComponentScan("com.tairanchina.csp.avm.controller")
public class BaseControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseControllerTest.class);


    public static MockMvc mockMvc;
    public static HttpHeaders httpHeaders;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean(name = "restTemplate")
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        httpHeaders = new HttpHeaders();
    }

    @Test
    public void test() {
        logger.info("BaseControllerTest........");
    }

    public String get(String uri) throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri).headers(httpHeaders)).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).isNotEmpty();
        return contentAsString;
    }


}
