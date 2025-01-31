package com.justintime.jit.service.impl;

import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.Food;
import com.justintime.jit.repository.CategoryRepository;
import com.justintime.jit.repository.FoodRepository;
import com.justintime.jit.service.FoodService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl extends BaseServiceImpl<Food, Long> implements FoodService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    @Override
    public Food getFoodById(Long id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found with id: " + id));
    }

    @Transactional
    public Food createFood(Food food) {
        if (food.getCategories() != null && !food.getCategories().isEmpty()) {
            Set<Category> existingCategories = food.getCategories().stream()
                    .map(cat -> categoryRepository.findById(cat.getId())
                            .orElseThrow(() -> new RuntimeException("Category not found: " + cat.getId())))
                    .collect(Collectors.toSet());

            food.setCategories(existingCategories);
        }
        return foodRepository.save(food);
    }


    @Override
    public Food updateFood(Long id, Food foodDetails) {
        return foodRepository.findById(id)
                .map(food -> {
                    // Update food name and updated date
                    food.setFoodName(foodDetails.getFoodName());
                    food.setUpdatedDttm(foodDetails.getUpdatedDttm());

                    // Fetch the categories based on the IDs provided in the update request
                    Set<Category> categories = foodDetails.getCategories().stream()
                            .map(category -> categoryRepository.findById(category.getId())
                                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + category.getId())))
                            .collect(Collectors.toSet());

                    // Set the updated categories to the food entity
                    food.setCategories(categories);

                    // Save the updated food entity
                    return foodRepository.save(food);
                }).orElseThrow(() -> new RuntimeException("Food not found with id: " + id));
    }

    @Override
    public void deleteFood(Long id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found with id: " + id));
        foodRepository.delete(food);
    }

}