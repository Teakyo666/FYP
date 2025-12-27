package com.example.backend.Services;

import com.example.backend.Mappers.AIGenerateMapper;
import com.example.backend.Mappers.GarbageMapper;
import com.example.backend.POJO.DO.AIGenerateDO;
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

@Service("AIGenerateService")
public class AIGenerateServiceImpl implements AIGenerateService {
    @Autowired
    private AIGenerateMapper AIMapper;
    @Autowired
    private GarbageMapper garbageMapper;

    @Override
    public Result create(AIGenerateDO aiGenerateDO){
        Result result = new Result();
        aiGenerateDO.setId(UUID.randomUUID().toString());
        AIMapper.insert(aiGenerateDO);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("insert success");
        return result;
    }

    @Override
    public Result update(AIGenerateDO aiGenerateDO){
        Result result = new Result();
        AIMapper.update(aiGenerateDO);
        result.setSuccess(Boolean.TRUE);
        if(aiGenerateDO.getStatus().equals("approved")){
            GarbageDO garbageDO = new GarbageDO();
            garbageDO.setId(aiGenerateDO.getId());
            garbageDO.setReason(aiGenerateDO.getReason());
            garbageDO.setTitle(aiGenerateDO.getTitle());
            garbageDO.setName(aiGenerateDO.getName());
            garbageDO.setCategory(aiGenerateDO.getCategory());
            garbageMapper.insert(garbageDO);
            result.setMessage("garbage insert success");
        }else{
            result.setMessage("update success");
        }
        return result;
    }

    @Override
    public Result delete(String id){
        Result result = new Result();
        AIMapper.deleteById(id);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("delete success");
        return result;
    }

    @Override
    public Result listAIGarbage(Integer page, Integer size, String keyword, String type, String status) {
        // Parameter verification
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 10;
        if (size > 50) size = 50;

        // Use PageHelper for pagination
        PageHelper.startPage(page, size);

        // Execute the query
        List<AIGenerateDO> AIgarbages = AIMapper.selectWithConditions(
                (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null,
                (type != null && !type.trim().isEmpty()) ? type.trim() : null,
                (status != null && !status.trim().isEmpty()) ? status.trim() : null
        );

        // Obtain pagination information
        PageInfo<AIGenerateDO> pageInfo = new PageInfo<>(AIgarbages);

        // Construct the return result
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