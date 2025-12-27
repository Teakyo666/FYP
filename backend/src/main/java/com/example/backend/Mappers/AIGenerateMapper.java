package com.example.backend.Mappers;

import com.example.backend.POJO.DO.AIGenerateDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AIGenerateMapper {

    int insert(AIGenerateDO aiGenerateDO);

    AIGenerateDO selectById(@Param("id") String id);

    AIGenerateDO selectByName(@Param("name") String name);

    List<AIGenerateDO> selectAll();

    List<AIGenerateDO> selectWithConditions(@Param("keyword") String keyword, @Param("type") String type, @Param("status") String status);

    int update(AIGenerateDO aiGenerateDO);

    int deleteById(@Param("id") String id);
}