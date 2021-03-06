package cn.huace.ads.entity;

import cn.huace.common.entity.IntBaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 审核 实体类 created on 2018-05-29
 * @author Loring
 */
@Data
@Entity
@Table(name = "ads_audit_task")
public class AdsAuditTask extends IntBaseEntity {
    private static final long serialVersionUID = 3875576920801558430L;

    /**
     * 商店ID
     *
     */
    @Column(name = "shop_id")
    private Integer shopId;
    /**
     * 广告对象
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "ad_id")
    private Ads ads;
    /**
     * 审核状态
     * -1：初始态 , 0:待审核,1:审核中,2:审核通过,3:下发修改，4-提交上级 ,9:审核不通过。
     */
    @Column(name = "audit_status")
    private Integer auditStatus;

    /**
     *  审核意见
     */
    private String reason;
    /**
     * 审核人
     */
    private String auditor;
    /**
     * 审核时间
     */
    private Date auditTime;


}
