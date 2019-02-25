package cn.huace.ads.Vo;

import cn.huace.common.Vo.BaseVo;
import lombok.Data;

import java.util.Date;

/**
 * 广告VO created on 2018-05-25
 * @author Loring
 */
@Data
public class AdsVo extends BaseVo {
    /**
     * ID
     */
    private Integer id;

    /**
     * 商店ID
     */
    private Integer shopId;
    /**
     * 商店名称
     */
    private String shopName;
    /**
     * 广告名称
     */
    private String name;
    /**
     * 广告类型：
     * 1：开机视频广告
     * 2：首页轮播广告
     * 3：特价封面广告
     * 4：新品轮播广告
     * 5：LBS广告
     * 6：搜索条底部广告
     * 7：搜索无结果广告
     */
    private Integer type;
    /**
     * 广告类型名称
     */
    private String typeName;
    /**
     * 上架标志：
     *  1：上架；0：下架
     */
    private Integer isShelf;
    /**
     * 审核状态：
     *  -1：初始态 , 0:待审核,1:审核中,2:审核通过,3:下发修改，4-提交上级 ,9:审核不通过。
     */
    private Integer auditStatus;
    /**
     * 审核状态名称
     */
    private String auditName;
    /**
     * 广告关联关系
     */
    private Integer relationCode;
    /**
     * 关联关系名称
     */
    private String  relationName;
    /**
     * 广告主
     */
    private Integer ownerCode;
    /**
     * 广告主名称
     */
    private String ownerName;
    /**
     * 关联关系的值
     * 无关联 --> null
     * 关联商品 --> goodsId或货架IDs(定位广告使用)
     * 关联超链接 --> 完整url
     * 关联商品和货架 --> goodsId,sid,sid...
     */
    private String relationExtra;
    /**
     * 是否被选中为线上广告
     */
    private Boolean isVoted;
    /**
     * 广告资源在OSS上相对路径
     */
    private String url;
    /**
     * 广告资源文件MD5值
     */
    private String md5;
    /**
     * 广告投放范围
     * 默认全部投放：-1; 指定设备：1
     */
    private Integer deliverScope;
    /**
     * 广告标识：
     * true -- 默认广告
     * false -- 正式广告
     */
    private Boolean isDefalut;
    /**
     * 广告开始生效时间戳
     */
    private Date effectTime;
    /**
     * 广告结束时间戳
     */
    private Date expiryTime;
    /**
     * 广告描述信息
     */
    private String description;
    /**
     *  广告价格
     */
    private Integer price;
    /**
     *  审核拒绝原因
     */
    private String reason;
    /**
     * 广告剩余有效天数
     */
    private Integer days;
    /**
     *  投放广告位(轮播广告有效，其他为0)
     */
    private Integer rank;

    private Date onlineTime;

    private String goodsName;

}
