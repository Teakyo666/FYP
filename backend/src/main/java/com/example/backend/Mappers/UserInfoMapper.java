package com.example.backend.Mappers;


import com.example.backend.POJO.DO.UserInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserInfoMapper {
    // 1. 新增用户信息
    int insert(UserInfoDO userInfoDO);

    // 2. 根据 ID 查询（单条查询）
    UserInfoDO selectById(@Param("id") String id);

    // 3. 根据用户id查询
    UserInfoDO selectByUserId(@Param("user_id") String userId);

    // 4. 查询所有
    List<UserInfoDO> selectAll();

    // 5. 条件查询（支持姓名模糊查询）
    List<UserInfoDO> selectByCondition(@Param("name") String name);

    // 6. 全字段更新（非空字段才更新，避免覆盖）
    int update(UserInfoDO userInfoDO);

    // 7. 根据 ID 删除（单条删除）
    int deleteById(@Param("id") String id);

    // 8. 批量删除（可选，按 ID 列表删除）
    int deleteBatchByIds(@Param("ids") List<String> ids);
}

