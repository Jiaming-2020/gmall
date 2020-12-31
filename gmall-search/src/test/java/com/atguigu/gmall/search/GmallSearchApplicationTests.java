package com.atguigu.gmall.search;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.PmsFeignClient;
import com.atguigu.gmall.search.feign.WmsFeignClient;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.util.QueryUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GmallSearchApplicationTests {
    @Autowired
    private ElasticsearchRestTemplate restTemplate;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private PmsFeignClient pmsFeignClient;
    @Autowired
    private WmsFeignClient wmsFeignClient;

    @Test
    void contextLoads() {
        restTemplate.deleteIndex(Goods.class);
        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);
        Integer pageNum = 1;
        Integer pageSize = 100;

        List<SpuEntity> spuEntities;
        do {
            //获取第pageNum页pageSize条SpuEntity集合
            spuEntities = pmsFeignClient.querySpuByPageJson(new PageParamVo(pageNum, pageSize, "")).getData();
            if (CollectionUtils.isEmpty(spuEntities)) {
                break;
            }

            //声明要导入到es中的Goods集合
            ArrayList<Goods> goodsList = new ArrayList<>();

            //遍历SpuEntity集合
            spuEntities.forEach(spuEntity -> {
                goodsList.addAll(QueryUtils.queryGoodsListBySpuEntity(spuEntity, pmsFeignClient, wmsFeignClient));
            });
            pageNum++;
            goodsRepository.saveAll(goodsList);
        } while (spuEntities.size() == pageSize);
    }

}
