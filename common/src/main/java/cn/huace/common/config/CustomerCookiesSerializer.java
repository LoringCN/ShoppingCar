package cn.huace.common.config;


import org.apache.shiro.codec.Base64;
import org.springframework.session.web.http.CookieSerializer;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huangdan on 2017/4/28.
 */
public class CustomerCookiesSerializer implements CookieSerializer {
    private String cookieName = "SESSION";
    private Boolean useSecureCookie;
    private boolean useHttpOnlyCookie = this.isServlet3();
    private String cookiePath;
    private int cookieMaxAge = -1;
    private String domainName;
    private Pattern domainNamePattern;
    private String jvmRoute;
    private boolean useBase64Encoding;
    private String rememberMeRequestAttribute;

    public CustomerCookiesSerializer() {
    }

    public List<String> readCookieValues(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        ArrayList matchingCookieValues = new ArrayList();
        if(cookies != null) {
            Cookie[] var4 = cookies;
            int var5 = cookies.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Cookie cookie = var4[var6];
                if(this.cookieName.equals(cookie.getName())) {
                    String sessionId = this.useBase64Encoding?this.base64Decode(cookie.getValue()):cookie.getValue();
                    if(sessionId != null) {
                        if(this.jvmRoute != null && sessionId.endsWith(this.jvmRoute)) {
                            sessionId = sessionId.substring(0, sessionId.length() - this.jvmRoute.length());
                        }

                        matchingCookieValues.add(sessionId);
                    }
                }
            }
        }

        return matchingCookieValues;
    }

    public void writeCookieValue(CookieValue cookieValue) {
        HttpServletRequest request = cookieValue.getRequest();
        HttpServletResponse response = cookieValue.getResponse();
        String requestedCookieValue = cookieValue.getCookieValue();
        String actualCookieValue = this.jvmRoute == null?requestedCookieValue:requestedCookieValue + this.jvmRoute;
        Cookie sessionCookie = new Cookie(this.cookieName, this.useBase64Encoding?this.base64Encode(actualCookieValue):actualCookieValue);
        sessionCookie.setSecure(this.isSecureCookie(request));
        sessionCookie.setPath(this.getCookiePath(request));
        String domainName = this.getDomainName(request);
        if(domainName != null) {
            sessionCookie.setDomain(domainName);
        }

        if(this.useHttpOnlyCookie) {
            sessionCookie.setHttpOnly(true);
        }

        if("".equals(requestedCookieValue)) {
            sessionCookie.setMaxAge(0);
        } else if(this.rememberMeRequestAttribute != null && request.getAttribute(this.rememberMeRequestAttribute) != null) {
            sessionCookie.setMaxAge(2147483647);
        } else {
            sessionCookie.setMaxAge(this.cookieMaxAge);
        }

        response.addCookie(sessionCookie);
    }

    private String base64Decode(String base64Value) {
        try {
            byte[] e = Base64.decode(base64Value.getBytes());
            return new String(e);
        } catch (Exception var3) {
            return null;
        }
    }

    private String base64Encode(String value) {
        byte[] encodedCookieBytes = Base64.encode(value.getBytes());
        return new String(encodedCookieBytes);
    }

    public void setUseSecureCookie(boolean useSecureCookie) {
        this.useSecureCookie = Boolean.valueOf(useSecureCookie);
    }

    public void setUseHttpOnlyCookie(boolean useHttpOnlyCookie) {
        if(useHttpOnlyCookie && !this.isServlet3()) {
            throw new IllegalArgumentException("You cannot set useHttpOnlyCookie to true in pre Servlet 3 environment");
        } else {
            this.useHttpOnlyCookie = useHttpOnlyCookie;
        }
    }

    private boolean isSecureCookie(HttpServletRequest request) {
        return this.useSecureCookie == null?request.isSecure():this.useSecureCookie.booleanValue();
    }

    public void setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
    }

    public void setCookieName(String cookieName) {
        if(cookieName == null) {
            throw new IllegalArgumentException("cookieName cannot be null");
        } else {
            this.cookieName = cookieName;
        }
    }

    public void setCookieMaxAge(int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    public void setDomainName(String domainName) {
        if(this.domainNamePattern != null) {
            throw new IllegalStateException("Cannot set both domainName and domainNamePattern");
        } else {
            this.domainName = domainName;
        }
    }

    public void setDomainNamePattern(String domainNamePattern) {
        if(this.domainName != null) {
            throw new IllegalStateException("Cannot set both domainName and domainNamePattern");
        } else {
            this.domainNamePattern = Pattern.compile(domainNamePattern, 2);
        }
    }

    public void setJvmRoute(String jvmRoute) {
        this.jvmRoute = "." + jvmRoute;
    }

    public void setUseBase64Encoding(boolean useBase64Encoding) {
        this.useBase64Encoding = useBase64Encoding;
    }

    public void setRememberMeRequestAttribute(String rememberMeRequestAttribute) {
        if(rememberMeRequestAttribute == null) {
            throw new IllegalArgumentException("rememberMeRequestAttribute cannot be null");
        } else {
            this.rememberMeRequestAttribute = rememberMeRequestAttribute;
        }
    }

    private String getDomainName(HttpServletRequest request) {
        if(this.domainName != null) {
            return this.domainName;
        } else {
            if(this.domainNamePattern != null) {
                Matcher matcher = this.domainNamePattern.matcher(request.getServerName());
                if(matcher.matches()) {
                    return matcher.group(1);
                }
            }

            return null;
        }
    }

    private String getCookiePath(HttpServletRequest request) {
        if (this.cookiePath == null) {
            // 此处改为返回根路径
            return "/";
        }
        return this.cookiePath;
    }

    private boolean isServlet3() {
        try {
            ServletRequest.class.getMethod("startAsync", new Class[0]);
            return true;
        } catch (NoSuchMethodException var2) {
            return false;
        }
    }
}
