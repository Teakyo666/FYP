package com.example.backend.Mappers;

import com.example.backend.POJO.DO.ApplyDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApplyMapper {

    int insert(ApplyDO applyDO);

    int deleteById(@Param("id") String id);

    int update(ApplyDO applyDO);

    ApplyDO selectById(@Param("id") String id);

    List<ApplyDO> selectAll();
    
    List<ApplyDO> selectByCondition(@Param("to_apply") String to_apply, @Param("status") String status);
}