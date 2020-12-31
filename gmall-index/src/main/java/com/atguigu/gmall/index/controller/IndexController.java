package com.atguigu.gmall.index.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private IndexService indexService;

    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        model.addAttribute("categories", indexService.queryLv1Categories(0L));
        return "index";
    }

    @GetMapping("index/cates/{pid}")
    @ResponseBody
    public ResponseVo<List<CategoryEntity>> querySubCategoriesByPid(@PathVariable("pid") Long pid) {
        return ResponseVo.ok(indexService.querySubCategoriesByPid(pid));
    }

    @GetMapping("index/test/lock")
    @ResponseBody
    public ResponseVo testLock() {
        indexService.testLock();
        return ResponseVo.ok();
    }
}
