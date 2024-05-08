package com.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import com.example.algorithm.GeoregeoApi;
import com.example.algorithm.VehicleRoutingProblem;
import com.example.mapper.UserMapper;
import com.example.model.DistanceResult;
import com.example.model.GeocodesResult;
import com.example.model.User;
import com.example.model.UserVo;
import com.example.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Autowired
    private GeoregeoApi georegeoApi;

    @Override
    public void updateLocation() {
        for (User user : userMapper.list()) {
            GeocodesResult geocodesResult = georegeoApi.geocode(user.getAddressDetail());
            if (geocodesResult != null) {
                User record = new User();
                record.setId(user.getId());
                record.setProvince(geocodesResult.getProvince());
                record.setCity(geocodesResult.getCity());
                record.setArea(geocodesResult.getDistrict());
                String[] location = geocodesResult.getLocation().split(",");
                record.setLongitude(new BigDecimal(location[0]));
                record.setLatitude(new BigDecimal(location[1]));
                record.setAddressDetail(geocodesResult.getFormatted_address());
                DistanceResult distanceResult = georegeoApi.defaultDistance(user.getLongitude() + "," + user.getLatitude());
                record.setDistance(new BigDecimal(distanceResult.getDistance()));
                record.setDuration(Long.valueOf(distanceResult.getDuration()));
                userMapper.updateByPrimaryKeySelective(record);
            }
        }
    }

    @Override
    public Map<String, Object> planningRoutes() {
        List<User> userList = userMapper.ownerLocation(10);
        List<UserVo> userVos = BeanUtil.copyToList(userList, UserVo.class);
        int[] demands = NumberUtil.generateRandomNumber(1, 150, 10);
        for (int i = 0; i < userVos.size(); i++) {
            userVos.get(i).setDemands(new BigDecimal(demands[i]));
        }
        return VehicleRoutingProblem.findBestRoute(userVos, georegeoApi);
    }
}
