package com.example.backend.Mappers;

import com.example.backend.POJO.DO.AIGenerateDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AIGenerateMapper {
    // 1. 插入
    int insert(AIGenerateDO aiGenerateDO);

    // 2. 根据 ID 查询（单条查询）
    AIGenerateDO selectById(@Param("id") String id);

    // 3. 根据垃圾名查询
    AIGenerateDO selectByName(@Param("name") String name);

    // 4. 查询所有
    List<AIGenerateDO> selectAll();

    // 5. 带条件查询（用于分页）
    List<AIGenerateDO> selectWithConditions(@Param("keyword") String keyword, @Param("type") String type, @Param("status") String status);

    // 6. 全字段更新（非空字段才更新，避免覆盖）
    int update(AIGenerateDO aiGenerateDO);

    // 7. 根据 ID 删除（单条删除）
    int deleteById(@Param("id") String id);
}