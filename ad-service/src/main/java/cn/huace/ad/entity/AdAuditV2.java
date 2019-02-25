package cn.huace.ad.entity;

import cn.huace.common.entity.BaseEntity;
import cn.huace.sys.entity.SystemUser;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 广告审核最终结果记录表
 * Date:2018/1/23
 */
@Data
@Entity
@Table(name = "ad_audit_v2")
public class AdAuditV2 extends BaseEntity{
    private static final long serialVersionUID = 7447827743540217644L;

    @Column(name = "shop_id")
    private Integer shopId;

    @Column(name = "ad_id")
    private Integer adId;

    @Column(name = "user_id")
    private Integer userId;
    /**
     * 审核状态
     */
    private Byte status;

    private String reason;

    @Transient
    private SystemUser user;
}
