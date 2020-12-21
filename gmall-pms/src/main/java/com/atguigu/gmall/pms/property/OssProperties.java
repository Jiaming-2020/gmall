package com.atguigu.gmall.pms.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
@ConfigurationProperties(prefix = "oss")
@Component
public class OssProperties {
    private String accessId;
    private String accessKey;
    private String endpoint;
    private String bucket;
    private String host;

    @PostConstruct
    public void init(){
        host = "https://" + bucket + "." + endpoint;
    }
}