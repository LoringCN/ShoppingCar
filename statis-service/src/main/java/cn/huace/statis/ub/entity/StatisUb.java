package cn.huace.statis.ub.entity;


import cn.huace.common.entity.BaseEntity;
import cn.huace.shop.shop.entity.Shop;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by huangdan on 2017/5/2.
 */
@Data
@Entity
@Table(name = "statis_ub")
public class StatisUb extends BaseEntity {

    @Column(name = "other_id")
    private Integer otherId;

    @Column(name = "other_name")
    private String otherName;

    @Column(name = "shop_id")
    private Integer shopId;

    @Column(name = "type")
    private Integer type;

    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "statis_date")
    private Date statisDate;

    @Column(name = "total_count")
    private long totalCount;

    @Transient
    private Shop shop;
}
