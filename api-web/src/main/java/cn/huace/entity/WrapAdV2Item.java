package cn.huace.entity;

import cn.huace.shop.map.entity.NaviMapResp;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by yld on 2018/3/7.
 * Date:2018/3/7
 */
@Data
public class WrapAdV2Item implements Serializable{
    private static final long serialVersionUID = 5908991258699671957L;
    private List<AdV2OV> ad;
    private NaviMapResp navimap;

    public WrapAdV2Item(){}
    public WrapAdV2Item(List<AdV2OV> ad,NaviMapResp navimap){
        this.ad = ad;
        this.navimap = navimap;
    }
}
