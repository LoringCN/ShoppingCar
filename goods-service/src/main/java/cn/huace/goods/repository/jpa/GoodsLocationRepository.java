package cn.huace.goods.repository.jpa;

import cn.huace.common.repository.BaseRepository;
import cn.huace.goods.entity.GoodsLocation;
import cn.huace.goods.entity.GoodsRenewal;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Loring on 2018/12/6.
 */
public interface GoodsLocationRepository extends BaseRepository<GoodsLocation,String> {

    @Query("select g from GoodsLocation g where g.productId = ?1")
    List<GoodsLocation> findByBarCode(String barcode);

}
