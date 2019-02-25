package cn.huace.shop.app.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * app vo
 * created by Loring on 2018-06-21
 */
@Data
public class AppVo {

    private Integer id;

    private Date createdTime;

    private String desc;

    private String md5;

    private String name;

    private String packageName;

    private String url;

    private Boolean useFlag;

    private Integer versionCode;

    private Integer shopId;

    private AppListVo appListVo;

    private Integer appListVoId;

    private Integer deliverScope;

}
