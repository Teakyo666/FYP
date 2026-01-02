package com.example.backend.Services;

import com.example.backend.Mappers.RecycleMapper;
import com.example.backend.POJO.DO.RecycleDO;
import com.example.backend.POJO.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("RecycleService")
public class RecycleServiceImpl implements RecycleService {
    @Autowired
    private RecycleMapper recycleMapper;

    @Override
    public Result CreatePlace (RecycleDO recycleDO){
        Result result = new Result();
        recycleDO.setId(UUID.randomUUID().toString());
        recycleMapper.insert(recycleDO);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("create place success");
        return result;
    }

    @Override
    public Result DeletePlace (String id){
        Result result = new Result();
        recycleMapper.deleteById(id);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("delete place success");
        return result;
    }

    @Override
    public Result UpdatePlace(RecycleDO recycleDO){
        Result result = new Result();
        recycleMapper.update(recycleDO);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("update place success");

        return result;
    }

    @Override
    public Result SearchPlace(String Country, String City){
        Result result = new Result();
        recycleMapper.selectByCountryAndCity(Country,City);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("search place success");
        return result;
    }

    @Override
    public Result GetPlace(String id){
        Result result = new Result();
        recycleMapper.selectById(id);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("get place success");
        return result;
    }
    
    @Override
    public Result listPlaces(Integer page, Integer size, String name, String country, String city) {

        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 10;
        if (size > 50) size = 50;

        PageHelper.startPage(page, size);

        List<RecycleDO> places;
        if ((name != null && !name.trim().isEmpty()) || (country != null && !country.trim().isEmpty()) || (city != null && !city.trim().isEmpty())) {
            places = recycleMapper.selectByCondition(name, country, city);
        } else {
            places = recycleMapper.selectAll();
        }

        PageInfo<RecycleDO> pageInfo = new PageInfo<>(places);

        Map<String, Object> data = new HashMap<>();
        data.put("list", pageInfo.getList());
        data.put("total", pageInfo.getTotal());
        data.put("page", pageInfo.getPageNum());
        data.put("size", pageInfo.getPageSize());
        
        Result result = new Result();
        result.setSuccess(Boolean.TRUE);
        result.setData(data);
        result.setMessage("success");
        return result;
    }
}