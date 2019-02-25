package cn.huace.goods.repository.solr;

import cn.huace.goods.constant.SearchGoodsDocumentFieldDefine;
import cn.huace.goods.document.SearchGoods;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.request.schema.AnalyzerDefinition;
import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.PartialUpdate;
import org.springframework.data.solr.core.schema.SchemaDefinition;
import org.springframework.data.solr.core.schema.SolrSchemaRequest;
import org.springframework.data.solr.core.schema.SolrSchemaWriter;
import org.springframework.data.solr.server.SolrClientFactory;
import org.springframework.data.solr.server.support.HttpSolrClientFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by yld on 2017/6/26.
 */
@Slf4j
@Repository
public class SearchGoodsRepositoryImpl implements SearchGoodsRepositoryCustom {

    @Resource(name = "goodsSolrTemplate")
    private SolrTemplate solrTemplate;

    @Override
    public Boolean addBatch(List<SearchGoods> searchGoods) {
        UpdateResponse response = solrTemplate.saveBeans(searchGoods);
        //软提交,查询立即可见，但不会马上写入磁盘
        solrTemplate.softCommit();

        int statusVal = response.getStatus();
        log.info("*** Solr添加结果：status = "+ statusVal);

        return statusVal == 0;
    }

    @Override
    public Boolean deleteBatch(Collection<String> ids) {
        UpdateResponse response = solrTemplate.deleteById(ids);
        solrTemplate.softCommit();
        int statusVal = response.getStatus();
        log.info("**** Solr删除结果：status = " +statusVal);
        return  statusVal == 0;
    }

    @Override
    public Boolean update(SearchGoods goods) {

        PartialUpdate update = new PartialUpdate(SearchGoodsDocumentFieldDefine.ID_FIELD_NAME,goods.getId().toString());
        update.add(SearchGoodsDocumentFieldDefine.DESCR_FIELD_NAME,goods.getDescr());

        UpdateResponse updateResponse = solrTemplate.saveBean(update);
        //提交事务
        solrTemplate.softCommit();
        int result = updateResponse.getStatus();
        log.info("**** Solr更新结果：status = "+ result);
        return result == 0;
    }

}
