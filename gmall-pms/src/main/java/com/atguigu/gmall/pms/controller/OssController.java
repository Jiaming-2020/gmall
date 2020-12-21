package com.atguigu.gmall.pms.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.service.OssService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "对象存储 管理")
@RestController
@RequestMapping("pms/oss")
public class OssController {
    @Autowired
    private OssService ossService;

    @GetMapping("policy")
    public ResponseVo<Map<String, String>> policy() {
        return ResponseVo.ok(ossService.policy());
    }
}