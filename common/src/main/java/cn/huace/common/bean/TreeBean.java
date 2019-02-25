package cn.huace.common.bean;
import lombok.Data;

import java.util.List;

/**
 * Created by huangdan on 2016/12/29.
 */
@Data
public class TreeBean {
    Integer id;
    String text;
    String state="open";
    boolean checked=false;
    List<TreeBean> children;

}
