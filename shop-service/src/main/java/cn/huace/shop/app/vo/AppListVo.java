package cn.huace.shop.app.vo;

import cn.huace.common.Vo.BaseVo;
import lombok.Data;

/**
 * created by Loring on 2018-07-24
 */
@Data
public class AppListVo extends BaseVo {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 商店Id
     */
    private Integer shopId;
    /**
     * app大类名称
     */
    private String name;
    /**
     * app包名
     */
    private String packageName;
}
