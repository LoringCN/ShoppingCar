package cn.huace.common.entity;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import java.io.Serializable;
import java.util.Date;

/**
 * Solr Document 基类
 * Created by yld on 2017/6/23.
 */
@Data
public class BaseSearchDocument implements Serializable{

    @Id
//    @Indexed
    @Field
    private String id;

//    @Indexed
    @Field
    private Date createdTime;

//    @Indexed
    @Field
    private Date modifiedTime;

}
