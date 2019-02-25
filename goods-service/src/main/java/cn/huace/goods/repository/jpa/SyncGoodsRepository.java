package cn.huace.goods.repository.jpa;

import cn.huace.common.repository.BaseRepository;
import cn.huace.goods.entity.SyncGoods;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by yld on 2017/8/24.
 */
public interface SyncGoodsRepository extends BaseRepository<SyncGoods,Integer>{
    /**
     * 根据oID唯一查询
     */
    @Query("select sg from SyncGoods sg where sg.originId = ?1 and sg.flag = '1'")
    SyncGoods findByOriginId(String oId);
}
