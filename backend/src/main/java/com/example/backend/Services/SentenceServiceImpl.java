package com.example.backend.Services;

import com.example.backend.Mappers.SentenceMapper;
import com.example.backend.POJO.DO.SentenceDO;
import com.example.backend.POJO.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service("SentenceService")
public class SentenceServiceImpl implements SentenceService {
    @Autowired
    private SentenceMapper sentenceMapper;

    @Override
    public Result createSentence(SentenceDO sentenceDO){
        Result result = new Result();
        sentenceDO.setCreateDate(LocalDateTime.now());
        sentenceMapper.insert(sentenceDO);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("create sentence success");
        return result;
    }

    public Result updateSentence(SentenceDO sentenceDO){
        Result result = new Result();
        sentenceMapper.updateById(sentenceDO);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("update sentence success");
        return result;
    }

    public Result deleteSentence(Long id){
        Result result = new Result();
        sentenceMapper.deleteById(id);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("delete sentence success");
        return result;
    }

    public Result checkSentence(Long id){
        Result result = new Result();
        sentenceMapper.selectById(id);
        result.setSuccess(Boolean.TRUE);
        result.setMessage("find sentence success");
        return result;
    }

    public Result findSentence() {
        Result result = new Result();
        Random random = new Random();
        int currentMax = 101;
        SentenceDO sentenceDO = null;
        int minMax = 1;
        Long randomId = (long) random.nextInt(currentMax);
        sentenceDO = sentenceMapper.selectById(randomId);

        while (sentenceDO == null && currentMax > minMax) {
            currentMax = randomId.intValue() + 1;
            randomId = (long) random.nextInt(currentMax);
            sentenceDO = sentenceMapper.selectById(randomId);
        }

        result.setSuccess(Boolean.TRUE);
        result.setMessage("find sentence success");
        result.setData(sentenceDO);
        return result;
    }
    
    @Override
    public Result listSentences(Integer page, Integer size, String sentence) {
        if (page == null || page <= 0) {
            page = 1;
        }
        if (size == null || size <= 0) {
            size = 10;
        }

        PageHelper.startPage(page, size);

        List<SentenceDO> sentenceList;
        if (sentence != null && !sentence.isEmpty()) {
            sentenceList = sentenceMapper.selectByCondition(sentence);
        } else {
            sentenceList = sentenceMapper.selectAll();
        }
        PageInfo<SentenceDO> pageInfo = new PageInfo<>(sentenceList);

        Map<String, Object> data = new HashMap<>();
        data.put("list", pageInfo.getList());
        data.put("total", pageInfo.getTotal());
        data.put("page", page);
        data.put("size", size);

        Result result = new Result();
        result.setData(data);
        result.setSuccess(true);
        result.setMessage("获取句子列表成功");
        return result;
    }
}
