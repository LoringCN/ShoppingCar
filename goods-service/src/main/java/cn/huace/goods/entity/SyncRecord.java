package cn.huace.goods.entity;

import cn.huace.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 商品数据同步记录表
 * Created by yld on 2017/8/21.
 */
@Data
@Entity
@Table(name = "sync_record")
public class SyncRecord extends BaseEntity{
    /*
      同步商品类型：
        add ----- 新增
        update -- 更新
        del ----- 删除
     */
    @Column(name = "type",length = 10)
    private String type;

    /**
     * 记录每次同步时间
     */
    @Column(name = "sync_time")
    private Date syncTime;

    @Column(name = "sync_statue")
    private Integer syncState;

    @Column(name = "descr",length = 500)
    private String descr;

    //每次同步取回商品数量
    @Column(name = "sync_num")
    private Integer syncNum;

    //记录同步的是哪个超市
    @Column(name = "shop_name",length = 20)
    private String shopName;

    //记录同步合作方原始商品id集合
    @Column(name = "oids")
    private String originIds;

}
