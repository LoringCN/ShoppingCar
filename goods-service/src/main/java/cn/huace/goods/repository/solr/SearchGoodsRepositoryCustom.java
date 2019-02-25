package cn.huace.goods.repository.solr;

import cn.huace.goods.document.SearchGoods;

import java.util.Collection;
import java.util.List;

/**
 *
 * Created by yld on 2017/6/26.
 */
public interface SearchGoodsRepositoryCustom {
    /**
     * 批量索引数据
     * @param searchGoods 索引商品数据集合
     * @return 返回索引结果
     */
    Boolean addBatch(List<SearchGoods> searchGoods);

    /**
     * 根据ID更新索引数据
     * @param searchGoods 待更新商品数据
     * @return 返回更新数量
     */
    Boolean update(SearchGoods searchGoods);

    /**
     * 批量删除索引文档
     * @param ids 文档Id
     * @return 删除结果
     */
    Boolean deleteBatch(Collection<String> ids);

}
