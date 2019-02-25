package cn.huace.controller.admin.shop;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.HttpStreamUtils;
import cn.huace.shop.app.entity.Alarm;
import cn.huace.shop.app.service.AlarmService;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.SystemUser;
import cn.huace.sys.service.SystemUserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by herry on 2017/5/22.
 */

@Slf4j
@RestController
@Api(value = "/admin/alarm", description = "告警信息管理")
@RequestMapping(value = "/admin/alarm")
public class AlarmController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private SystemUserService systemUserService;

    @RequestMapping
    public HttpResult list(Integer shopId, @RequestParam Integer page, @RequestParam(defaultValue = "10") Integer rows) {
        log.info("shopId : " + shopId + ",page : " + page + ",rows : " + rows);
        Map<String, Object> searchParams = new HashedMap();
        if (shopId != null) {
            searchParams.put("EQ_shop.id", shopId);
        } else {
            ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
            SystemUser systemUser = systemUserService.findOne(user.getId());
            if (systemUser.getType() != 0) {
                String shopIds = systemUser.getShopIds();
                if (shopIds.split(",").length == 1) {
                    searchParams.put("EQ_shop.id", Integer.parseInt(shopIds.trim()));
                } else {
//                    取列表的第一个的商店的数据
                    searchParams.put("EQ_shop.id", Integer.parseInt(shopIds.split(",")[0]));
                }
            }
        }
        Page<Alarm> pageResult = alarmService.findAll(searchParams, page, rows, Sort.Direction.DESC, "id");
        return HttpResult.createSuccess(pageResult);
    }


    @RequestMapping("/save")
    public HttpResult save(@RequestParam MultipartFile ledList, @RequestParam Integer shopId, Alarm entity) {
        log.info("ledList : " + ledList + ",shopId : " + shopId);
        try {
//            ObjectMapper mapper = new ObjectMapper();
//            JavaType type = mapper.getTypeFactory().constructParametricType(ArrayList.class, String.class);
//           List<String> data =  mapper.readValue(ledList.getInputStream(), type);
//           log.info("data : " + data);
            String data = HttpStreamUtils.readStream(ledList.getInputStream());
            log.info("data : " + data);
            Shop shop = shopService.findOne(shopId);
            if (entity.isNew()) {
                alarmService.deleteAllByShopId(shopId);
                entity.setShop(shop);
                entity.setLedIdlist(data.toString());
                Alarm saved = alarmService.save(entity);
                log.info("saved : " + saved);
            } else {
                Alarm oldEntity = alarmService.findOne(entity.getId());
                oldEntity.setShop(shop);
                oldEntity.setLedIdlist(data.toString());
                List<Alarm> alarmList = new ArrayList<Alarm>();
                alarmList.add(oldEntity);
                int count = alarmService.batchUpdate(alarmList);
                log.info("updated : " + count);
            }

        } catch (IOException e) {
            log.error("parse file error,", e);
            return HttpResult.createFAIL("error json file.");
        }
        return HttpResult.createSuccess("success");
    }


    @RequestMapping(value = "/del", method = RequestMethod.POST)
    public HttpResult delete(Integer id) {
        log.info("delete item , id : " + id);
        alarmService.delete(id);
        return HttpResult.createSuccess("删除完毕！");
    }
}
