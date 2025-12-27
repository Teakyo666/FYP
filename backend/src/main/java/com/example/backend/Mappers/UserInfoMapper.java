package com.example.backend.Mappers;


import com.example.backend.POJO.DO.UserInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserInfoMapper {

    int insert(UserInfoDO userInfoDO);

    UserInfoDO selectById(@Param("id") String id);

    UserInfoDO selectByUserId(@Param("user_id") String userId);

    List<UserInfoDO> selectAll();

    List<UserInfoDO> selectByCondition(@Param("name") String name);

    int update(UserInfoDO userInfoDO);

    int deleteById(@Param("id") String id);

    int deleteBatchByIds(@Param("ids") List<String> ids);
}

