package cn.huace.common.service;


import cn.huace.common.entity.BizCode;
import cn.huace.common.entity.BizCodeType;
import cn.huace.common.repository.BizCodeRepository;
import cn.huace.common.vo.BizCodeTypeVo;
import cn.huace.common.vo.BizCodeVo;
import org.apache.commons.lang3.StringUtils;
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
 * 基础字典 服务层created on 2018-06-12
 * @author Loring
 */
@Service
public class BizcodeService extends BaseService<BizCode,Integer> {
    @Autowired
    private BizCodeRepository bizCodeRepository;

    @Autowired
    private BizcodeTypeService bizcodeTypeService;
    /**
     * 根据字典类型查询字典
     *
     * @param typeCode
     * @return
     */
    public List<BizCode> findByTypeCode(String typeCode) {
        return bizCodeRepository.findByTypeCode(typeCode);
    }

    /**
     * 查询字典
     *
     * @param typeCode
     * @param itemCode
     * @return
     */
    public BizCode findByTypeAndCode(String typeCode, String itemCode) {
        return bizCodeRepository.findByTypeAndCode(typeCode, itemCode);
    }

    public BizCodeVo findById(Integer codeCode) {
        return PoToVo(findOne(codeCode), new BizCodeVo());
    }

    /**
     * 查询
     * @param vo
     * @param page
     * @param rows
     * @return
     */
    public Page<BizCodeVo> list( BizCodeVo vo ,Integer page,Integer rows){
        Map<String,Object> searchMap = new HashMap<>();
        if(vo.getBizCodeTypeVo() != null){
            searchMap.put("EQ_bizCodeType.codeType",vo.getBizCodeTypeVo().getCodeType());
        }
        if(vo.getCodeName() != null){
            searchMap.put("LIKE_codeName",vo.getCodeName());
        }
        Page<BizCode> pagelist = findAll(searchMap,page,rows,Sort.Direction.DESC,"itemCode");
        List<BizCodeVo> retData = pagelist.getContent().stream().map(bizCode -> PoToVo(bizCode,new BizCodeVo())).collect(Collectors.toList());
        return new PageImpl<>(retData,null,pagelist.getTotalElements());
    }

    /**
     * save
     * @param vo
     * @return
     */
    public Boolean edit(BizCodeVo vo){
        BizCode po = new BizCode();
        if(vo.getCodeCode() != null){
         po = findOne(vo.getCodeCode())==null?new BizCode():findOne(vo.getCodeCode());
        }
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
    public BizCodeVo PoToVo(BizCode source, BizCodeVo target) {
        BeanUtils.copyProperties(source, target);
        target.setCodeCode(source.getCodeCode());
        target.setBizCodeTypeVo(bizcodeTypeService.PoToVo(source.getBizCodeType(),new BizCodeTypeVo()));
        return target;
    }

    /**
     * VO TO PO
     * @param source
     * @param target
     * @return
     */
    public  BizCode VoToPo(BizCodeVo source,BizCode target){
        BeanUtils.copyProperties(source,target);
        if(StringUtils.isNotBlank(source.getCodeType())){
            BizCodeType bizCodeType = bizcodeTypeService.findOne(source.getCodeType());
            target.setBizCodeType(bizCodeType);
        }
        return target;
    }

}
