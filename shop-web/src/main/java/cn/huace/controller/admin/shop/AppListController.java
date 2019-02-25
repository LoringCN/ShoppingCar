package cn.huace.controller.admin.shop;

import cn.huace.common.bean.HttpResult;
import cn.huace.controller.admin.base.AdminBasicController;
import cn.huace.shop.app.service.AppListService;
import cn.huace.shop.app.vo.AppListVo;
import cn.huace.sys.bean.ShiroUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Loring on 2017/7/24.
 */
@Slf4j
@RestController
@Api(value = "/admin/appList",description = "app集合")
@RequestMapping(value = "/admin/appList")
public class AppListController extends AdminBasicController{

    @Autowired
    private AppListService appListService;

    @ApiOperation(value = "app大类列表查询", notes = "app大类列表查询")
    @RequestMapping(value="list",method=RequestMethod.POST)
    public HttpResult list(AppListVo vo,
                           @RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "20") Integer rows){
        log.info("*** app大类列表查询 开始：list(): 入参：vo={},page={},rows={}",vo.toString(),page,rows);
        HttpResult httpResult ;
        Page<AppListVo> retPage = appListService.list(vo,page,rows);
        httpResult = HttpResult.createSuccess(retPage);
        log.info("*** app大类列表查询 结束：list(): 出参：httpResult={}",httpResult.toString());
        return httpResult;
    }

    @ApiOperation(value = "app大类主键查询", notes = "app大类主键查询")
    @RequestMapping(value="preEdit",method=RequestMethod.POST)
    public HttpResult preEdit(Integer id){
        log.info("*** app大类主键查询 开始：preEdit(): 入参：id={}",id);
        HttpResult httpResult;
        AppListVo retVo = appListService.preEdit(id);
        httpResult = HttpResult.createSuccess(retVo);
        log.info("*** app大类主键查询 结束：preEdit(): 出参：httpResult={}",httpResult.toString());
        return httpResult;
    }

    @ApiOperation(value = "app大类保存", notes = "app大类保存")
    @RequestMapping(value="save",method=RequestMethod.POST)
    public HttpResult save(AppListVo vo){
        log.info("*** app大类保存 开始：save(): 入参：vo={}",JSONObject.fromObject(vo).toString());
        HttpResult httpResult;
        ShiroUser user = getCurrentUser();
        if(StringUtils.isBlank(vo.getCreator())){
            vo.setCreator(user.getAccount());
        }
        if(StringUtils.isBlank(vo.getModifier())){
            vo.setModifier(user.getAccount());
        }
        vo.setIsEnabled(true);

        if(appListService.saveVo(vo)){
            httpResult =  HttpResult.createSuccess("保存成功！");
        }else {
            httpResult = HttpResult.createFAIL("保存失败！");
        }
        log.info("*** app大类保存 结束：save(): 出参：httpResult={}",httpResult.toString());
        return httpResult;
    }

    @ApiOperation(value = "app大类删除", notes = "app大类删除")
    @RequestMapping(value="delete",method=RequestMethod.POST)
    public HttpResult delete(Integer id){
        log.info("*** app大类删除 开始：delete(): 入参：id={}",id);
        HttpResult httpResult ;
        httpResult =appListService.deleteById(id)?HttpResult.createSuccess("删除成功！"):HttpResult.createFAIL("删除失败！");
        log.info("*** app大类删除 结束：delete(): 出参：httpResult={}",httpResult.toString());
        return httpResult;
    }

    @ApiOperation(value = "查詢商店下的app大类", notes = "查詢商店下的app大类")
    @RequestMapping(value="listByshopId",method=RequestMethod.POST)
    public HttpResult listByshopId(Integer shopId){
        log.info("*** 查詢商店下的app大类 开始：listByshopId(): 入参：shopId={}",shopId);
        HttpResult httpResult ;
        httpResult = HttpResult.createSuccess(appListService.listByShopId(shopId));
        log.info("*** 查詢商店下的app大类 结束：listByshopId(): 出参：httpResult={}",httpResult.toString());
        return httpResult;
    }


}
