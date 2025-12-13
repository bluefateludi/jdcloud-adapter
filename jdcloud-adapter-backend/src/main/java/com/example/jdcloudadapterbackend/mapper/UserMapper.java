package com.example.jdcloudadapterbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jdcloudadapterbackend.model.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 * 题目1：用户基础表数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
    // MyBatis-Plus提供基础CRUD方法，无需额外定义
}
