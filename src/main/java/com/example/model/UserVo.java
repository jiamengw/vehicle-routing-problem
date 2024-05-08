package com.example.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserVo {
    private Integer id;

    private String name;

    private String province;

    private String city;

    private String area;

    private String addressDetail;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String lineId;

    private Integer lineSort;

    private String deliveryTime;

    private BigDecimal demands;

    /**
     * 路径距离，单位：米
     */
    private BigDecimal distance;

    /**
     * 预计行驶时间，单位：秒
     */
    private Long duration;
}