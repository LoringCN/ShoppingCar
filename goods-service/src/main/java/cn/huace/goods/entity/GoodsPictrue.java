package cn.huace.goods.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="goods_picture")
public class GoodsPictrue {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String barcode ;

    private String inShopCode;

    private String url;

}
