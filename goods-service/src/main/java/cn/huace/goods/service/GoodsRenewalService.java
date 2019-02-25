package cn.huace.goods.service;

import cn.huace.common.service.BaseService;
import cn.huace.goods.entity.GoodsRenewal;
import cn.huace.goods.repository.jpa.GoodsRenewalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * created by Loring on 2018-11-15
 */
@Slf4j
@Service
public class GoodsRenewalService extends BaseService<GoodsRenewal,Integer> {

    @Autowired
    private GoodsRenewalRepository goodsRenewalRepository;

    public GoodsRenewal findByBarcodeAndShopId(String barcode,String shopId){
        List<GoodsRenewal> list = goodsRenewalRepository.findByBarcodeAndShopId(barcode,shopId);
        if(list.size()>0){
            return list.get(0);
        }else{
            return null;
        }

    }

}
