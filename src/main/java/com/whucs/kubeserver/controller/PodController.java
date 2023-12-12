package com.whucs.kubeserver.controller;

import com.whucs.kubeserver.base.utils.R;
import com.whucs.kubeserver.service.PodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("pod")
public class PodController {

    @Resource
    PodService podService;

    @RequestMapping("/create")
    public R createPod(String nameSpace){
        String createResult = podService.createPod(nameSpace);
        return R.ok().put("createResult",createResult);
    }

//    @RequestMapping("/list")
//    public R listPod(){
//        return R.ok(podService.listAllPods());
//    }
    @RequestMapping("/log/{podId}")
    public R getPodLog(@PathVariable("podId") String podId){
        String podLog = podService.getPodLog(podId);
        return R.ok().put("log",podLog);
    }

    @RequestMapping("/status/{podId}")
    public R getPodStatus(@PathVariable("podId") String podId){
        String podStatus = podService.getPodStatus(podId);
        return R.ok().put("status",podStatus);
    }


}
