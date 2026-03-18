package cn.edu.hdu.iamisfcaubackend.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    private final Map<String, String> tokenToUserId = new ConcurrentHashMap<>();

    public String issueToken(String userId) {
        String token = UUID.randomUUID().toString();
        tokenToUserId.put(token, userId);
        return token;
    }

    public String resolveUserId(String token) {
        return tokenToUserId.get(token);
    }

    public void revoke(String token) {
        tokenToUserId.remove(token);
    }
}