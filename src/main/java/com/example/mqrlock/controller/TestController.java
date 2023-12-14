package com.example.mqrlock.controller;

import com.example.mqrlock.entity.TestEntity;
import com.example.mqrlock.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final TestService testService;

    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/save")
    public String saveTestEntities() {
        List<TestEntity> list = testService.query();
        return "Entities saved successfully! count:"+list.size();
    }
}
