package com.whucs.kubeserver.service;

import lombok.SneakyThrows;

public interface PodService {
    @SneakyThrows
    void listAllPods();

    String getPodLog(String podId);

    String getPodStatus(String podId);

    String createPod(String nameSpace);

    boolean createPodByYaml(String tempYamlPath);
}
