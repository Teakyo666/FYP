package com.example.backend.Mappers;

import com.example.backend.POJO.DO.SentenceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface SentenceMapper {

    int insert(SentenceDO sentenceDO);

    int deleteById(@Param("id") Long id);

    int updateById(SentenceDO sentenceDO);

    SentenceDO selectById(@Param("id") Long id);

    List<SentenceDO> selectByUsedDate(@Param("usedDate") Date usedDate);

    SentenceDO selectRandomByJump();
    
    List<SentenceDO> selectAll();
    
    List<SentenceDO> selectByCondition(@Param("sentence") String sentence);
}