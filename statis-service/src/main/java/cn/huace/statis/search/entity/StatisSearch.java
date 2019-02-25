package cn.huace.statis.search.entity;

import cn.huace.common.entity.BaseEntity;
import cn.huace.shop.shop.entity.Shop;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * 商品搜索统计
 * Created by yld on 2017/12/23.
 */
@Data
@Entity
@Table(name = "statis_search")
public class StatisSearch extends BaseEntity{
    private static final long serialVersionUID = -1660972798361898089L;
    //搜索关键字
    @Column(name = "search_key")
    private String searchKey;
    //搜索时间
    @Column(name = "search_time")
    private Date searchTime;
    //商店
    @Column(name = "shop_id")
    private Integer shopId;
    //搜索结果:0 - 没有结果，其他为搜索相关结果
    private Integer result;
    //相关商品ID
    @Column(name = "rel_goods")
    private String relGoods;
    @Transient
    private Shop shop;
    @Transient
    private String category;
}
