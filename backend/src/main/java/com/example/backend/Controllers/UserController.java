package com.example.backend.Controllers;


import com.example.backend.POJO.DO.LoginDO;
import com.example.backend.POJO.Result;
import com.example.backend.POJO.VO.LoginRequestVO;
import com.example.backend.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    // admin register
    @PostMapping("/create")
    public Result createUser(@Validated @RequestBody LoginDO loginDO) {
        return userService.createUser(loginDO);
    }

    // delete user && user information
    @PostMapping("/delete")
    public Result deleteUser(@Validated @RequestParam String id) {
        return userService.deleteUser(id);
    }

    @PostMapping("/update")
    public Result updateUser(@Validated @RequestBody LoginDO loginDO) {
        return userService.updateUser(loginDO);
    }

    @GetMapping("/get")
    public Result getUser(@RequestParam String username) {
        return userService.getUser(username);
    }

    @GetMapping("/list")
    public Result listUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        return userService.listUsers(page, size, keyword, role);
    }

}
