package com.whucs.kubeserver.service.impl;

import com.whucs.kubeserver.base.constant.KubeYamlConstant;
import com.whucs.kubeserver.base.utils.KubeUtils;
import com.whucs.kubeserver.service.PodService;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodBuilder;
import io.kubernetes.client.openapi.models.V1PodCondition;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Yaml;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class PodServiceImpl implements PodService {

    @SneakyThrows
    @Override
    public void listAllPods() {

        ApiClient client = KubeUtils.kubeGetClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        V1PodList list = api.listPodForAllNamespaces(null,null,null,null,null,null,null,null,null,null,null);
        for(V1Pod item:list.getItems()){
            System.out.println(item.getMetadata().getName());
        }
    }

    @SneakyThrows
    @Override
    public String getPodLog(String podId) {
        ApiClient client = KubeUtils.kubeGetClient();
        Configuration.setDefaultApiClient(client);
        CoreV1Api coreV1Api = new CoreV1Api(client);

        PodLogs logs = new PodLogs();
        V1Pod pod = coreV1Api.readNamespacedPod(podId,"default","true");
        InputStream is = logs.streamNamespacedPodLog(pod);
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader bf = new BufferedReader(isr);
        StringBuilder res = new StringBuilder();
        String newLine = "";
        while ((newLine=bf.readLine())!=null){
            res.append(newLine).append("\n");
        }
        return res.toString();
    }

    @SneakyThrows
    @Override
    public String getPodStatus(String podId) {
        ApiClient client = KubeUtils.kubeGetClient();
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api(client);
        V1Pod pod = api.readNamespacedPod(podId,"default","true");
        List<V1PodCondition> podCondition = pod.getStatus().getConditions();
        String containerStatuses = pod.getStatus().getPhase();
        StringBuilder sb = new StringBuilder();
//        for(V1PodCondition item:podCondition){
//         sb.append(containerStatuses.get(containerStatuses.size()-1).getState());
//        }
        return containerStatuses;
    }

    @SneakyThrows
    @Override
    public String createPod(String nameSpace) {
        ApiClient client = KubeUtils.kubeGetClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();

        V1Pod pod = new V1PodBuilder()
                .withNewMetadata()
                .endMetadata()
                .build();
        V1Pod namespacedPod = api.createNamespacedPod(nameSpace, pod, null, null, null, null);

        return namespacedPod.getMetadata().getName();
    }

    @SneakyThrows
    @Override
    public boolean createPodByYaml(String tempYamlPath) {
        ApiClient client = KubeUtils.kubeGetClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();

        if (tempYamlPath!=null) {
            V1Pod pod = (V1Pod) Yaml.load(new File(tempYamlPath));
            V1Pod result = api.createNamespacedPod(KubeYamlConstant.DEFAULTNAMESPACE,pod,null,null,null,null);
            log.info("{} has been deployed.",result.getMetadata().getName());
            return true;
        }
        return false;
    }
}
