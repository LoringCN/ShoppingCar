package cn.huace.shop.map.entity;

import cn.huace.common.entity.BaseEntity;
import cn.huace.shop.shop.entity.Shop;
import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name = "t_navimap")
public class NaviMap extends BaseEntity {

    @Column(name = "map_version")
    private int version;

    @Column(name = "description", length = 512)
    private String desc;

    /*文件下载地址*/
    private String url;

    @Column(length = 32)
    private String md5;

    @Column(name = "use_flag")
    private boolean useFlag;


    @ManyToOne(optional = false)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    public NaviMapResp toResp() {
        NaviMapResp resp = new NaviMapResp();
        resp.setId(getId());
        resp.setVersion(version);
        resp.setUrl(url);
        resp.setMd5(md5);
        return resp;
    }
}
