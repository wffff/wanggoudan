package cn.goudan.wang.core.baseconfig.utils;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by momo on 2017/4/24.
 */
public class Utils {

    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String ENCRYPT_MD5 = "MD5";
    public static final String ENCRYPT_SHA = "SHA";
    public static final String ENCRYPT_SHA_1 = "SHA-1";
    public static final String ENCRYPT_SHA_512 = "SHA-512";

    public static String extractTokenKey(String value) {

        if (value == null) return null;

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(ENCRYPT_SHA_1);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(ENCRYPT_SHA_1 + " algorithm not available.  Fatal (should be in the JDK).");
        }

        try {
            byte[] bytes = digest.digest(value.getBytes(CHARSET_UTF8));
            return String.format("%040x", new BigInteger(1, bytes));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(CHARSET_UTF8 + " encoding not available.  Fatal (should be in the JDK).");
        }
    }

    public static byte[] serializeAccessToken(OAuth2AccessToken token) {
        return SerializationUtils.serialize(token);
    }

    public static byte[] serializeRefreshToken(OAuth2RefreshToken token) {
        return SerializationUtils.serialize(token);
    }

    public static byte[] serializeAuthentication(OAuth2Authentication authentication) {
        return SerializationUtils.serialize(authentication);
    }

    public static OAuth2AccessToken deserializeAccessToken(byte[] token) {
        return SerializationUtils.deserialize(token);
    }

    public static OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
        return SerializationUtils.deserialize(token);
    }

    public static OAuth2Authentication deserializeAuthentication(byte[] authentication) {
        return SerializationUtils.deserialize(authentication);
    }

    public static String dateToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }
}