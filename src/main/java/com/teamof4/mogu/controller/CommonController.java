package com.teamof4.mogu.controller;

import com.teamof4.mogu.entity.Category;
import com.teamof4.mogu.entity.Skill;
import com.teamof4.mogu.service.CommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cm")
@Api(tags = {"05. Common API"})
public class CommonController {

    private final CommonService commonService;

    @GetMapping("/category")
    @ApiOperation(value = "카테고리 리스트 조회")
    public ResponseEntity<List<Category>> getCategoryList() {
        return ResponseEntity.ok(commonService.getCategoryList());
    }

    @GetMapping("/skill")
    @ApiOperation(value = "스킬 리스트 조회")
    public ResponseEntity<List<Skill>> getSkillList() {
        return ResponseEntity.ok(commonService.getSkillList());
    }
}
