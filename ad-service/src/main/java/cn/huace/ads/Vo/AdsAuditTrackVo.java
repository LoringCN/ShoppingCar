package cn.huace.ads.Vo;

import cn.huace.common.Vo.BaseVo;
import lombok.Data;

/**
 * 广告审核Vo created on 2018-05-29
 * @author Loring
 */
@Data
public class AdsAuditTrackVo extends BaseVo {
    /**
     * ID
     */
    private Integer id;
    /**
     * 商店ID
     */
    private Integer shopId;
    /**
     * 商店name
     */
    private String shopName;
    /**
     * 广告vo
     */
    private AdsVo adsVo;

//    /**
//     * 广告Id
//     */
//    private Integer adId;
//    /**
//     * 广告名称
//     */
//    private String  adName;
    /**
     * 版本号
     */
    private Integer versionNo;
    /**
     * 审核状态
     */
    private Integer auditStatus;
    /**
     * 审核意见
     */
    private String reason;
    /**
     * 审核人
     */
    private String auditor;
    /**
     * 审核时间
     */
    private String auditTime;

}
