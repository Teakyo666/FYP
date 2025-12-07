package com.example.backend.Controllers;

import com.example.backend.POJO.DO.SentenceDO;
import com.example.backend.POJO.Result;
import com.example.backend.Services.SentenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sentence")
@Validated
public class SentenceController {
    @Autowired
    private SentenceService sentenceService;

    @PostMapping("create")
    public Result createSentence(@RequestBody SentenceDO sentenceDO) {
        return sentenceService.createSentence(sentenceDO);
    }

    @PostMapping("/delete")
    public Result deleteSentence(@RequestParam Long id) {
        return sentenceService.deleteSentence(id);
    }

    @PostMapping("/update")
    public Result updateSentence(@RequestBody SentenceDO sentenceDO) {
        return sentenceService.updateSentence(sentenceDO);
    }

    @GetMapping("/check")
    public Result checkSentence(@RequestParam Long id) {
        return sentenceService.checkSentence(id);
    }

    @GetMapping("/get")
    public Result findSentence() {
        return sentenceService.findSentence();
    }
    
    @GetMapping("/list")
    public Result listSentences(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer size,
                               @RequestParam(required = false) String sentence) {
        return sentenceService.listSentences(page, size, sentence);
    }
}
