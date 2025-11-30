package com.example.backend.Controllers;

import com.example.backend.POJO.DO.GarbageDO;
import com.example.backend.POJO.Result;
import com.example.backend.POJO.VO.GarbageVO;
import com.example.backend.Services.GarbageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@Validated
public class GarbageController {
    @Autowired
    private GarbageService garbageService;

    @PostMapping("/create")
    public Result createGarbage(GarbageVO garbageVO) {
        return garbageService.createGarbage(garbageVO);
    }

    @PostMapping("/update")
    public Result updateGarbage(GarbageDO garbageDO) {
        return garbageService.updateGarbage(garbageDO);
    }

    @PostMapping("/delete")
    public Result deleteGarbage(String garbageId) {
        return garbageService.deleteGarbage(garbageId);
    }

    @GetMapping("/get")
    public Result getGarbage(String garbageName) {
        return garbageService.getGarbage(garbageName);
    }

}
