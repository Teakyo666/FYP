package com.example.backend.Services;

import com.example.backend.POJO.VO.ForgotPasswordVO;
import com.example.backend.POJO.VO.LoginRequestVO;
import com.example.backend.POJO.VO.ResetPasswordVO;
import com.example.backend.POJO.Result;

public interface LoginService {
    String createUser(LoginRequestVO loginRequestVO);
    Result login(LoginRequestVO loginRequestVO);
    Result applyResetPassword(ForgotPasswordVO dto);
    Result confirmResetPassword(ResetPasswordVO dto);
}
