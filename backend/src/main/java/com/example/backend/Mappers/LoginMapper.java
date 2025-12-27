package com.example.backend.Mappers;

import com.example.backend.POJO.DO.LoginDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;


@Mapper
public interface LoginMapper {

    int insert(LoginDO login);

    LoginDO selectById(@Param("id") String id);

    LoginDO selectByUsername(@Param("username") String username);

    List<LoginDO> selectAll();

    List<LoginDO> selectByCondition(@Param("username") String username, @Param("role") String role);

    int update(LoginDO login);

    int deleteById(@Param("id") String id);

    int deleteBatchByIds(@Param("ids") List<String> ids);
}