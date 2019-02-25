package cn.huace.goods.entity;

import cn.huace.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.*;

/**
 * 用于记录同步商品原始属性，与商品表一一对应
 * Created by yld on 2017/8/24.
 */
@Data
@Entity
@Table(name = "sync_goods")
public class SyncGoods extends BaseEntity{
    private static final long serialVersionUID = 7424026008827091814L;

    /**
     * 我方商品ID
     */
    @Column(name = "gid")
    private Integer gId;

    /**
     * 合作超市方商品ID
     * 格式：超市名每个字首字母大写_ID
     * 如：汇隆百货超市 12
     *      HLBH_12
     */
    @Column(name = "oid")
    private String originId;

    /**
     * 用于判断商品图片是否更换
     */
    @Column(name = "o_img_url")
    private String originImgUrl;

    /**
     * 删除状态标志位
     */
    @Column(name = "flag")
    private String flag;
}
