package cn.gduf.xytg.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description MD5加密工具类
 * @date 2025/10/20 22:04
 */
public class MD5 {
    /**
     * 对字符串进行MD5加密
     *
     * @param strSrc 待加密的源字符串
     * @return 加密后的十六进制字符串表示
     */
    public static String encrypt(String strSrc) {
        try {
            // 定义十六进制字符数组，用于转换字节到十六进制字符
            char hexChars[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                    '9', 'a', 'b', 'c', 'd', 'e', 'f'};

            // 将字符串转换为字节数组
            byte[] bytes = strSrc.getBytes();

            // 获取MD5消息摘要实例并计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            bytes = md.digest();

            // 将字节数组转换为十六进制字符串
            int j = bytes.length;
            char[] chars = new char[j * 2];
            int k = 0;
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                chars[k++] = hexChars[b >>> 4 & 0xf];
                chars[k++] = hexChars[b & 0xf];
            }
            return new String(chars);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("MD5加密出错！！+" + e);
        }
    }
}
