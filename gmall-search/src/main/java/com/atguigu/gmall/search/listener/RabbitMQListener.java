package com.atguigu.gmall.search.listener;

import com.atguigu.gmall.search.feign.PmsFeignClient;
import com.atguigu.gmall.search.feign.WmsFeignClient;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.util.QueryUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class RabbitMQListener {
    @Autowired
    private PmsFeignClient pmsFeignClient;
    @Autowired
    private WmsFeignClient wmsFeignClient;
    @Autowired
    private GoodsRepository goodsRepository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "SEARCH_ITEM_INSERT_UPDATE_QUEUE", durable = "true"),
            exchange = @Exchange(value = "PMS_ITEM_EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}
    ))
    public void itemInsertUpdateListener(String msg, Channel channel, Message message) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            goodsRepository.saveAll(QueryUtils.queryGoodsListBySpuEntity(pmsFeignClient.querySpuById(Long.parseLong(msg)).getData(), pmsFeignClient, wmsFeignClient));
            log.info("添加或更新SpuId=" + msg + "成功");
            channel.basicAck(deliveryTag, false);
            log.debug("消息确认成功");
        } catch (Exception e) {
            if (message.getMessageProperties().getRedelivered()) {
                log.error("添加或更新SpuId=" + msg + "失败", e);
                channel.basicReject(deliveryTag, false);
            } else {
                log.warn("添加或更新SpuId=" + msg + "重试", e);
                channel.basicNack(deliveryTag, false, true);
            }
        }
    }

}
