package cn.huace.common.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.io.Serializable;


/**
 *
 * Created by yld on 2017/6/20.
 */
@NoRepositoryBean
public interface BaseSolrRepository<T,ID extends Serializable> extends SolrCrudRepository<T,ID> {

}
