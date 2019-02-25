package cn.huace.goods.service;

import cn.huace.common.service.BaseService;
import cn.huace.goods.entity.GoodsLocation;
import cn.huace.goods.repository.jpa.GoodsLocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * created by Loring on 2018-12-6
 */
@Slf4j
@Service
public class GoodsLocationService extends BaseService<GoodsLocation,String> {

    @Autowired
    private GoodsLocationRepository goodsLocationRepository;

    public GoodsLocation findByBarCode(String barcode){
       List<GoodsLocation> list = goodsLocationRepository.findByBarCode(barcode);
       if(list.size()>0){
           return list.get(0);
       }else{
           return null;
       }
    }

}
