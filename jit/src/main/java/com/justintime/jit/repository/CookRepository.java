package com.justintime.jit.repository;

import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.Cook;

import java.util.Optional;

public interface CookRepository extends BaseRepository<Category,Long> {
    Cook findByName(String name);
}
