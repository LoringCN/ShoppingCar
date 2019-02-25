package cn.huace.goods.repository.jpa;

import cn.huace.common.repository.BaseRepository;
import cn.huace.goods.entity.GoodsCategory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 商品分类
 * Created by yld on 2017/7/20.
 */
public interface GoodsCategoryRepository extends BaseRepository<GoodsCategory,Integer>{
    /**
     * 查询所有商品分类，返回集合数据(后台用)
     */
    @Query("select new GoodsCategory(gc.id,gc.catName) from GoodsCategory gc where gc.shop.id = ?1 and gc.flag = '1'" )
    List<GoodsCategory> findByShopId(Integer shoId);

    /**
     * 查询所有商品分类，返回集合数据,客户端用，排除特殊分类,只显示正常商品分类
     */
    @Query("select new GoodsCategory(gc.id,gc.catName) from GoodsCategory gc where gc.shop.id = ?1 and gc.flag = '1' and gc.specialMark = false " )
    List<GoodsCategory> findByShopIdForApp(Integer shoId);

    /**
     * 根据分类名和超市Id查询
     */
    @Query("select gc from GoodsCategory gc where gc.catName = ?1 and gc.shop.id = ?2 and gc.flag = '1'")
    GoodsCategory findByCatNameAndShopId(String catName,Integer shopId);

    @Query("delete from GoodsCategory gc where gc.shop.id = ?1")
    @Modifying
    void deleteAllByShopId(Integer shopId);
}
