package cn.huace.goods.repository.jpa;

import cn.huace.common.repository.BaseRepository;
import cn.huace.goods.entity.GoodsClassLoc;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Loring on 2019/1/10.
 */
public interface GoodsClassLocRepository extends BaseRepository<GoodsClassLoc,Integer>{

    @Query("select g from GoodsClassLoc g where g.classificationId = ?1 and g.shopId = ?2 ORDER BY g.modifiedTime desc")
    List<GoodsClassLoc> findByclassIdAndShopId(Integer classificationId, Integer shopId);
}
