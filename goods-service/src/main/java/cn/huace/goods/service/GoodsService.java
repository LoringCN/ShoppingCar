package cn.huace.goods.service;

import cn.huace.common.service.BaseService;
import cn.huace.common.utils.jpa.PageUtil;
import cn.huace.common.utils.redis.RedisService;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.enums.GoodsType;
import cn.huace.goods.repository.jpa.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yld on 2017/5/9.
 */
@Slf4j
@Service
public class GoodsService extends BaseService<Goods, Integer> {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private RedisService redisService;

    /**
     * 查询主题关联商品
     *
     * @param ids 商品主键数组
     */
    public Page<Goods> findThemeGoods(Integer[] ids, String keyword, Integer pageNo, Integer pageSize) {
        PageRequest pageRequest = PageUtil.buildPageRequest(pageNo, pageSize, Sort.Direction.DESC, "modifiedTime");
        Page<Goods> goods = goodsRepository.findThemeGoodsByIds(ids, keyword, pageRequest);
        return goods;
    }

    /**
     * 检查商品是否存在
     */
    public boolean isExistGoods(Integer shopId, String goodsName) {
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id", shopId);
        searchMap.put("EQ_title", goodsName);
        searchMap.put("EQ_flag", "1");
        List<Goods> result = findAll(searchMap);
        if (!CollectionUtils.isEmpty(result)) {
            return true;
        }
        return false;
    }

    /**
     * 管理后台查询商品详情
     *
     * @param id 商品Id
     * @return
     */
    public Goods findGoodsDetailForAdmin(Integer id) {
        Goods goodsDetail = goodsRepository.findOne(id);
//        goodsTedail.setShopId(goodsTedail.getShop().getId());
        return goodsDetail;
    }

    /**
     * 管理后台查询所有商品
     *
     * @param searchMap 查询条件字段
     * @param pageNo    页码
     * @param pageSize  每页显示数
     * @return
     */
    public Page<Goods> findAllGoodsForAdmin(Map<String, Object> searchMap, Integer pageNo, Integer pageSize) {

        return findAll(searchMap, pageNo, pageSize, Sort.Direction.DESC, "modifiedTime");
    }

    /**
     * 管理后台查询促销商品和预促销商品
     *
     * @param searchMap 查询条件字段
     * @param pageNo    页码
     * @param pageSize  每页显示数
     * @return
     */
    public Page<Goods> findAllPromotionGoodsForAdmin(Map<String, Object> searchMap, Integer pageNo, Integer pageSize) {

        return findAll(searchMap, pageNo, pageSize, Sort.Direction.DESC, "modifiedTime");
    }

    /**
     * App查询商品详情
     *
     * @param id     商品Id
     * @param shopId 超市Id
     * @return
     */
    public Goods findGoodsDetailForApp(Integer id, Integer shopId) {
        return goodsRepository.findGoodsDetailForApp(id, shopId);
    }

    /**
     * app查询所有商品
     *
     * @param searchMap 查询条件
     * @param pageNo    页码
     * @param pageSize  每页显示数
     * @return
     */
    public Page<Goods> findAllGoodsForApp(Map<String, Object> searchMap, Integer pageNo, Integer pageSize) {

        PageRequest pageRequest = PageUtil.buildPageRequest(pageNo, pageSize, Sort.Direction.DESC,"coverImgUrl","sid");

        return findAll(searchMap, pageRequest);
    }

    /**
     * app查询所有促销商品
     *
     * @param shopId   超市Id
     * @param pageNo   页码
     * @param pageSize 每页显示数
     * @return
     */
    public Page<Goods> findAllPromotionGoodsForApp(Integer shopId, Integer pageNo, Integer pageSize) {

        PageRequest pageRequest = PageUtil.buildPageRequest(pageNo, pageSize, sortDirectionList(), sortFieldList());
        return goodsRepository.findAllPromotionGoodsForApp(shopId, GoodsType.PROMOTION.getValue(), pageRequest);
    }

    /**
     * app搜索商品列表(搜所有)
     *
     * @param shopId    超市ID
     * @param goodsName 商品名
     * @param pageNo    页码
     * @param pageSize  每页显示数
     * @return
     */
    public Page<Goods> searchGoodsForApp(Integer shopId, String goodsName, Integer pageNo, Integer pageSize) {
        PageRequest pageRequest = PageUtil.buildPageRequest(pageNo, pageSize, sortDirectionList(), sortFieldList());
        return goodsRepository.searchGoodsByTitleForApp(shopId, goodsName, pageRequest);
    }

    /**
     *更新商品
     */
    public Integer updateGoodsByBarcode(String coverImgUrl, String detailImgUrl, String sid,
                                        String location, String descr,Integer id) {
        return goodsRepository.updateGoodsByBarcode(coverImgUrl, detailImgUrl, sid, location, descr,id);
    }

    /**
     * 查询举荐商品
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Page<Goods> findRecommendGoodsForApp(Integer shopId, Integer[] ids, Integer pageNo, Integer pageSize) {
//        Page<Goods> recommendGoods = findAll(searchMap,pageNo,pageSize);
        PageRequest pageRequest = PageUtil.buildPageRequest(pageNo, pageSize);
        return goodsRepository.findRecommendGoodsForApp(shopId, ids, pageRequest);
    }

    /**
     * 根据条形码查询商品信息
     *
     * @param barcode
     * @param shopId
     * @return
     */
    public Goods findGoodsByBarcodeForApp(String barcode, Integer shopId) {
        log.info("*****　商品条形码： " + barcode);
//        List<String> barcodeList = new ArrayList<>();
//        barcodeList.add("6934166500057");
//        barcodeList.add("6914068020839");
//        barcodeList.add("6921168511280");
//        Goods goods = null;
//        if(barcodeList.contains(barcode)){
//            goods = goodsRepository.findGoodsByBarcode(barcode,shopId);
//        }else{
//            //随机返回库中商品数据，测试用
//            List<Integer> ids = goodsRepository.findAllGoodsId(shopId);
//            if(!CollectionUtils.isEmpty(ids)){
//                int index = RandomUtils.nextInt(0,ids.size());
//                goods = goodsRepository.findOne(ids.get(index));
//            }
//        }
        Goods goods = goodsRepository.findGoodsByBarcode(barcode, shopId);
//        if(goods == null){
//            //随机返回库中商品数据，测试用
//            List<Integer> ids = goodsRepository.findAllGoodsId(shopId);
//            if(!CollectionUtils.isEmpty(ids)){
//                int index = RandomUtils.nextInt(0,ids.size());
//                goods = goodsRepository.findOne(ids.get(index));
//            }
//            log.info("********* 条码：{}，没有找到对应商品信息，随机返回商品ID:{}",barcode,goods.getId());
//        }
//        return  goodsRepository.findGoodsByBarcode(barcode,shopId);
        return goods;
    }

    /**
     * 批量删除商品，不做物理删除，只修改其状态
     *
     * @param goodsList 待删除商品集合
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean batchDeleteGoods(List<Goods> goodsList) {
        List<Goods> newGoodsList = new ArrayList<Goods>();

        if (!CollectionUtils.isEmpty(goodsList)) {
            for (Goods goods : goodsList) {
                goods.setFlag("-1");
                newGoodsList.add(goods);
            }
        }
        int result = batchUpdate(newGoodsList);
        log.info("**** 批量删除商品数： " + result);
        return result == newGoodsList.size();
    }

    /**
     * 批量插入商品数据
     *
     * @param goodsList
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Goods> batchInsertGoods(List<Goods> goodsList) {
        if (CollectionUtils.isEmpty(goodsList)) {
            return null;
        }
        int result = batchInsert(goodsList);
        if (result == goodsList.size()) {
            log.info("**** 批量插入商品数： " + result);
            return goodsList;
        }
        return null;
    }

    /**
     * 批量更新商品数据
     *
     * @param goodsList
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Goods> batchUpdateGoods(List<Goods> goodsList) {
        if (CollectionUtils.isEmpty(goodsList)) {
            return null;
        }
        int result = batchUpdate(goodsList);
        if (result == goodsList.size()) {
            log.info("**** 批量更新商品数： " + result);
            return goodsList;
        }
        return null;
    }

    /**
     * 批量移除新品举荐商品
     *
     * @param goodsList
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean batchRemoveNewRecommendGoods(List<Goods> goodsList) {
        List<Goods> newGoodsList = new ArrayList<Goods>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            for (Goods goods : goodsList) {
                goods.setNewRecommend("0");
                newGoodsList.add(goods);
            }
        }
        int result = batchUpdate(newGoodsList);
        log.info("**** 批量移除新品举荐商品数： " + result);
        return result == newGoodsList.size();
    }

    /**
     * 查询所有待加入新品举荐列表的商品
     *
     * @param shopId
     * @param goodsIds
     * @return
     */
    public List<Goods> findNewRecommendGoodsByGoodsIdList(Integer shopId, Integer[] goodsIds) {
        return goodsRepository.findAllNewRecommendGoods(shopId, goodsIds);
    }

    /**
     * 批量将商品添加至新品举荐列表
     *
     * @param goodsList
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean batchAddNewRecommendGoods(List<Goods> goodsList) {
        List<Goods> newGoodsList = new ArrayList<Goods>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            for (Goods goods : goodsList) {
                goods.setNewRecommend("1");
                newGoodsList.add(goods);
            }
        }
        int result = batchUpdate(newGoodsList);
        log.info("**** 批量移除新品举荐商品数： " + result);
        return result == newGoodsList.size();
    }

    /**
     * 商品最大ID
     *
     * @return
     */
    public Integer findMaxGoodsId() {
        return goodsRepository.findMaxId();
    }

    /**
     * 商品最小ID
     *
     * @return
     */
    public Integer findMinGoodsId() {
        return goodsRepository.findMinId();
    }

    /**
     * 查询所有商品ID
     *
     * @return
     */
    public List<Integer> findAllGoodsId(Integer shopId) {
        return goodsRepository.findAllGoodsId(shopId);
    }

    /**
     * 根据id范围查询商品列表
     *
     * @param ids
     * @return
     */
    public Page<Goods> findGoodsListByIds(Integer pageNum, Integer pageSize, Integer ids) {
        PageRequest pageRequest = PageUtil.buildPageRequest(pageNum, pageSize);
        return goodsRepository.findGoodsListByIds(ids, pageRequest);
    }

    /**
     * 根据商店ID和商品分类ID查询商品
     *
     * @param catIds
     * @param shopId
     * @return
     */
    public List<Goods> findGoodsByShopAndCategory(Integer[] catIds, Integer[] shopId) {
        return goodsRepository.findGoodsByShopAndCategory(catIds, shopId);
    }

    /**
     * 根据超市ID查询需要添补SID及Location的商品信息
     */
    public List<Goods> findMapGoods(Integer shopId) {
        return goodsRepository.findMapGoods(shopId);
    }

    /**
     * 查询所有商品图片URL
     *
     * @return
     */
    public List<Goods> findAllGoodsImgUrls() {
        return goodsRepository.findAllGoodsImgUrls();
    }

    /**
     * 查询所有商品条形码
     *
     * @param shopId
     */
    public List<String> findBarcodesByShopId(Integer shopId) {
        return goodsRepository.findBarcodesByShopId(shopId);
    }

    public List<Goods> findGoodsInBarcodes(String[] barcodes, Integer shopId) {
        return goodsRepository.findGoodsInBarcodes(shopId, barcodes);
    }

    /**
     * 清除商店历史特价信息
     *
     * @param shopId
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer cleanPromotion(Integer shopId) {
        return goodsRepository.cleanPromotion(shopId, "normal");
    }

    /**
     * 根据条形码查询指定商店的商品信息
     *
     * @param barcode 条形码
     * @param shopId  商店id
     * @return
     */
    public Goods findByBarcodeAndShopId(String barcode, Integer shopId) {
        return goodsRepository.findByBarcodeAndShopId(barcode, shopId);
    }

    /**
     * 特价查询 created by Loring
     * @param shopId
     * @return
     */
    public List<Goods> findByPromotionGoods(Integer shopId,Integer pageNum,Integer pageSize){
        return goodsRepository.findByPromotionGoods(shopId,(pageNum - 1 ) * pageSize,pageSize);
    }


    /**
     * 排序字段
     *
     * @return
     */
    private List<String> sortFieldList() {
        List<String> sortField = new ArrayList<String>();
        sortField.add("sortNo");
        sortField.add("modifiedTime");
        return sortField;
    }

    /**
     * 排序方向
     *
     * @return
     */
    private List<Sort.Direction> sortDirectionList() {
        List<Sort.Direction> sortDirection = new ArrayList<Sort.Direction>();
        sortDirection.add(Sort.Direction.ASC);
        sortDirection.add(Sort.Direction.DESC);
        return sortDirection;
    }


}
