package com.tairanchina.csp.avm.common;

import org.springframework.util.DigestUtils;

/**
 * @author me 2022-11-29 00:04
 */
public class Digest {

    public static String md5(String source) {
        return DigestUtils.md5DigestAsHex(source.getBytes());
    }

}
