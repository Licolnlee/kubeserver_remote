package com.whucs.kubeserver.service.impl;

import com.whucs.kubeserver.service.PodService;
import com.whucs.kubeserver.service.TaskService;
import com.whucs.kubeserver.service.YamlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;
import javax.xml.crypto.Data;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Resource
    YamlService yamlService;

    @Resource
    PodService podService;

    @Override
    public String createTask(String codePath, String dataPath) {
        String tempYamlPath = yamlService.swapYaml(codePath, dataPath);
        if (tempYamlPath != null) {
            boolean b = podService.createPodByYaml(tempYamlPath);
            if (b) {
                return "pod has been deployed.";
            }

        }
        return "0";
    }
}
