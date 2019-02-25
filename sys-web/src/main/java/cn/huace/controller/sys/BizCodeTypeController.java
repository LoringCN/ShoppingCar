package cn.huace.controller.sys;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.entity.BizCodeType;
import cn.huace.common.service.BizcodeTypeService;
import cn.huace.common.vo.BizCodeTypeVo;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.sys.bean.ShiroUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.util.List;

/**
 *  dictionary definition manage
 *  created by Loring on 2018-05-24
 */
@Slf4j
@RestController
@Api(value = "/admin/codeType",description = "字典定义管理")
@RequestMapping(value = "/admin/codeType")
public class BizCodeTypeController extends AdminBasicController {

    @Autowired
    private BizcodeTypeService bizcodeTypeService;

    /**
     * single query by typeCode
     * @param codeType
     * @return
     */
    @RequestMapping(value = "find", method=RequestMethod.POST)
    public HttpResult findOne(String codeType)
    {
        BizCodeTypeVo vo = bizcodeTypeService.findByTypeCode(codeType);
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
    public HttpResult list( BizCodeTypeVo vo,
            @RequestParam(defaultValue="1") Integer page,
            @RequestParam(defaultValue="20") Integer rows){
          Page<BizCodeTypeVo> pageResult = bizcodeTypeService.list(vo, page, rows);
        return HttpResult.createSuccess(pageResult);
    }

    /**
     * save function contain add and edit
     * @param vo
     * @return
     */
    @RequestMapping(value = "save",method = RequestMethod.POST)
    public HttpResult save(BizCodeTypeVo vo) {
        ShiroUser user = getCurrentUser();
        if(StringUtils.isBlank(vo.getCreator())){
            vo.setCreator(user.getAccount());
        }
        if(StringUtils.isBlank(vo.getModifier())){
            vo.setModifier(user.getAccount());
        }
        //默认有效数据
        vo.setIsEnabled(null!= vo.getIsEnabled()?vo.getIsEnabled():true);

        return HttpResult.createSuccess(bizcodeTypeService.edit(vo));
    }

    @ApiOperation(value = "字典类 删除 方法")
    @RequestMapping(value = "/delete/{codeType}")
    public HttpResult delete(@PathVariable("codeType") String codeType){
        log.info("*** 字典类 删除方法 开始：delete()，入参 codeType：{}",codeType);
        HttpResult httpResult;
        bizcodeTypeService.delete(codeType);
        httpResult = HttpResult.createSuccess("对象删除成功!");
        log.info("*** 字典类 删除方法 结束：delete()，出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return  httpResult;
    }

    @ApiOperation(value = "字典分类 下拉框方法")
    @RequestMapping(value = "/select")
    public HttpResult select(){
        log.info("*** 字典分类 下拉框方法 开始");
        HttpResult httpResult;
        httpResult = HttpResult.createSuccess(bizcodeTypeService.findAll());
        log.info("*** 字典分类 下拉框方法 结束：delete()，出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return  httpResult;
    }


}
