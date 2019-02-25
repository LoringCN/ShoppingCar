package cn.huace.controller.ad;

import cn.huace.ad.bean.AdBean;
import cn.huace.ad.entity.Ad;
import cn.huace.ad.entity.AdGoods;
import cn.huace.ad.service.AdGoodsService;
import cn.huace.ad.service.AdService;
import cn.huace.ad.util.AdCodeConstants;
import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.BaseFrontController;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.enums.GoodsType;
import cn.huace.goods.service.GoodsService;
import cn.huace.shop.map.entity.NaviMap;
import cn.huace.shop.map.entity.NaviMapResp;
import cn.huace.shop.map.service.NaviMapService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zhouyanbin
 * @version 1.0
 * @date 2017年5月21日 下午6:54:11
 */

@Slf4j
@RestController
@Api(value = "/ad", description = "广告管理")
@RequestMapping(value = "/ad")

public class AdController extends BaseFrontController {
    @Autowired
    private AdService adService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private AdGoodsService adGoodsService;
    @Autowired
    private NaviMapService naviMapService;

    @ApiOperation(value = "app根据shopId查询广告接口", notes = "app根据shopId查询广告接口")
    @RequestMapping(value = "homelist")
    public HttpResult homelist(HttpServletRequest request) {
        Integer shopId = findShopId(request);
        if (shopId == null) {
            return HttpResult.createFAIL("非法接口访问！");
        }

        List<Ad> adList = adService.findAdListByShopId(shopId, AdCodeConstants.VALID);
        if (adList == null || adList.isEmpty()) {
            return HttpResult.createFAIL("超市ID为！" + shopId + ",无广告信息");
        }
        adList.sort(new Comparator<Ad>() {
            @Override
            public int compare(Ad o1, Ad o2) {
                if (o1.getId() == 77) {
                    return -1;
                }
                return 0;
            }
        });
        Set<AdBean> adSet = new HashSet<AdBean>();
        for (Ad ad : adList) {
            //主题广告没有关联商品信息
            if ("4".equals(ad.getType())) {
                continue;
//				adBean.setProductId(null);
//				adBean.setNormalPrice(null);
//				adBean.setPromotionPrice(null);
//				adBean.setTitle(null);
            }
            AdBean adBean = new AdBean();
            adBean.setType(ad.getType());
            adBean.setId(ad.getId());
            adBean.setUrl(ad.getPath());
            adBean.setMd5(ad.getMd5());
            List<AdGoods> adGoodsList = adGoodsService.findAdGoodsByAdIdAndShopId(ad.getId(), ad.getShopId());
            if (adGoodsList == null || adGoodsList.isEmpty()) {
                adBean.setProductId(null);
                adBean.setNormalPrice(null);
                adBean.setPromotionPrice(null);
                adBean.setTitle(null);
                adBean.setSid(null);
            } else {
                Goods goods = goodsService.findOne(adGoodsList.get(0).getGoodsId());
                adBean.setProductId(String.valueOf(goods.getId()));
                adBean.setNormalPrice(goods.getNormalPrice());
                Integer promotionPrice = goods.getPromotionPrice();
                boolean isPromotionGoods = goods.getType().equalsIgnoreCase(GoodsType.PROMOTION.getValue());
                if (promotionPrice != null && isPromotionGoods) {
                    adBean.setPromotionPrice(goods.getPromotionPrice());
                }
                adBean.setTitle(goods.getTitle());
                adBean.setSid(goods.getSid());
            }

            adSet.add(adBean);
        }

        NaviMap naviMap = naviMapService.findNewestNavimapByShop(shopId);
        Item ret = new Item();
        ret.setAd(adSet);
        ret.setNavimap(naviMap.toResp());
        return HttpResult.createSuccess("查询成功", ret);
    }

    @Data
    public class Item {
        private Set<AdBean> ad;
        private NaviMapResp navimap;
    }


}
