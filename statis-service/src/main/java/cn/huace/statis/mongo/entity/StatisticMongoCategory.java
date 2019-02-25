package cn.huace.statis.mongo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
//存在mongodb中的data的数据类型
public class StatisticMongoCategory implements Serializable {
    private String name;
    private Long count;
    private String ratio;
}
