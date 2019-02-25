package cn.huace.controller.statis.bean;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * Created by huangdan on 2017/5/29.
 */
@Getter
@Setter
public class Track {
    private String date;
    private List<TrackItem> track;
}
