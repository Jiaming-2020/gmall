package com.atguigu.gmall.index.feign;

import com.atguigu.gmall.pms.feign.PmsFeignService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("pms-service")
public interface PmsFeignClient extends PmsFeignService {
}
