package com.example;

import com.example.algorithm.GeoregeoApi;
import com.example.model.GeocodesResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VehicleRoutingProblemApplicationTests {

    @Autowired
    private GeoregeoApi georegeoApi;

    @Test
    void contextLoads() {
        GeocodesResult geocodesResult = georegeoApi.geocode("贵州省贵阳市白云区杜拉营黔货云仓2楼");
        System.out.println(geocodesResult);
//        String str = "{\"formatted_address\":\"贵州省贵阳市观山湖区天一国际\",\"country\":\"中国\",\"province\":\"贵州省\",\"citycode\":\"0851\",\"city\":\"贵阳市\",\"district\":\"观山湖区\",\"township\":[],\"neighborhood\":{\"name\":[],\"type\":[]},\"building\":{\"name\":[],\"type\":[]},\"adcode\":\"520115\",\"street\":[],\"number\":[],\"location\":\"106.642827,26.617702\",\"level\":\"住宅区\"}";
//        String str = "{\"status\":\"1\",\"info\":\"OK\",\"infocode\":\"10000\",\"count\":\"1\",\"geocodes\":[{\"formatted_address\":\"贵州省铜仁市\",\"country\":\"中国\",\"province\":\"贵州省\",\"citycode\":\"0856\",\"city\":\"铜仁市\",\"district\":[],\"township\":[],\"neighborhood\":{\"name\":[],\"type\":[]},\"building\":{\"name\":[],\"type\":[]},\"adcode\":\"520600\",\"street\":[],\"number\":[],\"location\":\"109.189528,27.731555\",\"level\":\"市\"}]}";
//        Gson gson = new GsonBuilder().create();
//        JsonObject jsonObject = gson.fromJson(str, JsonObject.class);
//        String st = null;
//        for (JsonElement geocodes : jsonObject.getAsJsonArray("geocodes")) {
//            if (geocodes.getAsJsonObject().get("province").getAsString().equals("贵州省")){
//                st = geocodes.toString();
//            }
//        }
//        System.out.println(gson.fromJson(st, GeocodesResult.class));
    }
}
