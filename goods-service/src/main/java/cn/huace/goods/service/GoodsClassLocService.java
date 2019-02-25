package cn.huace.goods.service;

import cn.huace.common.service.BaseService;
import cn.huace.goods.entity.GoodsClassLoc;
import cn.huace.goods.repository.jpa.GoodsClassLocRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * created by Loring on 2018-11-15
 */
@Slf4j
@Service
public class GoodsClassLocService extends BaseService<GoodsClassLoc,Integer> {

    @Autowired
    private GoodsClassLocRepository goodsClassLocRepository;

    public GoodsClassLoc findByclassIdAndShopId(Integer classificationId,Integer shopId){
        List<GoodsClassLoc> list = goodsClassLocRepository.findByclassIdAndShopId(classificationId,shopId);
        if(list.size()>0){
            return list.get(0);
        }else{
            return null;
        }

    }

}
