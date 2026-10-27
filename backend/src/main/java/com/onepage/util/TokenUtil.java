package com.onepage.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 无状态 Token 工具（HMAC-SHA256 签名）
 *
 * <p>Token 结构为 {@code base64Url(payload).base64Url(signature)}，
 * payload 为 {@code userId:expireAt}，服务端无需存储会话即可校验。</p>
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Component
public class TokenUtil {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String SEPARATOR = ".";

    private final byte[] secret;
    private final long defaultTtlMillis;
    private final long rememberTtlMillis;

    public TokenUtil(@Value("${onepage.auth.secret}") String secret,
                     @Value("${onepage.auth.token-ttl-hours}") long tokenTtlHours) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.defaultTtlMillis = tokenTtlHours * 60 * 60 * 1000L;
        this.rememberTtlMillis = this.defaultTtlMillis * 4;
    }

    public String create(Long userId, boolean remember) {
        long ttl = remember ? rememberTtlMillis : defaultTtlMillis;
        long expireAt = System.currentTimeMillis() + ttl;
        String payload = userId + ":" + expireAt;
        String encodedPayload = encode(payload.getBytes(StandardCharsets.UTF_8));
        String signature = encode(hmac(encodedPayload));
        return encodedPayload + SEPARATOR + signature;
    }

    /**
     * 校验 Token 并返回用户 ID，失败返回 {@code null}
     */
    public Long parse(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        int index = token.lastIndexOf(SEPARATOR);
        if (index <= 0) {
            return null;
        }
        String encodedPayload = token.substring(0, index);
        String signature = token.substring(index + 1);
        String expected = encode(hmac(encodedPayload));
        if (!constantTimeEquals(expected, signature)) {
            return null;
        }
        try {
            String payload = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
            String[] parts = payload.split(":");
            if (parts.length != 2) {
                return null;
            }
            long userId = Long.parseLong(parts[0]);
            long expireAt = Long.parseLong(parts[1]);
            if (expireAt < System.currentTimeMillis()) {
                return null;
            }
            return userId;
        } catch (Exception e) {
            return null;
        }
    }

    public long getDefaultTtlMillis() {
        return defaultTtlMillis;
    }

    public long getRememberTtlMillis() {
        return rememberTtlMillis;
    }

    private byte[] hmac(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Token 签名失败", e);
        }
    }

    private static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
