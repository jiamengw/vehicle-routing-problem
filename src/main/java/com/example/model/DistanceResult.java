package com.example.model;

import lombok.Data;

@Data
public class DistanceResult {
    /**
     * 起点坐标，起点坐标序列号（从１开始）
     */
    private String origin_id;

    /**
     * 终点坐标，终点坐标序列号（从１开始）
     */
    private String dest_id;

    /**
     * 路径距离，单位：米
     */
    private String distance;

    /**
     * 预计行驶时间，单位：秒
     */
    private String duration;
}
