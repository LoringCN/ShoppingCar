package cn.huace.statis.handle;

import cn.huace.statis.utils.StatContants;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static Map<String, String> PARSERS_Map = new HashMap<String, String>() {
        private static final long serialVersionUID = -2210615264282964448L;
        {
            put(StatContants.STAT_TYPE_UB, "ubHandle");
            put(StatContants.STAT_TYPE_TRACK, "trackHandle");
            put(StatContants.STAT_TYPE_SEARCH,"searchHandle");
        }
    };
    public static final int DAY_TYPE = 0;

    public static final int WEEK_TYPE = 1;

    public static final int MOUTH_TYPE = 2;



}
