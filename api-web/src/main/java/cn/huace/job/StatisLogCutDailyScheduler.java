package cn.huace.job;

import cn.huace.statis.utils.StatContants;
import cn.huace.statis.utils.StatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 每天12点手动触发logback按天切割日志
 * Seconds Minutes Hours DayofMonth Month DayofWeek Year或
 * Seconds Minutes Hours DayofMonth Month DayofWeek
 * Date:2018/4/13
 */
@Slf4j
@Component
public class StatisLogCutDailyScheduler {

    @Scheduled(cron = "0 5 0 * * ?")
    public void statisLogCutDaily(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("********* 定时添加日志，触发日志切割！,logTime = {}",sdf.format(new Date()));
        int shopId = 15;
        int searchResult = 1;
        int resultGoodsId = 486;
        String goodsName = "牛奶";

        List<String> args = new ArrayList<>();
        args.add(StatContants.STAT_SHOP_ID+StatContants.STAT_COLON+shopId);
        args.add(StatContants.SEARCH_KEY.KEYWORD+StatContants.STAT_COLON+goodsName);
        args.add(StatContants.SEARCH_KEY.RESULT+StatContants.STAT_COLON+searchResult);
        args.add(StatContants.SEARCH_KEY.REL_GOODS+StatContants.STAT_COLON+resultGoodsId);
        StatUtils.stat(StatContants.STAT_TYPE_SEARCH,args.toArray(new String[args.size()]));
    }
}
