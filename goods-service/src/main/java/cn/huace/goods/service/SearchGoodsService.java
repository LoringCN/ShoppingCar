package cn.huace.goods.service;

import cn.huace.goods.document.SearchGoods;
import cn.huace.goods.entity.Goods;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;

/**
 *  商品Solr搜索接口
 * Created by yld on 2017/6/24.
 */
public interface SearchGoodsService {

    /**
     * 保存或更新索引文档数据
     * @param goods 商品数据
     * @return
     */
    SearchGoods save(Goods goods);

    /**
     * 删除单个索引文档
     * @param id 文档Id
     * @return
     */
    Boolean delete(String id);

    /**
     * 批量删除索引文档
     * @param ids 文档Id集合
     * @return
     */
    Boolean deleteBatch(Collection<String> ids);

    /**
     * 批量索引文档
     * @param goodsList 商品数据集合
     * @return
     */
    Boolean addBatch(List<Goods> goodsList);

    /**
     *  对<em><b>所有超市</b></em>商品进行全文搜索，任何含有关键字的商品信息都讲被返回
     * @param keyword 搜索关键字
     * @param pageNo 分页数
     * @param pageSize 每页显示数
     * @return
     */
    Page<SearchGoods> searchAllGoodsIgnoreDifferentShop(String keyword,Integer pageNo,Integer pageSize);

    /**
     * 对<em><b>指定超市</b></em>商品进行全文搜索，<br/>
     * document中任何field含有关键字信息的商品信息都将被返回
     */
    Page<SearchGoods> searchGoodsByShopId(Integer shopId,String keyword,Integer pageNo,Integer pageSize);

    /**
     * 对<em><b>指定超市</b></em>商品根据<em>商品名</em>进行模糊搜索
     */
    Page<SearchGoods> searchGoodsByShopIdAndName(Integer shopId,String name,Integer pageNo,Integer pageSize);


    /**
     * 根据超市ID查询该超市所有商品ID
     * @param shopId
     * @return
     */
    List<Integer> searchAllGoodsIdByShopId(Integer shopId);

    /**
     * 根据商品ID查询商品信息
     * @param ids
     * @return
     */
    Page<SearchGoods> searchGoodsByIds(Collection<String> ids,Integer pageNo,Integer pageSize);


}
