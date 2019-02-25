package cn.huace.controller.ad;

import cn.huace.ad.entity.AdRelationV2;
import cn.huace.ad.service.AdRelationV2Service;
import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.AdminBasicController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yld on 2018/2/27.
 * Date:2018/2/27
 */
@Slf4j
@RestController
@Api(value = "/admin/v2/ad/relation",description = "v2版本广告关联关系")
@RequestMapping(value = "/admin/v2/ad/relation")
public class AdRelationV2Controller extends AdminBasicController{

    @Autowired
    private AdRelationV2Service adRelationV2Service;

    @ApiOperation(value = "查询所有广告关联关系列表")
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public HttpResult listAllAdRelations(){
        log.info("**** 查询所有广告关联关系列表：listAllAdRelation()");
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_flag",true);
        List<AdRelationV2> relationList = adRelationV2Service.findAll(searchMap);
        return HttpResult.createSuccess("查询成功！",relationList);
    }
    @ApiOperation(value = "根据Id查询关联关系详情")
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public HttpResult detail(@PathVariable("id")Integer id){
        return HttpResult.createSuccess("查询详情成功！",adRelationV2Service.findOne(id));
    }
}
