package cn.huace.common.service;


import cn.huace.common.entity.BizCodeType;
import cn.huace.common.repository.BizCodeTypeRepository;
import cn.huace.common.vo.BizCodeTypeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基础字典定义 服务层. created on 2018-06-12
 * @author Loring
 */
@Service
public class BizcodeTypeService  extends BaseService<BizCodeType, String>
{
    @Autowired
    private BizCodeTypeRepository bizCodeTypeRepository;

    /**
     * 根据字典类型查询字典配置对象
     * @param codeType
     * @return
     */
    public BizCodeTypeVo findByTypeCode(String codeType){
        BizCodeTypeVo vo = PoToVo(bizCodeTypeRepository.findByTypeCode(codeType),new BizCodeTypeVo());
        return vo;
    }

    /**
     * 查询
     * @param vo
     * @param page
     * @param rows
     * @return
     */
    public Page<BizCodeTypeVo> list( BizCodeTypeVo vo ,Integer page,Integer rows){
        Map<String,Object> searchMap = new HashMap<>();
        if(vo.getCodeType() != null){
            searchMap.put("EQ_codeType",vo.getCodeType());
        }
        if(vo.getTypeName() != null){
            searchMap.put("LIKE_typeName",vo.getTypeName());
        }
        Page<BizCodeType> pageList = findAll(searchMap,page,rows,Sort.Direction.DESC,"modifiedTime");
        List<BizCodeTypeVo> retData = pageList.getContent().stream().map(bizCodeType -> PoToVo(bizCodeType,new BizCodeTypeVo())).collect(Collectors.toList());
//        Sort sort = new Sort(Sort.Direction.DESC, "modifiedTime");
//        Pageable pageable = new PageRequest(page, rows, sort);
        return new PageImpl<>(retData,null,pageList.getTotalElements());
    }

    /**
     * save
     * @param vo
     * @return
     */
    public Boolean edit(BizCodeTypeVo vo){
        BizCodeType po = findOne(vo.getCodeType())==null?new BizCodeType():findOne(vo.getCodeType());
        if(save(VoToPo(vo,po)) == null){
            return false;
        }else {
            return true;
        }
    }

    /**
     * PO TO VO
     * @param source
     * @param target
     * @return
     */
    public  BizCodeTypeVo PoToVo(BizCodeType source,BizCodeTypeVo target){
        BeanUtils.copyProperties(source,target);
        return target;
    }

    /**
     * VO TO PO
     * @param source
     * @param target
     * @return
     */
    public  BizCodeType VoToPo(BizCodeTypeVo source,BizCodeType target){
        BeanUtils.copyProperties(source,target);
        return target;
    }


}
