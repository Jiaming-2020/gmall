package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.sms.feign.SmsFeignService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

@FeignClient(value = "sms-service")
public interface SmsFeignClient extends SmsFeignService {
}