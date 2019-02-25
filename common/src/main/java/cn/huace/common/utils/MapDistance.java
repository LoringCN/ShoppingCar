package cn.huace.common.utils;
import java.util.HashMap;
import java.util.Map;

public class MapDistance {
    //赤道半径(单位m)
    private static double EARTH_RADIUS = 6378137;
//    private static double EARTH_RADIUS = 6378.137;
    /**
     * 转化为弧度(rad)
     * */
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 基于余弦定理求两经纬度距离
     * @param lng1 第一点的经度
     * @param lat1 第一点的纬度
     * @param lng2 第二点的经度
     * @param lat2 第二点的纬度
     * @return 返回的距离，单位m
     * */
    public static double getDistance(double lng1, double lat1,double lng2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);

        double radLng1 = rad(lng1);
        double radLng2 = rad(lng2);

        if (radLat1 < 0)
            radLat1 = Math.PI / 2 + Math.abs(radLat1);// south
        if (radLat1 > 0)
            radLat1 = Math.PI / 2 - Math.abs(radLat1);// north
        if (radLng1 < 0)
            radLng1 = Math.PI * 2 - Math.abs(radLng1);// west
        if (radLat2 < 0)
            radLat2 = Math.PI / 2 + Math.abs(radLat2);// south
        if (radLat2 > 0)
            radLat2 = Math.PI / 2 - Math.abs(radLat2);// north
        if (radLng2 < 0)
            radLng2 = Math.PI * 2 - Math.abs(radLng2);// west
        double x1 = EARTH_RADIUS * Math.cos(radLng1) * Math.sin(radLat1);
        double y1 = EARTH_RADIUS * Math.sin(radLng1) * Math.sin(radLat1);
        double z1 = EARTH_RADIUS * Math.cos(radLat1);

        double x2 = EARTH_RADIUS * Math.cos(radLng2) * Math.sin(radLat2);
        double y2 = EARTH_RADIUS * Math.sin(radLng2) * Math.sin(radLat2);
        double z2 = EARTH_RADIUS * Math.cos(radLat2);

        double d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)+ (z1 - z2) * (z1 - z2));
        //余弦定理求夹角
        double theta = Math.acos((EARTH_RADIUS * EARTH_RADIUS + EARTH_RADIUS * EARTH_RADIUS - d * d) / (2 * EARTH_RADIUS * EARTH_RADIUS));
        double dist = theta * EARTH_RADIUS;
        return dist;
    }

    /**
     * 获取当前用户一定距离以内的经纬度值
     * 单位米 return minLat
     * 最小经度 minLng
     * 最小纬度 maxLat
     * 最大经度 maxLng
     * 最大纬度 minLat
     */
    public static Map getAround(String latStr, String lngStr, String raidus) {
        Map map = new HashMap();

        Double latitude = Double.parseDouble(latStr);// 传值给纬度
        Double longitude = Double.parseDouble(lngStr);// 传值给经度

        Double degree = (24901 * 1609) / 360.0; // 获取每度
        double raidusMile = Double.parseDouble(raidus);

        Double mpdLng = Double.parseDouble((degree * Math.cos(latitude * (Math.PI / 180))+"").replace("-", ""));
        Double dpmLng = 1 / mpdLng;
        Double radiusLng = dpmLng * raidusMile;
        //获取最小经度
        Double minLng = longitude - radiusLng;
        // 获取最大经度
        Double maxLng= longitude + radiusLng;

        Double dpmLat = 1 / degree;
        Double radiusLat = dpmLat * raidusMile;
        // 获取最小纬度
        Double minLat = latitude - radiusLat;
        // 获取最大纬度
        Double maxLat = latitude + radiusLat;

        map.put("minLat", minLat+"");
        map.put("maxLat", maxLat+"");
        map.put("minLng", minLng+"");
        map.put("maxLng", maxLng+"");

        return map;
    }

}
