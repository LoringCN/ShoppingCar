package cn.huace.goods.repository.jpa;

import cn.huace.common.repository.BaseRepository;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.entity.GoodsRenewal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Loring on 2018/11/15.
 */
public interface GoodsRenewalRepository extends BaseRepository<GoodsRenewal,Integer>{

    @Query("select g from GoodsRenewal g where g.barcode = ?1 and g.shopId = ?2 ORDER BY g.modifiedTime desc")
    List<GoodsRenewal> findByBarcodeAndShopId(String barcode,String shopId);
}
