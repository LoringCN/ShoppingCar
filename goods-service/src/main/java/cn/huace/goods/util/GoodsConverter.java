package cn.huace.goods.util;

import cn.huace.goods.document.SearchGoods;
import cn.huace.goods.entity.Goods;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *  实体Goods和SearchGoods之间相互转换
 * Created by yld on 2017/6/26.
 */
@Slf4j
public class GoodsConverter {
    /**
     *  将Goods转换成SearchGoods
     * @param goods
     * @return
     */
    public static SearchGoods goodsConvertToSearchGoods(Goods goods){
        if(goods == null){
            return null;
        }
        if(goods.getId() == null){
            log.error("Goods Convert To SearchGoods Fail: Goods Id Can Not Null !");
            return null;
        }
        SearchGoods searchGoods = new SearchGoods();
        copyProperties(goods,searchGoods);
        return searchGoods;
    }

    /**
     * 将Goods集合转换成SearchGoods集合
     * @param goodsList
     * @return
     */
    public static List<SearchGoods> goodsListConvertToSearchGoodsList(List<Goods> goodsList){
        if(CollectionUtils.isEmpty(goodsList)){
            return null;
        }
        List<SearchGoods> searchGoodsList = new ArrayList<SearchGoods>();
        for(Goods goods:goodsList){
            searchGoodsList.add(goodsConvertToSearchGoods(goods));
        }
        return searchGoodsList;
    }

    private static void copyProperties(Goods goods,SearchGoods searchGoods){
        searchGoods.setId(goods.getId().toString());
        searchGoods.setShopId(goods.getShop().getId());
        searchGoods.setBarcode(goods.getBarcode());
        searchGoods.setCoverImgUrl(goods.getCoverImgUrl());
        searchGoods.setDetailImgUrl(goods.getDetailImgUrl());
        searchGoods.setNormalPrice(goods.getNormalPrice());
        searchGoods.setPromotionPrice(goods.getPromotionPrice());
        searchGoods.setSid(goods.getSid());
        searchGoods.setLocation(goods.getLocation());
        searchGoods.setDescr(goods.getDescr());
        searchGoods.setSortNo(goods.getSortNo());
        searchGoods.setTitle(goods.getTitle());
        searchGoods.setType(goods.getType());
        searchGoods.setCreatedTime(goods.getCreatedTime());
        searchGoods.setModifiedTime(goods.getModifiedTime());
        searchGoods.setRemark(goods.getRemark());
        searchGoods.setCatId(goods.getCategory().getId());//商品分类
        searchGoods.setNewRecommend(goods.getNewRecommend());//新品举荐
        searchGoods.setSeoTag(goods.getSeoTag());
    }
}
