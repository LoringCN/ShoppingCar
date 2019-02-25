package cn.huace.common.utils;

/**
 * Created by Administrator on 2017/3/1.
 */
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class EmojiFilter {


    public static String filterEmoji(String source) {
        if(source != null)
        {
            Pattern emoji = Pattern.compile ("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",Pattern.UNICODE_CASE | Pattern . CASE_INSENSITIVE ) ;
            Matcher emojiMatcher = emoji.matcher(source);
            if ( emojiMatcher.find())
            {
                source = emojiMatcher.replaceAll("*");
                if (StringUtils.isNotBlank(source)){
                    return source ;
                }else {
                    return "未知";
                }
            }
            return source;
        }
        return "未知";
    }
}