package com.example.backend.Services;

import com.example.backend.Mappers.GarbageMapper;
import com.example.backend.POJO.DO.GarbageDO;
import com.example.backend.POJO.Result;
import com.example.backend.POJO.VO.GarbageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service("GarbageService")
public class GarbageServiceImpl implements GarbageService {
    @Autowired
    private GarbageMapper garbageMapper;

    @Override
    public Result createGarbage(GarbageVO garbageVO) {
        String uuid = UUID.randomUUID().toString();
        GarbageDO garbageDO = new GarbageDO();
        garbageDO.setId(uuid);
        garbageDO.setName(garbageVO.getName());
        garbageDO.setCategory(garbageVO.getCategory());
        garbageDO.setReason(garbageVO.getReason());
        Result result = new Result();
        result.setSuccess(Boolean.TRUE);
        result.setMessage("insert garbage success");
        return result;
    }
    public Result deleteGarbage(String garbageId) {
        garbageMapper.deleteById(garbageId);
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
}
