package cn.huace.controller.statis.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by huangdan on 2017/5/29.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackItem {
    String x;
    String y;
    String ts;
    String location;
}
