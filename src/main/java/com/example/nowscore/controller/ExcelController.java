package com.example.nowscore.controller;

import com.alibaba.excel.ExcelWriter;
import com.example.nowscore.service.impl.ExcelService;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Excel控制器
 * @author kuangyoupeng1
 * @date 2022.4.1
 */
@RestController
public class ExcelController {
    /**
     * excel服务
     */
    @Resource
    ExcelService excelService;

    @GetMapping("/excel/port")
    private void export(HttpServletResponse response) throws IOException {
        excelService.export(response);
    }

    @GetMapping("/excel/load")
    private void load() throws FileNotFoundException {
        excelService.load();
    }
}
