package cn.huace.goods.service;

import cn.huace.common.service.BaseService;
import cn.huace.goods.entity.GoodsPictrue;
import cn.huace.goods.repository.jpa.GoodsPictrueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * created by Loring on 2018-12-6
 */
@Slf4j
@Service
public class GoodsPictrueService extends BaseService<GoodsPictrue,Integer> {

    @Autowired
    private GoodsPictrueRepository goodsPictrueRepository;

    public GoodsPictrue findByBarcode(String barcode){
        List<GoodsPictrue> list = goodsPictrueRepository.findByBarcode(barcode);
        if(list.size()>0){
            return list.get(0);
        }
        return null;
    }

}
