package com.dxy.library.util.common;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

/**
 * 字符串工具类
 * @author duanxinyuan
 * 2015-01-16 20:43
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 判断字符串是否乱码
     */
    public static boolean isGarbled(String str) {
        if (isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            // 当从Unicode编码向某个字符集转换时，如果在该字符集中没有对应的编码，则得到0x3f（即问号字符?）
            //从其他字符集向Unicode编码转换时，如果这个二进制数在该字符集中没有标识任何的字符，则得到的结果是0xfffd
            if ((int) c == 0xfffd) {
                // 存在乱码
                return true;
            }
        }
        return false;
    }

    /**
     * 获取字符串长度（一个中文表示两个字符）
     */
    public static int getWordCount(String s) {
        if (StringUtils.isEmpty(s)) {
            return 0;
        }
        s = s.replaceAll("[^\\x00-\\xff]", "**");
        return s.length();
    }

    /**
     * 图片Url是否是网络地址
     */
    public static boolean isHttpUrl(String url) {
        return !StringUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith("https://"));
    }

    /**
     * 产生一个随机的字符串
     */
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(62);
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }

    /**
     * 将字符串转成16进制字符串
     */
    public static String toHex(String s) {
        return toHex(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将字节码转成16进制字符串
     */
    public static String toHex(byte[] bytes) {
        // 把密文转换成十六进制的字符串形式
        StringBuilder hexString = new StringBuilder();
        // 字节数组转换为 十六进制 数
        for (byte aMd : bytes) {
            String shaHex = Integer.toHexString(aMd & 0xFF);
            if (shaHex.length() < 2) {
                hexString.append(0);
            }
            hexString.append(shaHex);
        }
        return hexString.toString();
    }

    /**
     * 将16进制转换为二进制
     */
    public static String hexToString(String hexStr) {
        byte[] bytes = hexToBytes(hexStr);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 将16进制转换为二进制
     */
    public static byte[] hexToBytes(String hexStr) {
        if (StringUtils.isEmpty(hexStr)) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static String join(String separator, Object... strings) {
        return join(Lists.newArrayList(strings), separator);
    }

    /**
     * 以分隔符分隔，拼接List中每个对象的某个字段
     * @param list 数组
     * @param fieldName 字段名
     * @param <T> List数据的范型
     */
    public static <T> String join(List<T> list, String fieldName, String separator) {
        if (!CollectionUtils.isNotEmpty(list)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Object fieldValue = ReflectUtils.getFieldValue(list.get(i), fieldName);
            if (fieldName != null) {
                stringBuilder.append(fieldValue);
            }
            if (i != list.size() - 1) {
                stringBuilder.append(separator);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 以分隔符分隔，拼接List中每个id
     * @param list id集合
     */
    public static <T> String join(List<T> list, String separator) {
        if (!CollectionUtils.isNotEmpty(list)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            stringBuilder.append(t);
            if (i != list.size() - 1) {
                stringBuilder.append(separator);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 首字母转换小写
     * @param param 需要转换的字符串
     * @return 转换好的字符串
     */
    public static String firstToLowerCase(String param) {
        if (param == null || param.length() == 0) {
            return "";
        }
        return param.substring(0, 1).toLowerCase() + param.substring(1);
    }

}
