package com.justintime.jit.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

@Service
public class ShaHashUtil {


    public  String hmacSHA256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes());
            return Hex.encodeHexString(hash); // commons-codec Hex
        } catch (Exception e) {
            throw new RuntimeException("Unable to generate HMAC", e);
        }
    }
}
