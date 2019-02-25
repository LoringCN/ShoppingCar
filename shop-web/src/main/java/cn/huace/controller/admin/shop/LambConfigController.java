package cn.huace.controller.admin.shop;

import cn.huace.common.bean.HttpFrontResult;
import cn.huace.controller.admin.base.AdminBasicController;
import cn.huace.shop.bluetooth.Vo.LambConfigVo;
import cn.huace.shop.bluetooth.service.LambConfigService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * LED灯 控制层
 */

@Slf4j
@RestController
@Api(value = "/admin/led",description = "LED灯")
@RequestMapping(value = "/admin/led")
public class LambConfigController extends AdminBasicController {

    @Autowired
    private LambConfigService lambConfigService;

    /**
     * 查询LED分页列表方法
     * @param vo
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value="list",method=RequestMethod.POST)
    public HttpFrontResult findPage(LambConfigVo vo, @RequestParam(defaultValue="1") Integer page,
                                       @RequestParam(defaultValue="10") Integer rows){
        log.info("*** 查询LED分页列表方法：findlist(),入参：vo:{},page:{},rows:{}",JSONObject.fromObject(vo).toString(),page,rows);
        return HttpFrontResult.createSuccess(lambConfigService.findPage(vo, page, rows));
    }

    /**
     * 查询LED详情信息
     * @param id
     * @return
     */
    @RequestMapping(value="find",method=RequestMethod.POST)
    public HttpFrontResult findById(Integer id){
        log.info("*** 查询LED详情信息：findById(),入参：id:{}",id);
        return HttpFrontResult.createSuccess(lambConfigService.findbyId(id));
    }

}
