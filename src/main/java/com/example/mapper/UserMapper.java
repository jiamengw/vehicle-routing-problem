package com.example.mapper;

import com.example.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 获取用户列表
     *
     * @return
     */
    List<User> list();

    /**
     *  获取拥有经纬度的用户
     * @param pageSize
     * @return
     */
    List<User> ownerLocation(@Param("pageSize") Integer pageSize);
}