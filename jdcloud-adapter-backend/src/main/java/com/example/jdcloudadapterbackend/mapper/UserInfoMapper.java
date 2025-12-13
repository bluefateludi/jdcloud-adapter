package com.example.jdcloudadapterbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jdcloudadapterbackend.model.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息Mapper
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserEntity> {
}
