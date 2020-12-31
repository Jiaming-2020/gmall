package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.sms.feign.SmsFeignService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("sms-service")
public interface SmsFeignClient extends SmsFeignService {
}
