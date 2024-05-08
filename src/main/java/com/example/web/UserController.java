package com.example.web;

import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "UserController", description = "用户管理")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "修改用户位置", description = "修改用户位置信息")
    @PutMapping("/updateLocation")
    public String updateLocation() {
        try {
            log.info("updateLocation");
            userService.updateLocation();
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    @Operation(summary = "规划线路", description = "规划线路")
    @GetMapping("/planningRoutes")
    public String planningRoutes() {
        try {
            return userService.planningRoutes().get("bestRoute").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Operation(summary = "规划线路", description = "规划线路")
    @GetMapping("/planningRoute")
    public  Map<String, Object> planningRoute() {
        log.info("planningRoute");
        return userService.planningRoutes();
    }
}
