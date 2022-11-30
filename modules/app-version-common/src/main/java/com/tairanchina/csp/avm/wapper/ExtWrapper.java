package com.tairanchina.csp.avm.wapper;

import io.mybatis.mapper.example.Example;
import io.mybatis.mapper.example.ExampleWrapper;

/**
 * 描述:版本比较拓展
 *
 * @author hzds
 * @Create 2018-09 : 26 19:44
 */
public class ExtWrapper {

    @SuppressWarnings("rawtypes")
    public static void orderByVersion(ExampleWrapper wrapper, String columns) {
        wrapper.orderBy("CONVERT(substring_index(substring_index(substring_index(" + columns + ",'-',1),'.',1),'.',-1),SIGNED)");
        wrapper.orderBy("CONVERT(substring_index(substring_index(substring_index(" + columns + ",'-',1),'.',2),'.',-1),SIGNED)");
        wrapper.orderBy("CONVERT(substring_index(substring_index(substring_index(" + columns + ",'-',1),'.',3),'.',-1),SIGNED)");
        wrapper.orderBy("CONVERT(substring_index(substring_index(substring_index(" + columns + ",'-',1),'.',4),'.',-1),SIGNED)");
    }

    @SuppressWarnings("rawtypes")
    public static void orderByVersion(Example wrapper, String columns) {
        wrapper.orderBy("CONVERT(substring_index(substring_index(substring_index(" + columns + ",'-',1),'.',1),'.',-1),SIGNED)");
        wrapper.orderBy("CONVERT(substring_index(substring_index(substring_index(" + columns + ",'-',1),'.',2),'.',-1),SIGNED)");
        wrapper.orderBy("CONVERT(substring_index(substring_index(substring_index(" + columns + ",'-',1),'.',3),'.',-1),SIGNED)");
        wrapper.orderBy("CONVERT(substring_index(substring_index(substring_index(" + columns + ",'-',1),'.',4),'.',-1),SIGNED)");
    }

}
