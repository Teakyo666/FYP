package com.example.backend.Mappers;

import com.example.backend.POJO.DO.SentenceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SentenceMapper {

    int insert(SentenceDO sentenceDO);

    int deleteById(@Param("id") Long id);

    int updateById(SentenceDO sentenceDO);

    SentenceDO selectById(@Param("id") Long id);

    List<SentenceDO> selectAll();

}