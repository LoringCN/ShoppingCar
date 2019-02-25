package cn.huace.shop.device.Vo;

import lombok.Data;

import java.util.Date;

@Data
public class DeviceVo {
    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 修改时间
     */
    private Date modifiedTime;
    /**
     * 软件现在版本
     */
    private String currentVersion;
    /**
     * 设备Id
     */
    private String devId;
    /**
     * 软件初始版本
     */
    private String initialVersion;
    /**
     * 设备名称
     */
    private String name;
    /**
     * 商店Id
     */
    private Integer shopId;
    /**
     * 指南针
     */
    private String compass;

}
