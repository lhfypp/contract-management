package com.snxy.contract.web;

import com.snxy.common.response.ResultData;
import com.snxy.contract.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by 24398 on 2019/1/2.
 */
@Controller
@Slf4j
public class TestController {

    @Resource
    private TestService testService;

    @PostMapping("/test")
    @ResponseBody
    public ResultData test(){
        this.testService.test();
        return ResultData.success("");
    }


    @GetMapping(value = "/test")
   public String testHtml(){
        return "test";
   }
}
