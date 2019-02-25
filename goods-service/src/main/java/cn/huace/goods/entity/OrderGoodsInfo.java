package cn.huace.goods.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import cn.huace.common.entity.IntBaseEntity;

@Data
@Entity
@Table(name="order_goods_info")
public class OrderGoodsInfo extends IntBaseEntity{
	private static final long serialVersionUID = -6096028693556251171L;
	/**
	 * 订单ID
	 */
	@Column(name = "order_id")
	private String orderId;
	/**
	 * 商品ID
	 */
	@Column(name = "goods_id")
	private Integer goodsId;
	/**
	 * 商品标题
	 */
	@Column(name = "title")
	private String title;
	/**
     * 商品详情图片，
     */
    @Column(name="detail_img_url")
    private String detailImgUrl;
	/**
	 * 商品单价
	 */
    @Column(name="price")
	private Integer price;
	/**
	 * 商品数量
	 */
    @Column(name="num")
	private Integer num;
	/**
	 * 商品总价
	 */
    @Column(name="total_price")
	private Integer totalPrice;
    /**
     * 商品条码
     */
    @Column(name="bar_code")
    private String barCode;
    
}
