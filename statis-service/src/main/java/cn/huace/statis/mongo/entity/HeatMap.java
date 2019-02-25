package cn.huace.statis.mongo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
//热力图的pojo类
public class HeatMap implements Serializable {
    private float x;
    private float y;
    private int value;
}