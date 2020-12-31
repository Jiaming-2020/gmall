package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;

    @GetMapping("{skuId}.html")
    public String toItem(@PathVariable("skuId") Long skuId, Model model) {
        model.addAttribute("itemVo", itemService.queryItemVoBySkuId(skuId));
        return "item";
    }
}
