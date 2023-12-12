package com.whucs.kubeserver.base.utils;


import com.whucs.kubeserver.base.constant.ServerConstant;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import lombok.SneakyThrows;
import org.apache.catalina.Server;
import org.apache.commons.compress.archivers.sevenz.CLI;

import java.io.FileReader;

public class KubeUtils {

    @SneakyThrows
    public static ApiClient kubeGetClient(){
        String kubeConfigPath = System.getProperty(ServerConstant.KUBE_SERVER_USER_PATH) + ServerConstant.KUBE_SERVER_KUBE_CONFIG_PATH;
        ApiClient client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
        return client;
    }
}
