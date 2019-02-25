package cn.huace.shop.bluetooth.Vo;

import cn.huace.common.Vo.BaseVo;
import lombok.Data;

/**
 * LED灯 VO
 * created by Loring on 2018-07-5
 */
@Data
public class LambConfigVo extends BaseVo {

    private Integer id;
    /**
     * 商店id
     */
    private Integer shopId;
    /**
     * 商店名称
     */
    private String shopName;
    /**
     * 灯ID的十六进制串
     */
    private String ledId;
    /**
     * 蓝牙MAC地址
     */
    private String mac;
    /**
     * 灯在地图中的X坐标
     */
    private Double x;
    /**
     * 灯在地图中的Y坐标
     */
    private Double y;
    /**
     * 灯光脉宽，十六进制串
     */
    private String pulse;
    /**
     * 蓝牙发射功率Enum值：0: -23db; 1: -4db; 2: 0db; 3: 4db
     */
    private Integer power;
    /**
     * 功率值
     */
    private String powerValue;

    /**
     * base64签名串
     */
    private String sign;

}
