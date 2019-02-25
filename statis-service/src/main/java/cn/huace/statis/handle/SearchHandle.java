package cn.huace.statis.handle;

import cn.huace.statis.search.entity.StatisSearch;
import cn.huace.statis.search.service.StatisSearchService;
import cn.huace.statis.utils.StatContants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Created by yld on 2017/12/24.
 */
@Component
@Slf4j
public class SearchHandle implements BasicHandle{

    @Autowired
    private StatisSearchService statisSearchService;

    @Override
    public void readLog(Map<String, String> str, String timeStr) {
        StatisSearch statisSearch = new StatisSearch();
        statisSearch.setSearchKey(str.get(StatContants.SEARCH_KEY.KEYWORD));
        statisSearch.setShopId(Integer.parseInt(str.get(StatContants.SEARCH_KEY.SHOP_ID)));
        statisSearch.setResult(Integer.parseInt(str.get(StatContants.SEARCH_KEY.RESULT)));
        statisSearch.setRelGoods(str.get(StatContants.SEARCH_KEY.REL_GOODS));
        if(!StringUtils.isEmpty(timeStr)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                statisSearch.setSearchTime(sdf.parse(timeStr.replace("_"," ")));
            } catch (ParseException e) {
                log.error("***** 解析时间出错，timeStr = {}",timeStr);
            }
        }
        statisSearchService.save(statisSearch);
    }

    @Override
    public void handleData(String timeStr) {

    }
}
