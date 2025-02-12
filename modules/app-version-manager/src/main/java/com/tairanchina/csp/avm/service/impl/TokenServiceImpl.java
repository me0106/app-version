package com.tairanchina.csp.avm.service.impl;

import com.tairanchina.csp.avm.common.Json;
import com.tairanchina.csp.avm.dto.JWTSubject;
import com.tairanchina.csp.avm.service.TokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by hzlizx on 2019/2/21
 */

@Service
public class TokenServiceImpl implements TokenService {

    private final Logger log = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Override
    public String signJWT(JWTSubject subject) {
        if (Objects.isNull(subject)) {
            return null;
        }
        return Jwts.builder()
            .setSubject(Json.toJsonString(subject))
            .signWith(secretKey)
            .compact();
    }

    @Override
    public JWTSubject validateJWT(String jwt) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
            String subject = claimsJws.getBody().getSubject();
            return Json.getMapper().readValue(subject, JWTSubject.class);
        } catch (JwtException | IOException e) {
            log.info("Jwt解析失败:{},jwt={},", e.getMessage(), jwt);
            return null;
        }
    }
}
