package com.whucs.kubeserver.controller;

import com.sun.org.apache.bcel.internal.classfile.Code;
import com.whucs.kubeserver.base.utils.R;
import com.whucs.kubeserver.service.TaskService;
import com.whucs.kubeserver.service.YamlService;
import javafx.concurrent.Task;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("task")
public class TaskController {

    @Resource
    TaskService taskService;

    @RequestMapping("/create")
    public R createTask(String codePath, String dataPath) {
        String taskCreateRes = taskService.createTask(codePath, dataPath);
        return R.ok().put("taskCreateRes", taskCreateRes);
    }


}
