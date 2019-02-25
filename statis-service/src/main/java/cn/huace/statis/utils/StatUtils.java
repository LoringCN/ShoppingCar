package cn.huace.statis.utils;

import lombok.extern.slf4j.Slf4j;


/**
 * Created by huangdan on 2017/5/29.
 */
@Slf4j
public class StatUtils {
    public static void stat(String model,String ...args){
            try{
                if (args != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("STATIS ");
                    sb.append(model);
                    sb.append(" ");
                    for (String s : args) {
                        sb.append(s);
                        sb.append("-");
                    }
                    log.info(sb.substring(0, sb.length() - 1));
                } else {
                    log.info("STATIS null");
                }
            }catch (Exception e) {

            }
    }
}
