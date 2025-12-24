package com.example.backend.Controllers;

import com.example.backend.POJO.DO.GarbageDO;
import com.example.backend.POJO.Result;
import com.example.backend.Services.GarbageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/home")
@Validated
public class GarbageController {
    @Autowired
    private GarbageService garbageService;

    @PostMapping("/create")
    public Result createGarbage(@RequestBody GarbageDO garbageDO) {

        return garbageService.createGarbage(garbageDO);
    }

    @PostMapping("/update")
    public Result updateGarbage(@RequestBody GarbageDO garbageDO) {
        return garbageService.updateGarbage(garbageDO);
    }

    @PostMapping("/delete")
    public Result deleteGarbage(@RequestParam String Id) {
        return garbageService.deleteGarbage(Id);
    }

    @GetMapping("/get")
    public Result getGarbage(@RequestParam String garbageName) {
        return garbageService.getGarbage(garbageName);
    }

    @GetMapping("/list")
    public Result listGarbage(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String type
    ) {
        return garbageService.listGarbage(page, size, keyword, type);
    }

}
