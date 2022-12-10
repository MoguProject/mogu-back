package com.teamof4.mogu.service;

import com.teamof4.mogu.entity.Category;
import com.teamof4.mogu.entity.Skill;
import com.teamof4.mogu.repository.CategoryRepository;
import com.teamof4.mogu.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;

    public List<Category> getCategoryList() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public List<Skill> getSkillList() {
        return skillRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

}
