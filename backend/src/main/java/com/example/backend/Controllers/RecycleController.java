package com.example.backend.Controllers;


import com.example.backend.POJO.DO.RecycleDO;
import com.example.backend.POJO.Result;
import com.example.backend.Services.RecycleService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/place")
@Validated
public class RecycleController {
    @Resource
    private RecycleService recycleService;

    @PostMapping("/create")
    public Result CreatePlace(@RequestBody RecycleDO recycleDO) {
        return recycleService.CreatePlace(recycleDO);
    }
    @PostMapping("/delete")
    public Result DeletePlace(@RequestBody RecycleDO recycleDO) {
        return recycleService.DeletePlace(recycleDO.getId());
    }
    @PostMapping("/update")
    public Result UpdatePlace(@RequestBody RecycleDO recycleDO) {
        return recycleService.UpdatePlace(recycleDO);
    }
    @GetMapping("search-place")
    public Result SearchPlace(@RequestParam("Country") String Country, @RequestParam("City") String City) {
        return recycleService.SearchPlace(Country, City);
    }
    @GetMapping("get")
    public Result GetPlace(@RequestParam("PlaceId") String Id) {
        return recycleService.GetPlace(Id);
    }
    
    @GetMapping("/list")
    public Result listPlaces(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer size,
        @RequestParam(required = false) String name
    ) {
        return recycleService.listPlaces(page, size, name);
    }
}
