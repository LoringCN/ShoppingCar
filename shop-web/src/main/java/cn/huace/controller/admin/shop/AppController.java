package cn.huace.controller.admin.shop;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.Contants;
import cn.huace.common.utils.PostObjectToOss;
import cn.huace.controller.admin.base.AdminBasicController;
import cn.huace.shop.app.entity.App;
import cn.huace.shop.app.service.AppService;
import cn.huace.shop.app.vo.AppVo;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.SystemUser;
import cn.huace.sys.service.SystemUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by huangdan on 2016/12/27.
 */
@Slf4j
@RestController
@Api(value = "/admin/app", description = "app版本管理")
@RequestMapping(value = "/admin/app")
public class AppController extends AdminBasicController {

    @Autowired
    private AppService appService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private SystemUserService systemUserService;

    @RequestMapping(method = RequestMethod.GET)
    public HttpResult list(String name, Integer shopId, Integer appListId, Boolean useFlag, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer rows) {
        log.info("shopId : " + shopId);
        Map<String, Object> searchParams = new HashedMap();
        if (!StringUtils.isBlank(name)) {
            searchParams.put("LIKE_name", name);
        }
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
        if (useFlag != null) {
            searchParams.put("EQ_useFlag", useFlag);
        }
        if (appListId != null) {
            searchParams.put("EQ_appList.id", appListId);
        }

        Page<App> pageResult = appService.findAll(searchParams, page, rows, Sort.Direction.DESC, "id");
        return HttpResult.createSuccess(pageResult);
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public HttpResult save(@RequestParam(value = "app", required = false) MultipartFile app, App entity, Integer shopId) {
        Shop shop = shopService.findOne(shopId);
        if (app != null && !app.isEmpty()) {
            String fileUrl = PostObjectToOss.postFile(app, Contants.App_FOLDER);
            entity.setUrl(fileUrl);
        }
        if (entity.isNew()) {
            try {
                String md5 = DigestUtils.md5DigestAsHex(app.getInputStream());
                entity.setMd5(md5);
            } catch (Exception e) {
                //
                e.printStackTrace();
            }
            entity.setShop(shop);
            appService.save(entity);
        } else {
            App old = appService.findOne(entity.getId());
            if (entity.getUrl() == null) {
                entity.setUrl(old.getUrl());
            }
            old.setName(entity.getName());
            old.setUseFlag(entity.getUseFlag());
            old.setShop(shop);
            old.setPackageName(entity.getPackageName());
            old.setVersionCode(entity.getVersionCode());
            appService.save(old);
        }
        return HttpResult.createSuccess("保存成功！");
    }

    @RequestMapping(value = "del", method = RequestMethod.POST)
    public HttpResult del(Integer id) {
        appService.delete(id);
        return HttpResult.createSuccess("删除成功！");
    }

    @RequestMapping(value = "find", method = RequestMethod.GET)
    public HttpResult find(@RequestParam Integer id) {
        App entity = appService.findOne(id);
        return HttpResult.createSuccess(entity);
    }

    @ApiOperation(value = "保存app分组信息", notes = "保存app分组信息")
    @RequestMapping(value = "saveAppGroupRel", method = RequestMethod.POST)
    public HttpResult saveAppGroupRel(Integer id, Integer[] groupIds) {
        log.info("*** 保存app分组信息 start: saveAppGroupRel()，入参 id:{}，groupIds:{}", id, groupIds);
        if (appService.saveAppGroupRel(id, groupIds)) {
            return HttpResult.createSuccess("保存app分组信息成功！");
        }
        return HttpResult.createFAIL("保存app分组信息失败！");
    }

    @ApiOperation(value = "查询app分组信息", notes = "保存app分组信息")
    @RequestMapping(value = "findAppGroupRel", method = RequestMethod.POST)
    public HttpResult findAppGroupRel(Integer shopId, Integer id, Integer appListVoId) {
        log.info("*** 查询app分组信息 start: findAppGroupRel()，入参 shopId:{}，id:{}", shopId, id);
        return HttpResult.createSuccess("查询成功！", appService.findGroup(shopId, id, appListVoId));
    }

    @ApiOperation(value = "上传APP资源")
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public HttpResult upload(@RequestParam(value = "file", required = false) MultipartFile app) {
        log.info("***** 开始上传APP资源文件！");
        if (app == null || app.isEmpty()) {
            return HttpResult.createFAIL("APP资源文件不能为空！");
        }

        String md5 = null;
        String url = PostObjectToOss.postFile(app, Contants.App_FOLDER);
        if (StringUtils.isEmpty(url)) {
            return HttpResult.createFAIL("上传失败!");
        }
        log.info("***** 上传成功！url = {}", url);
        try {
            md5 = DigestUtils.md5DigestAsHex(app.getInputStream());
        } catch (IOException e) {
            log.error("计算APP文件md5值出错！", e.getMessage());
        }

        log.info("***test by loring");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("url", url);
        resultMap.put("md5", md5);
        return HttpResult.createSuccess("上传成功！", resultMap);
    }

    @ApiOperation(value = "保存APP资源")
    @RequestMapping(value = "saveV3", method = RequestMethod.POST)
    public HttpResult save3(AppVo appvo, Integer[] groupIds) {
        log.info("*** App 保存 start: save3 ,入参：appvo:{}", JSONObject.fromObject(appvo).toString());
        if (appvo.getDeliverScope() == 1 && groupIds.length == 0) {
            return HttpResult.createFAIL("请选择分组或选择默认投放！");
        }
        if (appvo.getDeliverScope() == -1 || appvo.getDeliverScope() == null) {
            //默认投放清空分组选项，防止前端误传
            groupIds = null;
        }
        if (appService.saveApp(appvo, groupIds)) {
            return HttpResult.createSuccess("保存成功！");
        } else {
            return HttpResult.createFAIL("保存失败！");
        }

    }


}
