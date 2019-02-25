package cn.huace.goods.entity;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * Created by yld on 2017/7/21.
 */
@Data
public class CategoryOV implements Serializable{
    private static final long serialVersionUID = 23376636552294755L;

    private Integer id;
    private String name;

}
