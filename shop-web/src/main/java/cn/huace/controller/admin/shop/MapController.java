package cn.huace.controller.admin.shop;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.Contants;
import cn.huace.common.utils.OssDeletionUtils;
import cn.huace.common.utils.PostObjectToOss;
import cn.huace.controller.admin.base.AdminBasicController;
import cn.huace.shop.map.entity.NaviMap;
import cn.huace.shop.map.service.NaviMapService;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.SystemUser;
import cn.huace.sys.service.SystemUserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@Api(value = "/admin/map", description = "地图版本管理")
@RequestMapping(value = "/admin/map")
public class MapController extends AdminBasicController {

    @Autowired
    NaviMapService naviMapService;

    @Autowired
    ShopService shopService;

    @Autowired
    private SystemUserService systemUserService;

    @RequestMapping(method = RequestMethod.GET)
    public HttpResult list(Integer shopId, Boolean useFlag, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer rows) {
//        log.info("shopId : " + shopId + "useFlag : " + useFlag + ",page : " + page + ",rows : " + rows);
        Map<String, Object> searchMap = new HashMap<String, Object>();
        if (shopId != null) {
            searchMap.put("EQ_shop.id", shopId);
        } else {
            ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
            SystemUser systemUser = systemUserService.findOne(user.getId());
            if (systemUser.getType() != 0) {
                String shopIds = systemUser.getShopIds();
                if (shopIds.split(",").length == 1) {
                    searchMap.put("EQ_shop.id", Integer.parseInt(shopIds.trim()));
                } else {
//                    取列表的第一个的商店的数据
                    searchMap.put("EQ_shop.id", Integer.parseInt(shopIds.split(",")[0]));
                }
            }
        }
        if (useFlag != null) {
            searchMap.put("EQ_useFlag", useFlag);
        }
        Page<NaviMap> result = naviMapService.findAll(searchMap, page, rows, Sort.Direction.DESC, "id");
        return HttpResult.createSuccess("success", result);
    }


    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public HttpResult save(Integer shopId, @RequestParam(value = "navimap", required = false) MultipartFile file, NaviMap entity) {
        Shop shop = shopService.findOne(shopId);
        if (file != null && !file.isEmpty()) {
            String fileUrl = PostObjectToOss.postFile(file, Contants.NaviMap_FOLDER);
            entity.setUrl(fileUrl);
        }
        if (entity.isNew()) {
            try {
                String md5 = DigestUtils.md5DigestAsHex(file.getInputStream());
                entity.setMd5(md5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            entity.setShop(shop);
//            Integer maxVersionId = naviMapService.findMaxMapIdByShop(shopId);
//            if (maxVersionId == null) {
//                maxVersionId = 1;
//            } else {
//                maxVersionId += 1;
//            }
            Integer maxVersionId = (int) (System.currentTimeMillis() / 1000);
            entity.setVersion(maxVersionId);
            naviMapService.save(entity);
        } else {
            NaviMap old = naviMapService.findOne(entity.getId());
            if (entity.getUrl() == null) {
                entity.setUrl(old.getUrl());
            }
            old.setDesc(entity.getDesc());
            old.setUseFlag(entity.isUseFlag());
            old.setShop(shop);
            naviMapService.save(old);
        }
        return HttpResult.createSuccess("保存成功！");
    }

    @RequestMapping(value = "/del", method = RequestMethod.POST)
    public HttpResult delete(Integer id) {
        NaviMap entity = naviMapService.findOne(id);
        naviMapService.delete(id);
        OssDeletionUtils.delete(entity.getUrl());
        return HttpResult.createSuccess("删除成功！");
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public HttpResult find(Integer id) {
        NaviMap entity = naviMapService.findOne(id);
        return HttpResult.createSuccess(entity);
    }
}
