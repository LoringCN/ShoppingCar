package cn.huace.ad.entity;

import cn.huace.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * Date:2018/2/28
 */
@Data
@Entity
@Table(name = "ad_group_v2")
public class AdGroupV2 extends BaseEntity{
    private static final long serialVersionUID = -1238064207072152963L;

    @Column(name = "name",length = 50)
    private String adGroupName;

    private String remark;
}
