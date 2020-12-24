package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.pms.feign.PmsFeignService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "pms-service")
public interface PmsFeignClient extends PmsFeignService {
}
