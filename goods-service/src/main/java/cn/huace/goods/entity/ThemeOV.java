package cn.huace.goods.entity;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * Created by yld on 2017/7/28.
 */
@Data
public class ThemeOV implements Serializable{
    private static final long serialVersionUID = -5386917503834418089L;
    private Integer id;
    private String coverImg;
}
