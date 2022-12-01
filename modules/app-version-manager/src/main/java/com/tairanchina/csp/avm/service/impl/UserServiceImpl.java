package com.tairanchina.csp.avm.service.impl;

import java.util.*;

import com.tairanchina.csp.avm.common.Digest;
import com.tairanchina.csp.avm.common.Cache;
import com.tairanchina.csp.avm.constants.RedisKey;
import com.tairanchina.csp.avm.constants.ServiceResultConstants;
import com.tairanchina.csp.avm.dto.JWTSubject;
import com.tairanchina.csp.avm.dto.ServiceResult;
import com.tairanchina.csp.avm.entity.User;
import com.tairanchina.csp.avm.mapper.UserMapper;
import com.tairanchina.csp.avm.service.TokenService;
import com.tairanchina.csp.avm.service.UserService;
import com.tairanchina.csp.avm.utils.StringUtilsExt;
import com.tairanchina.csp.avm.utils.ThreadLocalUtils;
import io.mybatis.mapper.example.Example;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenService tokenService;

    @Override
    public ServiceResult<?> register(String phone, String password) {
        if (StringUtilsExt.hasEmpty(password, phone)) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        long existPhone = userMapper.wrapper().eq(User::getPhone, phone).count();
        if (existPhone > 0) {
            return ServiceResultConstants.USER_EXISTS;
        }
        long count = userMapper.selectCount(new User());
        User user = new User();
        user.setIsAdmin(count > 0 ? 0 : 1);
        user.setUserId(UUID.randomUUID().toString().substring(UUID.randomUUID().toString().lastIndexOf("-")));
        user.setFirstLoginTime(new Date());
        user.setPhone(phone);
        user.setNickName(phone);
        user.setUsername(phone);
        String md5 = Digest.md5(password + phone); // 加盐算法
        user.setPassword(md5);
        int insert = userMapper.insert(user);
        if (insert > 0) {
            return ServiceResult.ok(null);
        } else {
            return ServiceResultConstants.DB_ERROR;
        }
    }

    @Override
    public ServiceResult<?> login(String phone, String password) {
        if (StringUtilsExt.hasEmpty(password, phone)) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        User userQuery = new User();
        userQuery.setPhone(phone);
        User user = userMapper.selectOne(userQuery).orElse(null);
        if (Objects.isNull(user)) {
            return ServiceResultConstants.WRONG_PHONE_PASSWORD;
        }
        String md5 = Digest.md5(password + phone); // 加盐算法
        if (StringUtils.isNotEmpty(user.getPassword()) && user.getPassword().equals(md5)) {
            // 登录成功，签发token
            JWTSubject jwtSubject = new JWTSubject();
            jwtSubject.setUserId(user.getUserId());
            String jwt = tokenService.signJWT(jwtSubject); // 签发JWTtoken

            Cache.set(RedisKey.USER_LOGIN_INFO, user.getUserId(), jwt); //将JWT保存至Redis
            HashMap<String, Object> map = new HashMap<>();
            map.put("token", jwt);
            map.put("ident", phone);
            map.put("isNew", false);
            map.put("nickName", user.getNickName());
            map.put("userId", user.getUserId());
            map.put("username", user.getPhone());
            map.put("warning", false);
            map.put("isAdmin", user.getIsAdmin());
            return ServiceResult.ok(map);
        } else {
            return ServiceResultConstants.WRONG_PHONE_PASSWORD;
        }
    }

    @Override
    public ServiceResult<?> validate(String jwt) {
        JWTSubject jwtSubject = tokenService.validateJWT(jwt);
        if (Objects.isNull(jwtSubject)) {
            return ServiceResultConstants.JWT_ERROR;
        }
        String userId = jwtSubject.getUserId();
        String jwtFromRedis = Cache.get(RedisKey.USER_LOGIN_INFO, userId);
        if (!jwt.equals(jwtFromRedis)) {
            return ServiceResultConstants.JWT_ERROR;
        }
        User userQuery = new User();
        userQuery.setUserId(userId);
        User user = userMapper.selectOne(userQuery).orElse(null);
        if (Objects.isNull(user)) {
            Cache.del(RedisKey.USER_LOGIN_INFO, userId);
            return ServiceResultConstants.USER_NOT_FOUND;
        }
        return ServiceResult.ok(user);
    }

    @Override
    public ServiceResult<?> changePassword(String oldPassword, String password) {
        if (StringUtilsExt.hasEmpty(password, oldPassword)) {
            return ServiceResultConstants.NEED_PARAMS;
        }
        String userId = ThreadLocalUtils.USER_THREAD_LOCAL.get().getUserId();
        User userQuery = new User();
        userQuery.setUserId(userId);
        User user = userMapper.selectOne(userQuery).orElse(null);
        if (Objects.isNull(user)) {
            return ServiceResultConstants.USER_NOT_FOUND;
        }
        String phone = user.getPhone();
        String oldPass = Digest.md5(oldPassword + phone);
        if (!oldPass.equals(user.getPassword())) {
            return ServiceResultConstants.WRONG_PHONE_PASSWORD;
        } else {
            String newPass = Digest.md5(password + phone);
            user.setPassword(newPass);
            final Example<User> example = userMapper.example();
            example.createCriteria().andEqualTo(User::getUserId, userId);
            long result = userMapper.updateByExample(user, example);
            if (result > 0) {
                return ServiceResult.ok(null);
            } else {
                return ServiceResultConstants.DB_ERROR;
            }
        }
    }

    @Override
    public ServiceResult<?> updateUserNickNameByUserId(String userId, String nickName) {
        User user = new User();
        user.setUserId(userId);
        User u = userMapper.selectOne(user).orElse(null);
        if (StringUtils.isNotBlank(u.getNickName()) && nickName.equals(u.getNickName())) {
            return ServiceResult.failed(40002, "请输入不一样的用户名");
        }
        List<User> check = userMapper.wrapper().eq(User::getNickName, nickName).list();
        if (!check.isEmpty()) {
            return ServiceResult.failed(40001, "该用户名已存在");
        }
        User update = new User();
        update.setNickName(nickName);
        final Example<User> example = userMapper.example();
        example.createCriteria().andEqualTo(User::getUserId, userId);
        Integer result = userMapper.updateByExample(update, example);
        return ServiceResult.ok(result);
    }

}
