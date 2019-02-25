package cn.huace.goods.repository.solr;

import cn.huace.common.repository.BaseSolrRepository;
import cn.huace.goods.document.SearchGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Boost;
import org.springframework.data.solr.repository.Query;
import java.util.Collection;
import java.util.List;

/**
 * Created by yld on 2017/6/23.
 */
public interface SearchGoodsRepository extends SearchGoodsRepositoryCustom,BaseSolrRepository<SearchGoods,String>{

    @Query("?0")
    Page<SearchGoods> findAllGoodsIgnoreDifferentShop(String keyword, Pageable pageable);
    
    /**
     * 此写法是在solr的solrconfig添加elevate配置，进行人工干预，关键字记录置顶操作
     * @Query(requestHandler ="/elevate",value = "?1",filters={"shopId:?0"})
     * 
     * 此写法是错误的
     * @Query("shopId:?0 AND title:*?1*") 在提交solr查询是 http请求中q参数值为q=shopId:?0 AND title:*?1*
     */
    @Query(value = "title:?1",filters={"shopId:?0"})
    Page<SearchGoods> findGoodsByShopId(Integer shopId,  String keyword, Pageable pageable);
    
    @Query(value = "title:?1",filters={"shopId:?0"})
    Page<SearchGoods> findGoodsByShopIdAndName(Integer shopId,String title,Pageable pageable);

    @Query("shopId:?0")
    List<Integer> findAllGoodsIdByShopId(Integer shopId);

    Page<SearchGoods> findGoodsByIdIn(Collection<String> ids,Pageable pageable);


}
