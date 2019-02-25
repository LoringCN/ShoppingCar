package cn.huace.shop.bluetooth.service;

import cn.huace.common.service.BaseService;
import cn.huace.common.service.BizcodeService;
import cn.huace.shop.bluetooth.Vo.LambConfigVo;
import cn.huace.shop.bluetooth.entity.LambConfig;
import cn.huace.shop.bluetooth.repository.LambConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * LED灯 服务层
 * created by Loring on 2018-06-21
 */
@Service
@Slf4j
public class LambConfigService extends BaseService<LambConfig,Integer> {

    @Autowired
    private LambConfigRepository lambConfigRepository;

    @Autowired
    private BizcodeService bizcodeService;

    /**
     * 根据灯ID查询详情信息
     * @param ledId
     * @return
     */
    public LambConfig findByLedId(String ledId){
        return  lambConfigRepository.findByLedId(ledId);
    }

    /**
     * 根据vo 查询列表
     * @param vo
     * @return
     */
    public Page<LambConfigVo> findPage(LambConfigVo vo , Integer page, Integer rows){

        Map<String,Object> searchMap = new HashMap<>();

//        if(vo.getShopId() == null){
//            return null;
//        }else {
//            searchMap.put("EQ_shop.id",vo.getShopId());
//        }

        //mac
        if(StringUtils.isNotBlank(vo.getMac())){
            searchMap.put("EQ_mac",vo.getMac());
        }
        //ledId
        if(StringUtils.isNotBlank(vo.getLedId())){
            searchMap.put("LIKE_ledId",vo.getLedId());
        }
        //x
        if(vo.getX() != null){
            searchMap.put("EQ_x",vo.getX());
        }
        //y
        if(vo.getY() != null){
            searchMap.put("EQ_y",vo.getY());
        }
        //pulse
        if(StringUtils.isNotBlank(vo.getPulse())){
            searchMap.put("EQ_pulse",vo.getPulse());
        }
        //power
        if(vo.getPower() != null){
            searchMap.put("EQ_power",vo.getPower());
        }

        Page<LambConfig> poPage = findAll(searchMap,page,rows,Sort.Direction.DESC,"modifiedTime");

        List<LambConfigVo> retList = poPage.getContent().stream().map(lambConfig -> PoToVo(lambConfig,new LambConfigVo())).collect(Collectors.toList());

        return new PageImpl<>(retList,null,retList.size());
    }

    /**
     * 根据vo 查询详情
     * @param id
     * @return
     */
    public LambConfigVo findbyId(Integer id){
        LambConfig po = findOne(id);
        return PoToVo(po,new LambConfigVo());
    }

    /**
     * PO TO VO
     * @param source
     * @param target
     * @return
     */
    public LambConfigVo PoToVo(LambConfig source,LambConfigVo target){
        BeanUtils.copyProperties(source,target);
        target.setId(source.getId());
//        target.setShopId(source.getShop().getId());
//        target.setShopName(source.getShop().getName());
        target.setPowerValue(
                bizcodeService.findByTypeAndCode("ledPower",source.getPower().toString()).getCodeName()
        );
        return target;
    }

}
