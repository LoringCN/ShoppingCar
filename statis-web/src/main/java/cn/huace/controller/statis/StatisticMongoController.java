package cn.huace.controller.statis;

import cn.huace.common.bean.HttpResult;
import cn.huace.statis.mongo.entity.StatisticMongoEntity;
import cn.huace.statis.mongo.service.StatisticMongoService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Lin Huan
 * @version 1.0.0
 * @date 2018年9月11日
 * @desc mongodb统计查询
 */
@Slf4j
@RestController
@Api(value = "/admin/ub/mongo", description = "")
@RequestMapping(value = "/admin/ub/mongo")
public class StatisticMongoController {
    @Autowired
    private StatisticMongoService statisticMongoService;

    /**
     * 从mongodb请求数据
     *
     * @param shopId
     * @param type
     * @param date
     * @return
     */
    @RequestMapping(value = "/findList")
    public HttpResult findList(Integer shopId, String type, String date) {
        if (null != shopId && !StringUtils.isEmpty(type) && !StringUtils.isEmpty(date)) {
            List<StatisticMongoEntity> eList = statisticMongoService.findList(shopId, type, date);
            return HttpResult.createSuccess("查询成功", eList);
        }
        return HttpResult.createFAIL("请求参数不完整");
    }
}
