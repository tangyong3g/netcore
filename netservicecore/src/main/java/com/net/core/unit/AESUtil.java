package com.net.core.unit;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * 加密工具类
 * <p>
 * 涵盖加密和解密方法
 */
public class AESUtil {

    public static String AES_KEY = "AES7654321!#@tcl";
    private static String IV = "1234567812345678";
    private static String ALGORITHM = "AES/CBC/NoPadding";
    public static String AES_DECRYPT_KEY = "cqgf971sp394@!#0";


    /**
     * 加密方法
     *
     * @param dataBytes
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encrypt2(byte[] dataBytes, String key) throws Exception {
        if (dataBytes == null || dataBytes.length == 0) return null;

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            int blockSize = cipher.getBlockSize();

            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            byte[] keybytes = key.getBytes("utf-8");
            SecretKeySpec keyspec = new SecretKeySpec(keybytes, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes("utf-8"));

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);
            return encrypted;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 揭秘方法
     *
     * @param encrypted
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decrypt2(byte[] encrypted, String key) {
        byte[] original = null;
        if (encrypted == null || encrypted.length == 0) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            byte[] keybytes = key.getBytes("utf-8");
            SecretKeySpec keyspec = new SecretKeySpec(keybytes, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes("utf-8"));

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            original = cipher.doFinal(encrypted);

            return original;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException al) {
            al.printStackTrace();
        } catch (UnsupportedEncodingException ue) {
            ue.printStackTrace();
        } catch (InvalidAlgorithmParameterException ine) {
            ine.printStackTrace();
        } catch (InvalidKeyException inek) {
            inek.printStackTrace();
        } catch (BadPaddingException badE) {
            badE.printStackTrace();
        } catch (IllegalBlockSizeException il) {
            il.printStackTrace();
        }
        return original;
    }

    /**
     * 解密解压缩
     *
     * @param str
     * @param aesKey       AES算法秘钥
     * @param isUncompress 是否解压
     * @return 返回解密解压的json字符串
     * @throws Exception
     */
    public static String decryptUncompress(String str, String aesKey, boolean isUncompress) throws Exception {
        if (TextUtils.isEmpty(str)) return null;
        byte[] decodeBase64Byte = Base64Utils.decodeBase64(str);
        byte[] decodeAesByte = AESUtil.decrypt2(decodeBase64Byte, aesKey);
        return new String(decodeAesByte);
    }
}
