package cn.huace.controller.sys;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.entity.BizCode;
import cn.huace.common.service.BizcodeService;
import cn.huace.common.vo.BizCodeVo;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.sys.bean.ShiroUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 *  dictionary  manage
 *  created by Loring on 2018-05-24
 */
@Slf4j
@RestController
@Api(value = "/admin/codecode",description = "字典管理")
@RequestMapping(value = "/admin/codecode")
public class BizCodeController extends AdminBasicController {
    @Autowired
    protected BizcodeService bizcodeService;

    /**
     * get dictionary
     * @param codeType
     * @return
     */
    @RequestMapping(value = "get", method=RequestMethod.POST)
    public HttpResult findByTypeCode(@RequestParam String codeType){
         List<BizCode> list = bizcodeService.findByTypeCode(codeType);
        return HttpResult.createSuccess(list);
    }

    /**
     * single query by typeCode
     * @param codeCode
     * @return
     */
    @RequestMapping(value = "find", method=RequestMethod.POST)
    public HttpResult findOne(Integer codeCode)
    {
        BizCodeVo vo = bizcodeService.findById(codeCode);
        return HttpResult.createSuccess(vo);
    }

    /**
     *  Page query
     * @param vo
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value = "list",method = RequestMethod.POST)
    public HttpResult list( BizCodeVo vo,
                            @RequestParam(defaultValue="1") Integer page,
                            @RequestParam(defaultValue="20") Integer rows){
        Page<BizCodeVo> pageResult = bizcodeService.list(vo, page, rows);
        return HttpResult.createSuccess(pageResult);
    }

    /**
     * save function contain add and edit
     * @param vo
     * @return
     */
    @RequestMapping(value = "save",method = RequestMethod.POST)
    public HttpResult save(BizCodeVo vo) {
        log.info("*** 数据字典保存方法 save() 开始：入参：vo={}",vo.toString());
        ShiroUser user = getCurrentUser();
        if(StringUtils.isBlank(vo.getCreator())){
            vo.setCreator(user.getAccount());
        }
        if(StringUtils.isBlank(vo.getModifier())){
            vo.setModifier(user.getAccount());
        }
        //默认有效数据
        vo.setIsEnabled(null!= vo.getIsEnabled()?vo.getIsEnabled():true);

        return HttpResult.createSuccess(bizcodeService.edit(vo));
    }

    @ApiOperation(value = "字典数据 删除 方法")
    @RequestMapping(value = "/delete/{codeCode}")
    public HttpResult delete(@PathVariable("codeCode") Integer codeCode){
        log.info("*** 字典数据 删除方法 开始：delete()，入参 codeCode：{}",codeCode);
        HttpResult httpResult;
        bizcodeService.delete(codeCode);
        httpResult = HttpResult.createSuccess("对象删除成功!");
        log.info("*** 字典数据 删除方法 结束：delete()，出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return  httpResult;
    }
}
