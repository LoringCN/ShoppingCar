package cn.huace.goods.service.impl;

import cn.huace.common.utils.jpa.PageUtil;
import cn.huace.goods.constant.SearchGoodsDocumentFieldDefine;
import cn.huace.goods.document.SearchGoods;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.repository.solr.SearchGoodsRepository;
import cn.huace.goods.service.SearchGoodsService;
import cn.huace.goods.util.GoodsConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yld on 2017/6/23.
 */
@Slf4j
@Service
public class SearchGoodsServiceImpl implements SearchGoodsService{

    @Autowired
    private SearchGoodsRepository searchGoodsRepository;

    public Page<SearchGoods> searchAllGoodsIgnoreDifferentShop(String keyword, Integer pageNo, Integer pageSize) {
        PageRequest pageRequest = PageUtil.buildPageRequest(pageNo,pageSize);
        return searchGoodsRepository.findAllGoodsIgnoreDifferentShop(keyword,pageRequest);
    }

    public Page<SearchGoods> searchGoodsByShopId(Integer shopId, String keyword, Integer pageNo, Integer pageSize) {
        PageRequest pageRequest = PageUtil.buildPageRequest(pageNo,pageSize);
        return searchGoodsRepository.findGoodsByShopId(shopId,keyword,pageRequest);
    }

    public Page<SearchGoods> searchGoodsByShopIdAndName(Integer shopId, String name, Integer pageNo, Integer pageSize) {
        PageRequest pageRequest = PageUtil.buildPageRequest(pageNo,pageSize);
        return searchGoodsRepository.findGoodsByShopIdAndName(shopId,name,pageRequest);
    }

    public List<Integer> searchAllGoodsIdByShopId(Integer shopId) {
        return searchGoodsRepository.findAllGoodsIdByShopId(shopId);
    }

    public Page<SearchGoods> searchGoodsByIds(Collection<String> ids,Integer pageNo,Integer pageSize) {
        PageRequest pageRequest = PageUtil.buildPageRequest(pageNo,pageSize);
        return searchGoodsRepository.findGoodsByIdIn(ids,pageRequest);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public SearchGoods save(Goods goods) {
        SearchGoods searchGoods = GoodsConverter.goodsConvertToSearchGoods(goods);
        return searchGoodsRepository.save(searchGoods);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean delete(String id) {
        if(StringUtils.isEmpty(id)){
            log.info("*** 删除索引数据，ID不能不为空");
            return false;
        }
        searchGoodsRepository.delete(id);
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean deleteBatch(Collection<String> ids) {
        return searchGoodsRepository.deleteBatch(ids);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean addBatch(List<Goods> goodsList) {
        List<SearchGoods> searchGoodsList = GoodsConverter.goodsListConvertToSearchGoodsList(goodsList);
        return searchGoodsRepository.addBatch(searchGoodsList);
    }

    /**
     * 构建分页、排序请求
     */
//    private PageRequest buildPageRequest(Integer pageNo, Integer pageSize){
//        return PageUtil.buildPageRequest(pageNo,pageSize,sortDirectionList(),sortFieldList());
//    }
    /**
     * 排序字段
     * @return
     */
    private List<String> sortFieldList(){
        List<String> sortField = new ArrayList<String>();
        sortField.add(SearchGoodsDocumentFieldDefine.SORTNO_FIELD_NAME);
        sortField.add(SearchGoodsDocumentFieldDefine.MODIFIED_TIME_FIELD_NAME);
        return sortField;
    }

    /**
     * 排序方向
     * @return
     */
    private List<Sort.Direction> sortDirectionList(){
        List<Sort.Direction> sortDirection = new ArrayList<Sort.Direction>();
        sortDirection.add(Sort.Direction.ASC);
        sortDirection.add(Sort.Direction.DESC);
        return sortDirection;
    }

}
