package com.example.backend.Mappers;

import com.example.backend.POJO.DO.GarbageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GarbageMapper {
    int insert(GarbageDO garbageDO);

    GarbageDO selectById(@Param("id") String id);

    GarbageDO selectByName(@Param("name") String name);

    List<GarbageDO> selectAll();

    List<GarbageDO> selectWithConditions(@Param("keyword") String keyword, @Param("type") String type);

    int update(GarbageDO garbageDO);

    int deleteById(@Param("id") String id);
}
