package com.example.backend.auth;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
public class TotpUtil {

    private static final int DIGITS = 6;
    private static final int PERIOD = 30;
    private static final String ALGORITHM = "HmacSHA1";

    public String generateSecret() {
        byte[] bytes = new byte[20];
        new SecureRandom().nextBytes(bytes);
        return new Base32().encodeToString(bytes);
    }

    public String getTotpUri(String secret, String username, String issuer) {
        return "otpauth://totp/" + issuer + ":" + username +
               "?secret=" + secret + "&issuer=" + issuer + "&algorithm=SHA1&digits=6&period=30";
    }

    public boolean verify(String secret, int code) {
        long time = System.currentTimeMillis() / 1000L / PERIOD;
        for (int i = -1; i <= 1; i++) {
            if (generateTotp(secret, time + i) == code) return true;
        }
        return false;
    }

    private int generateTotp(String secret, long time) {
        try {
            byte[] key = new Base32().decode(secret);
            byte[] msg = ByteBuffer.allocate(8).putLong(time).array();
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(key, ALGORITHM));
            byte[] hash = mac.doFinal(msg);
            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset] & 0x7F) << 24)
                       | ((hash[offset + 1] & 0xFF) << 16)
                       | ((hash[offset + 2] & 0xFF) << 8)
                       | (hash[offset + 3] & 0xFF);
            return binary % (int) Math.pow(10, DIGITS);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("TOTP generation failed", e);
        }
    }
}
