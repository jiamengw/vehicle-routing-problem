package com.example.model;

import lombok.Data;

@Data
public class GeocodesResult {
    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市名
     */
    private String city;

    /**
     * 城市编码
     */
    private String citycode;

    /**
     * 地址所在的区
     */
    private String district;

    /**
     * 详细地址
     */
    private String formatted_address;

    /**
     * location
     * 经度，纬度
     */
    private String location;

    /**
     * 匹配级别
     */
    private String level;
}
