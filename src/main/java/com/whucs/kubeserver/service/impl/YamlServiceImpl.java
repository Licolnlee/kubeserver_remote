package com.whucs.kubeserver.service.impl;

import com.whucs.kubeserver.base.constant.KubeYamlConstant;
import com.whucs.kubeserver.service.YamlService;
import jdk.internal.util.xml.impl.Input;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.web.Link;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
public class YamlServiceImpl implements YamlService {

    private final static DumperOptions OPTIONS = new DumperOptions();

    static {
        OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        OPTIONS.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        OPTIONS.setPrettyFlow(true);
    }

    @Override
    public String swapYaml(String codePath, String dataPath) {
        if (updateYaml("spec.volumes",dataPath, KubeYamlConstant.ORIGINYAML,KubeYamlConstant.SWAPYAML,false)&&updateYaml("spec.volumes",codePath,KubeYamlConstant.SWAPYAML,KubeYamlConstant.SWAPYAML,true)){
            log.info("{} has been updated",KubeYamlConstant.SWAPYAML);
            return KubeYamlConstant.SWAPYAML;
        }
        return null;
    }

    private Map<String, Object> getYaml2Map(String fileName) {
        LinkedHashMap<String, Object> yamls = new LinkedHashMap<>();
        InputStream inputStream = null;
        Yaml yaml = new Yaml();
        try {
            File file = new File(fileName);
            inputStream = new FileInputStream(file);
            yamls = yaml.loadAs(inputStream, LinkedHashMap.class);
        } catch (FileNotFoundException e) {
            log.error("{} load failed", fileName);
        }
        return yamls;
    }

    public Object getValue(String key, Map<String, Object> map) {
        String[] keys = key.split("[.]");
        Object o = map.get(keys[0]);
        if (key.contains(".")) {
            if (o instanceof Map) {
                return getValue(key.substring(key.indexOf(".") + 1), (Map<String, Object>) o);
            } else {
                return null;
            }
        } else {
            return o;
        }
    }

    public Map<String, Object> setValue(String key, Object value) {
        Map<String, Object> res = new LinkedHashMap<>();
        String[] keys = key.split("[.]");
        int i = keys.length - 1;
        res.put(keys[i], value);
        if (i>0) {
            return setValue(key.substring(0,key.lastIndexOf(".")),res);
        }
        return res;
    }

    public Map<String, Object> setValue(Map<String, Object> map, String key, Object value) {
        String[] keys = key.split("\\.");

        int len = keys.length;
        Map temp = map;
        for (int i = 0; i < len - 1; i++) {
            if (temp.containsKey(keys[i])) {
                temp = (Map) temp.get(keys[i]);
            } else {
                return null;
            }
            if (i == len - 2) {
                temp.put(keys[i + 1], value);
            }
        }
        for (int i = 0; i < len - 1; i++) {
            if (i == len - 1) {
                map.put(keys[i], temp);
            }
        }
        return map;
    }

    @SneakyThrows
    public boolean updateYaml(String key, @Nullable Object value, String fileOrigin, String fileName, Boolean codeOrData) {
        Map<String, Object> map = this.getYaml2Map(fileOrigin);
        Yaml yaml = new Yaml(OPTIONS);
        String path = fileName;
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        if (map == null) {
            return false;
        }
        Object oldVal = this.getValue(key, map);
        if (null == oldVal) {
            log.error("{} not exist", key);
            return false;
        }
        if (oldVal instanceof Map) {
            log.error("input key is not last node {}", key);
            return false;
        }
        if (oldVal instanceof ArrayList) {
//            try {
//                Map<String, Object> resultMap = setValue(map, key, value);
//                if (resultMap != null) {
////                @Cleanup InputStream inputStream = TestYaml.class.getClassLoader().getResourceAsStream(fileName);
////                Yaml yaml = new Yaml(OPTIONS);
//
//                    return true;
//                } else {
//                    return false;
//                }
//            } catch (Exception e) {
//                log("{} update failed", fileName);
//                log("msg: {}", e.getMessage());
//                log("cause: {}", e.getCause());
//            }
            for (Object o : (ArrayList) oldVal) {
                if (o instanceof Map) {
                    if (((Map) o).containsKey("name")) {
                        if (((Map) o).get("name").equals("data") && !codeOrData) {
                            log.info("this is data volume");
                            if (((Map) o).containsKey("nfs")) {
//                                try {
                                LinkedHashMap<String, Object> nfs = (LinkedHashMap<String, Object>) ((Map) o).get("nfs");
                                if (nfs.containsKey("path")) {
                                    nfs.put("path", value);
                                }
//                                System.out.println(nfs);

//                            }
                            yaml.dump(map, new FileWriter(file));
//                            System.out.println(oldVal);
                            return true;
                            }
                        } else if (((Map) o).get("name").equals("code") && codeOrData) {
                            log.info("this is code volume");
                            if (((Map) o).containsKey("nfs")) {
                                LinkedHashMap<String, Object> nfs = (LinkedHashMap<String, Object>) ((Map) o).get("nfs");
                                if (nfs.containsKey("path")) {
                                    nfs.put("path", value);
                                }
//                                System.out.println(nfs);

                            }
                            yaml.dump(map, new FileWriter(file));
//                            System.out.println(oldVal);
                            return true;
                        }
                    }
                }
            }
        }
        if (value.equals(oldVal)) {
            log.error("newVal equals oldVal,newVal:{},oldVal:{}", oldVal);
            return false;
        }


        return false;
    }


}
