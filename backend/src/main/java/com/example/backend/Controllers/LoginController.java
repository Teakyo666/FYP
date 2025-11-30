package com.example.backend.Controllers;

import com.example.backend.POJO.VO.ForgotPasswordVO;
import com.example.backend.POJO.VO.LoginRequestVO;
import com.example.backend.POJO.VO.ResetPasswordVO;
import com.example.backend.POJO.Result;
import com.example.backend.Services.LoginService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Validated
public class LoginController {
    @Resource
    private LoginService loginService;

    // register
    @PostMapping("/create")
    public Result createUser(@Validated @RequestBody LoginRequestVO reqVO) {

        String id = loginService.createUser(reqVO);
        Result s = new Result();
        s.setSuccess(Boolean.TRUE);
        s.setData(id);
        return s;
    }

    // login
    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginRequestVO reqVO) {
        return loginService.login(reqVO);
    }

    // apply reset token
    @PostMapping("/applyToken")
    public Result applyToken(@Validated @RequestBody ForgotPasswordVO dto) {
        return loginService.applyResetPassword(dto);
    }

    // reset password
    @PostMapping("/resetPassword")
    public Result resetPassword(@Validated @RequestBody ResetPasswordVO dto) {
        return loginService.confirmResetPassword(dto);
    }
}
