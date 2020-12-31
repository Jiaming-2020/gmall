package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.wms.feign.WmsFeignService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("wms-service")
public interface WmsFeignClient extends WmsFeignService {
}
