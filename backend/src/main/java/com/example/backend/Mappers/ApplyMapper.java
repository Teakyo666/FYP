package com.example.backend.Mappers;

import com.example.backend.POJO.DO.ApplyDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApplyMapper {

    int insert(ApplyDO applyDO);

    int deleteById(@Param("id") Long id);

    int updateById(ApplyDO applyDO);

    ApplyDO selectById(@Param("id") Long id);

    List<ApplyDO> selectAll();

}