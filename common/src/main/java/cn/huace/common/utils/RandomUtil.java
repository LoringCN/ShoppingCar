package cn.huace.common.utils;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * 生成随机数或字符串工具类
 * Created by yld on 2017/10/30.
 */
public class RandomUtil {
    private static final char[] CHARS
            = new char[]{
                '0','1','2','3','4','5','6','7','8','9',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
            };

    /**
     * 返回指定长度随机字符串
     * @param count 要返回的随机字符串长度
     * @return
     */
    public static String randomString(int count){
        return RandomStringUtils.random(count,CHARS);
    }
}
