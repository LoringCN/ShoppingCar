package cn.huace.common.utils;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by huangdan on 2017/1/18.
 */
public class DateUtils {
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATE_STRING_FORMAT = "yyyyMMdd";

    public static final String DATE_FORMAT_HOUR_MINUTE = "HH:mm";

    public static final String YEARMONTHDATEHOURMINUTESECOND = "yyyyMMddHHmmss";

    public static final String DATE_FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT_FULL_TIME = "yyyy-MM-dd HH:mm:ss.SSS";

    public static Date getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    public static Date getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    public static Date getNextDayTime(int day) {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.add(Calendar.DATE, day);
        return todayEnd.getTime();
    }

    public static Date getStartTime(Date date) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(date);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    public static Date getEndTime(Date date) {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTime(date);
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    public static String getFormatTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    //从上面的方法改过来的，额外的把天后面的时间置为零
    public static Date getNextDayTimes(int day) {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 0);
        todayEnd.set(Calendar.MINUTE, 0);
        todayEnd.set(Calendar.SECOND, 0);
        todayEnd.set(Calendar.MILLISECOND, 0);
        todayEnd.add(Calendar.DATE, day);
        return todayEnd.getTime();
    }

    /**
     * 得到两个指定时间的相差的天数
     */
    public static int getDays(Date endDate, Date startDate) {
        int day = (int) ((getStartTime(endDate).getTime() - getStartTime(startDate).getTime()) / 86400000);
        return day;
    }


    /**
     * DateFormat,格式:yyyyMMddHHmmss
     */
    private static DateFormat dateTimeStrFormat;


    /**
     * @param minute 时长（分钟）
     * @return 格式：33时40分
     */
    public static String formatDurationByMinute(int minute) {
        return formatDuration(minute * 60);
    }

    /**
     * @param second 时长（秒）
     * @return 格式：33时40分22秒
     */
    public static String formatDuration(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }
        StringBuilder dateStr = new StringBuilder();
        if (h != 0) {
            dateStr.append(h + "时");
        }
        if (d != 0) {
            dateStr.append(d + "分");
        }
        if (s != 0) {
            dateStr.append(s + "秒");
        }
        return dateStr.toString();
    }

    /**
     * 显示日期的格式,yyyy-MM-dd HH:mm:ss
     */
    public static final String TIMEF_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String formatDate(Date date) {
        if (date == null)
            return "";
        DateFormat sf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return sf.format(date);
    }

    public static Date parseDate(String str) {
        DateFormat sf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        try {
            return sf.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDate(Date date, String parrent) {
        if (date == null)
            return "";
        if (parrent == null || "".equals(parrent)) {
            return formatDate(date);
        } else {
            DateFormat sf = new SimpleDateFormat(parrent);
            return sf.format(date);
        }
    }

    public static String formatDate(Timestamp date, String parrent) {
        if (date == null)
            return "";
        if (parrent == null || "".equals(parrent)) {
            parrent = "yyyy-MM-dd";
        }
        DateFormat sf = new SimpleDateFormat(parrent);
        return sf.format(date);
    }

    public static Date parseDate(String str, String parrent) {
        if (parrent == null || "".equals(parrent))
            return parseDate(str);
        DateFormat sf = new SimpleDateFormat(parrent);
        try {
            return sf.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 计算两时间所相差的分钟数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static double diffMinute(Date date1, Date date2) {
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        return (Math.abs(time2 - time1)) / (1000 * 60);
    }

    /**
     * @param date yyyyMMddHHmmss格式的日期字符转换为yyyy-MM-dd HH:mm:ss秒格式的字符串
     * @return yyyy-MM-dd HH:mm:ss秒格式的日期字符串
     * @throws ParseException
     */
    public static String convertStringToDateTime(String date)
            throws ParseException {
        return new SimpleDateFormat(TIMEF_FORMAT).format(dateTimeStrFormat
                .parse(date));
    }

    /**
     * 获取今天的日期，格式自定
     *
     * @param pattern - 设定显示格式
     * @return String - 返回今天的日期
     */
    public static String getToday(String pattern) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        DateFormat sdf = getDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getDefault());
        return (sdf.format(now.getTime()));
    }

    /**
     * 将yyMMddHHmmss形式的字符串转换成Date的形式
     *
     * @param date yyMMddHHmmss形式的日期字符串
     * @return Date对象
     * @throws ParseException
     */
    public static Date convertStringToDate(String date) throws ParseException {
        return new SimpleDateFormat(YEARMONTHDATEHOURMINUTESECOND).parse(date);
    }

    /**
     * 字符串转化为日期
     *
     * @param date         日期字符串
     * @param formatString 格式化字符串
     * @return
     * @throws ParseException
     */
    public static Date convertStringToDate(String date, String formatString)
            throws ParseException {
        return new SimpleDateFormat(formatString).parse(date);
    }

    /**
     * 查询传入的时间是星期几
     */
    public static int queryWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * for example "yyyyMMdd" "yyyy-MM-dd HH:mm" etc 将时间转换成字符串连接
     *
     * @param date
     * @param format
     * @return
     */
    public static final String date2String(Date date, String format) {
        if (date == null) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * 将Date转换成字符串“yyyy-mm-dd hh:mm:ss”的字符串
     *
     * @return String 字符串
     */
    public static String getCurrentTime() {
        Date date = new Date();
        return dateToDateString(date, TIMEF_FORMAT);
    }

    /**
     * 将Date转换成formatStr格式的字符串
     *
     * @param date
     * @param formatStr
     * @return
     */
    public static String dateToDateString(Date date, String formatStr) {
        DateFormat df = getDateFormat(formatStr);
        return df.format(date);
    }

    /**
     * 获取定义的DateFormat格式
     *
     * @param formatStr
     * @return
     */
    private static DateFormat getDateFormat(String formatStr) {
        return new SimpleDateFormat(formatStr);
    }

    /**
     * 判断两日期的差是否超过1天 0为未超过，1为超过
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int diffDateGreaterOneDay(Date date1, Date date2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        int betweenYears = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        if (betweenYears > 0) {
            return 1;
        } else if (betweenYears == 0) {
            int betweenMonth = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
            if (betweenMonth > 0) {
                return 1;
            } else if (betweenMonth == 0) {
                return (c2.get(Calendar.DAY_OF_MONTH) - c1
                        .get(Calendar.DAY_OF_MONTH)) > 0 ? 1 : 0;
            }
        }
        return 0;
    }

    /**
     * 计算两时间所相差的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int diffDate(Date date1, Date date2) {
        int diffDay = 0;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        // 保证第二个时间一定大于第一个时间
        if (c1.after(date2)) {
            c1 = c2;
            c2.setTime(date1);
        }
        int betweenYears = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        diffDay = c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR);
        for (int i = 0; i < betweenYears; i++) {
            c1.set(Calendar.YEAR, (c1.get(Calendar.YEAR) + 1));
            diffDay += c1.getMaximum(Calendar.DAY_OF_YEAR);
        }
        return diffDay;
    }

    /**
     * 根据出生日期计算年龄
     *
     * @param birthDay
     * @return 未来日期返回0
     * @throws Exception
     */
    public static int getAge(Date birthDay) {

        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            return 0;
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }

        return age;
    }

    /**
     * 根据出生日期计算年龄
     *
     * @param strBirthDay 字符串型日期
     * @param format      日期格式
     * @return 未来日期返回0
     * @throws Exception
     */
    public static int getAge(String strBirthDay, String format) {

        DateFormat df = new SimpleDateFormat(format);
        Date birthDay = null;
        try {
            birthDay = df.parse(strBirthDay);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return birthDay == null ? 0 : getAge(birthDay);
    }

    public static Date addDate(Date date, String time) {
        if (date == null)
            return null;
        if (time == null || "".equals(time) || time.length() < 2)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String unit = time.substring(time.length() - 1);
        if ("y".equalsIgnoreCase(unit)) {
            cal.add(Calendar.YEAR,
                    Integer.parseInt(time.substring(0, time.length() - 1)));
        } else if ("m".equalsIgnoreCase(unit)) {
            cal.add(Calendar.MONTH,
                    Integer.parseInt(time.substring(0, time.length() - 1)));
        }
        return cal.getTime();
    }

    /**
     * 获得昨天日期
     *
     * @return
     */
    public static String getYesterday() {
        Date date = new Date();
        date = new Date(date.getTime() - 1000 * 60 * 60 * 24);
        SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
        return dateFm.format(date);
    }

    public static String getYesterday(String formatStr) {
        Date date = new Date();
        date = new Date(date.getTime() - 1000 * 60 * 60 * 24);
        SimpleDateFormat dateFm = new SimpleDateFormat(formatStr);
        return dateFm.format(date);
    }

    /**
     * 获得两日期的时间差
     *
     * @param date1
     * @param date2
     * @param type  "hour","min","sec","ms"
     * @return
     */
    public static long getDiffTime(String date1, String date2, String type) {
        long ret = 0;
        try {
            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date begin = dfs.parse(date1);
            Date end = dfs.parse(date2);
            long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
            if ("ms".equalsIgnoreCase(type)) {
                ret = between * 1000;
            } else if ("sec".equalsIgnoreCase(type)) {
                ret = between;
            } else if ("min".equalsIgnoreCase(type)) {
                ret = between / 60;
            } else if ("hour".equalsIgnoreCase(type)) {
                ret = between / (60 * 60);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 获得今天是一周的周几
     *
     * @return
     */
    public static String getCurDayOfWeek() {
        // 获得今天是一周的周几，从周日(0)开始
        GregorianCalendar obj = new GregorianCalendar();
        obj.setTime(new java.util.Date());
        int week = obj.get(GregorianCalendar.DAY_OF_WEEK) - 1;
        String dayOfWeek = "";
        switch (week) {
            case 0:
                dayOfWeek = "周日";
                break;
            case 1:
                dayOfWeek = "周一";
                break;
            case 2:
                dayOfWeek = "周二";
                break;
            case 3:
                dayOfWeek = "周三";
                break;
            case 4:
                dayOfWeek = "周四";
                break;
            case 5:
                dayOfWeek = "周五";
                break;
            case 6:
                dayOfWeek = "周六";
                break;
            default:
                break;
        }
        return dayOfWeek;
    }

    /**
     * 返回传入日期的星期
     *
     * @param s
     * @return
     */
    public static String getDayOfWeek(String s) {
        final String dayNames[] =
                {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        try {
            date = sdfInput.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0) {
            dayOfWeek = 0;
        }
        return (dayNames[dayOfWeek]);
    }

    /**
     * 根据原来的时间（Date）获得相对偏移 N 天的时间（Date）
     *
     * @param protoDate  原来的时间（java.util.Date）
     * @param dateOffset （向前移正数，向后移负数）
     * @return 时间（java.util.Date）
     */
    public static Date getOffsetDayDate(Date protoDate, int dateOffset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(protoDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)
                - dateOffset);
        return cal.getTime();
    }

    /**
     * 获得第一个月时间
     *
     * @return
     */
    public static Date getFirstDayOfMonth() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DATE, c.getActualMinimum(Calendar.DATE));
        return c.getTime();
    }

    /**
     * 获得第一个月时间
     *
     * @return
     */
    public static Date getLastDayOfMonth() {
        // 获取Calendar
        Calendar calendar = Calendar.getInstance();
        // 设置时间,当前时间不用设置
        // 设置日期为本月最大日期
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        return calendar.getTime();
    }

    /**
     * 可以获取指定时间的前后几天
     *
     * @param date
     * @param day
     * @return
     */
    public static Date getNextStartTime(Date date, int day) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(date);
        todayStart.add(Calendar.DATE, day);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    //获取指定时间的星期数
    public static String getWeek(Date date) {
        String[] weeks = {"周天", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekOfIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekOfIndex < 0) {
            weekOfIndex = 0;
        }
        return weeks[weekOfIndex];
    }

    //得到指定时间的天数
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(calendar.DAY_OF_MONTH);
        return day;
    }

    //得到指定时间的年份
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    //得到指定时间的月数
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        return month;
    }

}
