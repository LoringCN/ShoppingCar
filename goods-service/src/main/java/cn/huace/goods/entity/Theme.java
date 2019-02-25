package cn.huace.goods.entity;

import cn.huace.common.entity.BaseEntity;
import cn.huace.shop.shop.entity.Shop;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

/**
 * 主题，与商品信息关联
 * Created by yld on 2017/7/24.
 */
@Data
@Entity
@Table(name = "theme")
public class Theme extends BaseEntity{
    private static final long serialVersionUID = 7794575721748133652L;

    /**
     * 主题名字
     */
    @Column(name = "name")
    private String name;

    /**
     * 主题封面图
     */
    @Column(name = "cover_img")
    private String coverImg;

    /**
     * 主题tag，保存商品信息
     */
    @Column(name = "tag")
    private String tag;

    @ManyToOne(optional = false)
    @JoinColumn(name ="shop_id")
    private Shop shop;

    @ManyToOne(optional = false)
    @JoinColumn(name ="type")
    private ThemeType type;

    /**
     * 删除标志位：-1 -- 删除，1 -- 正常
     */
    @Column(name = "flag")
    private String flag;

    /**
     * 主题备注信息
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 是否上架主题（-1 -- 下架，1 -- 上架 ）
     */
    @Column(name = "active",length = 2)
    private String active;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
