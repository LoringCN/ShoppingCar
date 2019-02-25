package cn.huace.goods.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name="goods_location")
public class GoodsLocation implements Serializable {

    private static final long serialVersionUID = 7484835195932444256L;

    @Id
    private String productId;

    private String shopId;

    private String shelfId;

    private String goodsName;
}
