package cn.huace.shop.device.Vo;

import cn.huace.common.Vo.BaseVo;
import cn.huace.shop.app.vo.AppListVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 设备分组VO
 * created by Loring on 2018-06-22
 */
@Data
public class DeviceGroupVo extends  BaseVo{
    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 商店ID
     */
    private Integer shopId;
    /**
     * 名称
     */
    private String name;
    /**
     * 大类
     */
    private AppListVo appListVo;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 设备百分比
     */
    private BigDecimal percent;

    private Integer appListVoId;
}
