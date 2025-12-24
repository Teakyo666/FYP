package com.example.backend.Services;

import com.example.backend.Mappers.ApplyMapper;
import com.example.backend.Mappers.LoginMapper;
import com.example.backend.POJO.DO.ApplyDO;
import com.example.backend.POJO.DO.LoginDO;
import com.example.backend.POJO.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service("ApplyService")
public class ApplyServiceImpl implements ApplyService {
    @Autowired
    private ApplyMapper applyMapper;
    @Autowired
    private LoginMapper loginMapper;

    @Override
    public Result create(ApplyDO applyDO){
        Result result = new Result();
        applyDO.setId(UUID.randomUUID().toString());
        applyMapper.insert(applyDO);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("insert success");
        return result;
    }

    @Override
    public Result update(ApplyDO applyDO){
        Result result = new Result();
        applyMapper.update(applyDO);
        result.setSuccess(Boolean.TRUE);
        if(applyDO.getStatus().equals("同意")){
            LoginDO loginDO = loginMapper.selectById(applyDO.getUser_id());
            loginDO.setRole(applyDO.getTo_apply());
            loginMapper.update(loginDO);
            result.setMessage("user role success");
        }
        result.setMessage("update success");
        return result;
    }

    @Override
    public Result delete(String id){
        Result result = new Result();
        applyMapper.deleteById(id);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("delete success");
        return result;
    }
    
    @Override
    public Result listApplies(Integer page, Integer size, String to_apply, String status) {
        // 设置默认分页参数
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 10;
        if (size > 50) size = 50; // 限制每页最大数量

        // 使用PageHelper进行分页
        PageHelper.startPage(page, size);

        // 根据条件执行不同的查询
        List<ApplyDO> applies;
        if ((to_apply != null && !to_apply.trim().isEmpty()) || (status != null && !status.trim().isEmpty())) {
            // 如果提供了to_apply或status参数，则执行条件查询
            applies = applyMapper.selectByCondition(to_apply, status);
        } else {
            // 否则查询所有记录
            applies = applyMapper.selectAll();
        }

        // 获取分页信息
        PageInfo<ApplyDO> pageInfo = new PageInfo<>(applies);

        // 构造返回结果
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