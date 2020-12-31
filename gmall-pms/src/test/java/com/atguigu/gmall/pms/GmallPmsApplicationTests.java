package com.atguigu.gmall.pms;

import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SkuService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallPmsApplicationTests {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Test
    void contextLoads() {
        rabbitTemplate.convertAndSend("pms.spuVO", "pms.insert.spuVO", "hello");
    }

    @Test
    void testQuery() {
//        System.out.println(skuAttrValueService.querySaleAttrsBySpuId(7L));
        skuAttrValueService.querySaleAttrValueMappingSkuIdBySpuId(7L);
    }
}
