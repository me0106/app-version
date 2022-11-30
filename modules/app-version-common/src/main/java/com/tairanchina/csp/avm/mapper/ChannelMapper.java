package com.tairanchina.csp.avm.mapper;

import java.util.List;

import com.tairanchina.csp.avm.common.BaseMapper;
import com.tairanchina.csp.avm.entity.Channel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * Created by hzlizx on 2018/5/17 0017
 */
@Mapper
public interface ChannelMapper extends BaseMapper<Channel, Integer> {


    @Select("SELECT * FROM channel WHERE app_id=#{appId} AND (channel_code =#{channelCode} OR channel_name=#{channelName}) AND del_flag=0")
    List<Channel> selectChannelsForCreate(int appId, String channelCode, String channelName);


}
