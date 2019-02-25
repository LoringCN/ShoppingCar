package cn.huace.goods.repository.jpa;

import cn.huace.common.repository.BaseRepository;
import cn.huace.goods.entity.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yld on 2017/5/9.
 */
public interface GoodsRepository extends BaseRepository<Goods, Integer> {
    /**
     * 查询商品详情
     *
     * @param id
     * @param shopId
     * @return
     */
    @Query("select new Goods(g.id,g.coverImgUrl,g.sid,g.location,g.descr,g.detailImgUrl,g.barcode,g.shop) from Goods g where g.id=?1 and g.shop.id=?2 and g.flag = '1'")
    Goods findGoodsDetailForApp(Integer id, Integer shopId);

    /**
     * 查询所有商品指定字段信息
     */
    @Query("select new Goods(g.id,g.title,g.promotionPrice,g.normalPrice,g.coverImgUrl,g.detailImgUrl) from Goods g where g.shop.id =?1 and g.flag = '1'")
    Page<Goods> findAllGoodsForApp(Integer shopId, Pageable pageable);

    /**
     * 查询所有促销商品指定字段信息
     */
    @Query("select new Goods(g.id,g.title,g.promotionPrice,g.normalPrice,g.coverImgUrl,g.detailImgUrl) from Goods g where g.shop.id =?1 and g.type = ?2 and g.flag = '1'")
    Page<Goods> findAllPromotionGoodsForApp(Integer shopId, String type, Pageable pageable);

    /**
     * 搜索接口
     */
    @Query("select g from Goods g where g.shop.id =?1 and g.flag = '1' and g.title like concat('%',?2,'%') ")
    Page<Goods> searchGoodsByTitleForApp(Integer shopId, String goodsName, Pageable pageable);

    /**
     * 更新商品
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE goods set cover_img_url=?1,detail_img_url=?2,sid=?3,location=?4,descr=?5 where id =?6", nativeQuery = true)
    Integer updateGoodsByBarcode(String coverImgUrl, String detailImgUrl, String sid, String location, String descr, Integer id);

    /**
     * 查询表中商品记录最大ID
     */
    @Query("select max(g.id) from Goods g ")
    Integer findMaxId();

    /**
     * 查询表中商品记录最小ID
     */
    @Query("select min(g.id) from Goods g ")
    Integer findMinId();

    /**
     * 查询所有商品Id,排除删除的商品
     */
    @Query("select g.id from Goods g where g.shop.id = ?1 and g.flag = '1' and g.type <> 'other'")
    List<Integer> findAllGoodsId(Integer shopId);

    /**
     * 查询举荐商品
     */
    @Query("select new Goods(g.id,g.title,g.promotionPrice,g.normalPrice,g.coverImgUrl,g.detailImgUrl) from Goods g where g.shop.id =?1 and g.id in ?2 and g.flag = '1'")
    Page<Goods> findRecommendGoodsForApp(Integer shopId, Integer[] ids, Pageable pageable);

    /**
     * 根据id范围查询商品列表
     *
     * @param ids
     * @param pageable
     * @return
     */
    @Query("select g from Goods g where g.id = ?1")
    Page<Goods> findGoodsListByIds(Integer ids, Pageable pageable);

    /**
     * 根据商品条形码查询
     */
    @Query("select new Goods(g.id,g.title,g.promotionPrice,g.normalPrice,g.coverImgUrl,g.sid,g.location,g.descr,g.detailImgUrl,g.price,g.memberPrice,g.promotionalPrice,g.promotionalSalePrice,g.promotionStartDate,g.promotionEndDate,g.stock,g.barcode) from Goods g where g.barcode = ?1 and g.shop.id = ?2 and g.flag = '1'")
    Goods findGoodsByBarcode(String barcode, Integer shopId);

    /**
     * 查询主题关联商品
     *
     * @param ids
     * @param pageable
     * @return
     */
    @Query("select g from Goods g where g.id in ?1 and g.title like concat('%',?2,'%') and g.flag = '1'")
    Page<Goods> findThemeGoodsByIds(Integer[] ids, String keyword, Pageable pageable);

    /**
     * 查询所有待加入新品举荐列表商品
     */
    @Query("select g from Goods g where g.shop.id = ?1 and g.id in ?2 and g.flag ='1'")
    List<Goods> findAllNewRecommendGoods(Integer shopId, Integer[] ids);

    @Query("select g from Goods g where g.category.id in ?1 and g.shop.id in ?2")
    List<Goods> findGoodsByShopAndCategory(Integer[] catIds, Integer[] shopId);

    @Query("select g from Goods g where g.shop.id = ?1 and (g.sid is null or g.sid = '' or g.sid = 'null') and g.flag = '1' and g.type <>'other'")
    List<Goods> findMapGoods(Integer shopId);

    /**
     * 查询所有商品图片URL
     */
    @Query("select new Goods(g.coverImgUrl,g.detailImgUrl) from Goods g")
    List<Goods> findAllGoodsImgUrls();

    @Query("select barcode from Goods where shop.id = ?1 and barcode <>'' and barcode is not null and flag = '1'")
    List<String> findBarcodesByShopId(Integer shopId);

    @Query("select g from Goods g where g.shop.id = ?1 and g.barcode in (?2)")
    List<Goods> findGoodsInBarcodes(Integer shopId, String[] barcodes);

    @Modifying
    @Query(value = "update Goods t set t.promotionPrice =null, t.type = ?2 WHERE t.shop.id= ?1")
    Integer cleanPromotion(Integer shopId, String str);

    @Query("select g from Goods g where g.barcode = ?1 and g.shop.id = ?2 and g.flag = '1'")
    Goods findByBarcodeAndShopId(String barcode, Integer shopId);

    @Query(value = "SELECT * FROM goods t WHERE t.shop_id = ?1 and NOW() > t.promotion_start_date AND NOW() < t.promotion_end_date ORDER BY LENGTH(t.detail_img_url) DESC, IF (LENGTH(t.sid) > 0, 1, 0) DESC limit ?2,?3",nativeQuery = true)
    List<Goods> findByPromotionGoods(Integer shopId,Integer index,Integer pageSize);
}
