package com.atguigu.gmall.search.feign;

import feign.WmsFeignService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("wms-service")
public interface WmsFeignClient extends WmsFeignService {
}
