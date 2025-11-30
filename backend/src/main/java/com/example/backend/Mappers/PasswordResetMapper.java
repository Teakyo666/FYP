package com.example.backend.Mappers;

import com.example.backend.POJO.VO.PasswordResetDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PasswordResetMapper {

    void insertResetToken(PasswordResetDO resetDO);

    PasswordResetDO selectByToken(String resetToken);

    void updateTokenUsed(String resetToken);
}