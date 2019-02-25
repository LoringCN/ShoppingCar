package cn.huace.goods.service;

import cn.huace.common.service.BaseService;
import cn.huace.goods.entity.SyncGoods;
import cn.huace.goods.repository.jpa.SyncGoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 *
 * Created by yld on 2017/8/24.
 */
@Slf4j
@Service
public class SyncGoodsService extends BaseService<SyncGoods,Integer>{
    @Autowired
    private SyncGoodsRepository syncGoodsRepository;

    /**
     * 根据oID唯一查询
     */
    public SyncGoods findByOid(String oId) {
        return syncGoodsRepository.findByOriginId(oId);
    }

    /**
     * 批量插入同步数据
     * @param goodsList
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<SyncGoods> batchInsertSyncGoods(List<SyncGoods> goodsList){
        if(CollectionUtils.isEmpty(goodsList)){
            return null;
        }
        int result = batchInsert(goodsList);
        if(result == goodsList.size()){
            log.info("**** 批量插入syncGoods同步数据： "+result);
            return goodsList;
        }
        return null;
    }

    /**
     * 批量更新同步数据
     * @param goodsList
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<SyncGoods> batchUpdateSyncGoods(List<SyncGoods> goodsList){
        if(CollectionUtils.isEmpty(goodsList)){
            return null;
        }
        int result = batchUpdate(goodsList);
        if(result == goodsList.size()){
            log.info("**** 批量更新syncGoods同步数据： "+result);
            return goodsList;
        }
        return null;
    }
}
