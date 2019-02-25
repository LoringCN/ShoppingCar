package cn.huace.statis.handle;

import java.util.Map;

public interface BasicHandle {
    public void readLog(Map<String,String> str, String timeStr);
    public void handleData(String timeStr);
}
