package com.whucs.kubeserver;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileReader;

@SpringBootTest
class KubeserverApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    @SneakyThrows
    public void listPods(){
        String kubeConfigPath = System.getProperty("user.home") + "/.kube/config";
        ApiClient client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();

        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();

        V1PodList list = api.listPodForAllNamespaces(null,null,null,null,null,null,null,null,null,null,null);
        System.out.println("Listing all pods: ");
        for (V1Pod item:list.getItems()){
            System.out.println(item.getMetadata().getName());
        }
    }

}
