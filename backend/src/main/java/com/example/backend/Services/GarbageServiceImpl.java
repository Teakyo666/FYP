package com.example.backend.Services;

import com.example.backend.Mappers.GarbageMapper;
import com.example.backend.POJO.DO.GarbageDO;
import com.example.backend.POJO.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("GarbageService")
public class GarbageServiceImpl implements GarbageService {
    @Autowired
    private GarbageMapper garbageMapper;

    @Override
    public Result createGarbage(GarbageDO garbageDO) {
        String uuid = UUID.randomUUID().toString();
        garbageDO.setId(uuid);
        Result result = new Result();
        garbageMapper.insert(garbageDO);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("insert garbage success");
        return result;
    }
    public Result deleteGarbage(String Id) {
        garbageMapper.deleteById(Id);
        Result result = new Result();
        result.setSuccess(Boolean.TRUE);
        result.setMessage("delete garbage success");
        return result;
    }
    public Result updateGarbage(GarbageDO garbageDO) {
        garbageMapper.update(garbageDO);
        Result result = new Result();
        result.setSuccess(Boolean.TRUE);
        result.setMessage("update garbage success");
        return result;
    }
    public Result getGarbage(String garbageName) {
        Result result = new Result();
        GarbageDO garbageDO = garbageMapper.selectByName(garbageName);
        if (garbageDO == null) {
            result.setSuccess(Boolean.FALSE);
            result.setMessage("No relevant records were found, Please wait for the data to be added.");
            return result;
        }else {
            result.setSuccess(Boolean.TRUE);
            result.setMessage("get garbage success");
            result.setData(garbageDO);
        }
        return result;
    }

    @Override
    public Result listGarbage(int page, int size, String keyword, String type) {

        if (page <= 0) page = 1;
        if (size <= 0) size = 10;
        if (size > 50) size = 50;

        PageHelper.startPage(page, size);

        List<GarbageDO> garbages = garbageMapper.selectWithConditions(
            (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null,
            (type != null && !type.trim().isEmpty()) ? type.trim() : null
        );

        PageInfo<GarbageDO> pageInfo = new PageInfo<>(garbages);

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
