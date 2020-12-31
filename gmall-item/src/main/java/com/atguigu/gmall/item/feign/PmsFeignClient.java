package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.pms.feign.PmsFeignService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("pms-service")
public interface PmsFeignClient extends PmsFeignService {
}
