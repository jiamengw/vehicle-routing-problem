package com.example.algorithm;

import com.example.model.DistanceResult;
import com.example.model.GeocodesResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
public class GeoregeoApi {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY = "c2a172e4dd2f5e9089e2ff2dbf663a59";
    //仓库坐标
    private static final String ORIGIN = "106.623069,26.677932";

    public GeocodesResult geocode(String address) {
        String url = "https://restapi.amap.com/v3/geocode/geo?";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("key", KEY);
        params.add("address", address);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.queryParams(params).build().toUri();
        String result = restTemplate.getForObject(uri, String.class);
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(result, JsonObject.class);
        log.info("geocode result:{}", jsonObject.toString());
        if (jsonObject.get("status").getAsInt() == 1 && jsonObject.get("count").getAsInt() >= 1) {
            String st = null;
            for (JsonElement geocodes : jsonObject.getAsJsonArray("geocodes")) {
                if (geocodes.getAsJsonObject().get("province").getAsString().equals("贵州省")) {
                    st = geocodes.toString();
                    break;
                }
            }
            if (st != null) {
                return gson.fromJson(st, GeocodesResult.class);
            }
        }
        return null;
    }

    public DistanceResult defaultDistance(String destination) {
        return distance(ORIGIN, destination);
    }

    /**
     * 计算两个地点的距离
     *
     * @param origins
     * @param destination
     * @return
     */
    public DistanceResult distance(String origins, String destination) {
        String key = origins + "_" + destination;
        Gson gson = new GsonBuilder().create();
        String s = redisTemplate.opsForValue().get(key);
        if (s != null) {
            log.info("distance result from redis:{}", s);
            return gson.fromJson(s, DistanceResult.class);
        }
        String url = "https://restapi.amap.com/v3/distance";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("key", KEY);
        params.add("origins", origins);
        params.add("destination", destination);
        params.add("type", "1");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.queryParams(params).build().toUri();
        String result = restTemplate.getForObject(uri, String.class);
        JsonObject jsonObject = gson.fromJson(result, JsonObject.class);
        log.info("distance result:{}", jsonObject.toString());
        if (jsonObject.get("status").getAsInt() == 1 && jsonObject.get("count").getAsInt() >= 1) {
            String st = jsonObject.getAsJsonArray("results").get(0).toString();
            redisTemplate.opsForValue().set(key, st);
            return gson.fromJson(st, DistanceResult.class);
        }
        return null;
    }
}
