package cn.huace.controller.bean;

import lombok.Data;

/**
 * Created by huangdan on 2017/7/25.
 */
@Data
public class UbType {

    Integer type;

    String name;

    public UbType(Integer type,String name){
        this.type=type;
        this.name=name;
    }
}
