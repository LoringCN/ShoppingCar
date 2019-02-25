package cn.huace.goods.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name="goods_class_loc")
public class GoodsClassLoc implements Serializable {

    private static final long serialVersionUID = 7484835195932444256L;

    @Id
    private Integer id;

    private Integer classificationId;

    private String classificationName;

    private String sid;

    private Integer shopId;

    private Date modifiedTime;
}
