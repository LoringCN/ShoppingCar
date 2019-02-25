package cn.huace.controller.bean;


import cn.huace.sys.entity.SystemMenu;
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

    public static TreeBean passSystemMenu(SystemMenu menu){
        TreeBean treeBean=new TreeBean();
        treeBean.setId(menu.getId());
        treeBean.setText(menu.getName());
        return treeBean;
    }
}
