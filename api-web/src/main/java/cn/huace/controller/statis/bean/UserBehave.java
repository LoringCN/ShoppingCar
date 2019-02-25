package cn.huace.controller.statis.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by huangdan on 2017/5/29.
 */
@Getter
@Setter
public class UserBehave {

       private String type;

       private List<Behave> content;

}
