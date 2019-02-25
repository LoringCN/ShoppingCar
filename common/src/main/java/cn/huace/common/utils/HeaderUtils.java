package cn.huace.common.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by herry on 2017/6/12.
 * <p>
 * 与公共头部有关的操作
 */
public class HeaderUtils {
    public static final String APP_VERSION = "hc_version";
    public static final String APP_CODE = "hc_code";
    public static final String APP_PACKAGE = "hc_package";
    public static final String APP_AGENT = "hc_agent";
    public static final String APP_ADDRESS = "hc_address";

    public static String getAppVersion(HttpServletRequest request) {
        return request.getHeader(APP_VERSION);
    }

    public static Integer getAppCode(HttpServletRequest request) {
        try {
            return Integer.parseInt(request.getHeader(APP_CODE));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String getAppPackage(HttpServletRequest request) {
        return request.getHeader(APP_PACKAGE);
    }

    public static String getAppAgent(HttpServletRequest request) {
        return request.getHeader(APP_AGENT);
    }

    public static String getAppAddress(HttpServletRequest request) {
        return request.getHeader(APP_ADDRESS);
    }

}
