package com.example.backend.Controllers;

import com.example.backend.POJO.DO.ApplyDO;
import com.example.backend.POJO.Result;
import com.example.backend.Services.ApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apply")
public class ApplyController {

    @Autowired
    private ApplyService applyService;

    @PostMapping("/create")
    public Result create(@RequestBody ApplyDO applyDO){
        return applyService.create(applyDO);
    }

    @PostMapping("/update")
    public Result update(@RequestBody ApplyDO applyDO){
        return applyService.update(applyDO);
    }

    @PostMapping ("/delete")
    public Result delete(@RequestParam String id){
        return applyService.delete(id);
    }

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(required = false) String to_apply,
                       @RequestParam(required = false) String status) {
        return applyService.listApplies(page, size, to_apply, status);
    }
}