package cn.huace.statis.handle;

import cn.huace.statis.ub.entity.StatisUb;
import cn.huace.statis.ub.entity.StatisUbNavi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandleData {
    //    public static Map<Long, UserInfo> userInfoMap = new ConcurrentHashMap<Long, UserInfo>(10000);
    public static Map<String, StatisUb> statisUbMap = new ConcurrentHashMap<String, StatisUb>();
    public static Map<String, StatisUbNavi> statisUbNaviMap = new ConcurrentHashMap<String, StatisUbNavi>();
    //    增加一个map来存储track数据
//    public static List<HeatMap> heatMapList = Collections.synchronizedList(new LinkedList());
    public static Map<Integer, Map<String, Integer>> xAndYMap = new ConcurrentHashMap<Integer, Map<String, Integer>>();
//    public static Map<String, Integer> xAndYMap2 = new ConcurrentHashMap<String, Integer>();

    //    public static Map<Integer, LiveChannel> channelKeyMap = new ConcurrentHashMap<Integer, LiveChannel>();
//    public static Map<String, LiveChannelUser> channelUserMap = new ConcurrentHashMap<String, LiveChannelUser>(10000);
//    public static Map<Integer, LiveChannelDay> channelPlayDayMap = new ConcurrentHashMap<Integer, LiveChannelDay>(10000);
//    public static Map<Integer, UserInfoDay> cityUserDayMap = new ConcurrentHashMap<Integer, UserInfoDay>();
//    public static Map<String, HomeTvVideo> homeTvVideoMap = new ConcurrentHashMap<String, HomeTvVideo>();
//    public static Map<Integer, Integer> homeTvSubjectDayMap = new ConcurrentHashMap<Integer, Integer>();
//    public static Map<Integer, Integer> homeTvSubjectDayNewCountMap = new ConcurrentHashMap<Integer, Integer>();
//    public static Map<String, Weixin> weixinPlayMap = new ConcurrentHashMap<String, Weixin>();
    public static void freshData() {
        statisUbNaviMap = new ConcurrentHashMap<String, StatisUbNavi>();
        statisUbMap = new ConcurrentHashMap<String, StatisUb>();
//       channelKeyMap = new ConcurrentHashMap<Integer, LiveChannel>();
//       cityUserDayMap=new ConcurrentHashMap<Integer, UserInfoDay>();
//       channelUserMap = new ConcurrentHashMap<String, LiveChannelUser>(10000);
//       channelPlayDayMap = new ConcurrentHashMap<Integer, LiveChannelDay>(10000);
//       homeTvVideoMap = new ConcurrentHashMap<String, HomeTvVideo>();
//       homeTvSubjectDayMap=new ConcurrentHashMap<Integer, Integer>();
//       homeTvSubjectDayNewCountMap=new ConcurrentHashMap<Integer, Integer>();
//       weixinPlayMap=new ConcurrentHashMap<String, Weixin>();
    }

//    刷新内容
    public static void freshXAndYMap() {
        xAndYMap = new ConcurrentHashMap<Integer, Map<String, Integer>>();
    }

}
