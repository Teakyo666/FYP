package com.example.backend.Controllers;

import com.example.backend.POJO.DO.AIGenerateDO;
import com.example.backend.POJO.Result;
import com.example.backend.Services.AIGenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/AI")
@Validated
public class AIGenerateController {
    @Autowired
    private AIGenerateService AIservice;

    @PostMapping("/create")
    public Result create(@Validated @RequestBody AIGenerateDO generateDO) {
        return AIservice.create(generateDO);
    }

    @PostMapping("/update")
    public Result update(@Validated @RequestBody AIGenerateDO generateDO) {
        return AIservice.update(generateDO);
    }

    @PostMapping("/delete")
    public Result delete(@Validated @RequestParam String id) {
        return AIservice.delete(id);
    }

    @GetMapping("/list")
    public Result listAIGarbage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status
    ) {
        return AIservice.listAIGarbage(page, size, keyword, type, status);
    }

}