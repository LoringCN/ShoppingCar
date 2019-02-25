package cn.huace.goods.entity;

import cn.huace.common.entity.BaseEntity;
import cn.huace.shop.shop.entity.Shop;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

/**
 *
 * Created by yld on 2017/7/20.
 */
@Data
@Entity
@Table(name = "goods_category")
public class GoodsCategory extends BaseEntity{
    private static final long serialVersionUID = 5956266832749537143L;

    @Column(name = "cat_name",length = 50)
    private String catName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shop_id",nullable = false)
    private Shop shop;

    /**
     * 删除标记：1 - 正常 ，-1 - 删除
     * */
    @Column(name = "flag")
    private String flag;

    /**
     * 特殊分类标识，默认为正常分类：false
     */
    @Column(name = "s_mark",columnDefinition = "bit(1) default false")
    private Boolean specialMark;

    public GoodsCategory(){}

    public GoodsCategory(Integer id,String catName){
        setId(id);
        this.catName = catName;
    }
    @Override
    public String toString() {
       return ToStringBuilder.reflectionToString(this);
    }
}
