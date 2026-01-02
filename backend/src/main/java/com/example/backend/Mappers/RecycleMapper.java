package com.example.backend.Mappers;

import com.example.backend.POJO.DO.RecycleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecycleMapper {
    int insert(RecycleDO recycleDO);

    int deleteById(@Param("id") String id);

    int update(RecycleDO recycleDO);

    RecycleDO selectById(@Param("id") String id);

    List<RecycleDO> selectByCountryAndCity(@Param("country") String country, @Param("city") String city);

    List<RecycleDO> selectAll();
    
    List<RecycleDO> selectByCondition(@Param("name") String name, @Param("country") String country, @Param("city") String city);
}