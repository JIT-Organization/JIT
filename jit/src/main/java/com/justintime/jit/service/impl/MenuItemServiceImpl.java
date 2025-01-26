package com.justintime.jit.service.impl;

import com.justintime.jit.exception.ImageSizeLimitExceededException;
import com.justintime.jit.service.MenuItemService;
import com.justintime.jit.repository.MenuItemRepository;
import com.justintime.jit.entity.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.justintime.jit.helpers.Validations.validateImageSize;

@Service
    public class MenuItemServiceImpl extends BaseServiceImpl<MenuItem,Long> implements MenuItemService {

        @Autowired
        private MenuItemRepository menuItemRepository;

        private static final long MAX_IMAGE_SIZE = 3 * 1024 * 1024;

        public List<MenuItem> getAllMenuItems() {
            return menuItemRepository.findAll();
        }

        public List<MenuItem> getMenuItemsByRestaurantId(Long restaurantId) {
            return menuItemRepository.findByRestaurantId(restaurantId);
        }

        public MenuItem addMenuItem(MenuItem menuItem) throws ImageSizeLimitExceededException {
            validateImageSize(menuItem.getBase64Image(), MAX_IMAGE_SIZE);
            menuItem.setUpdatedDttm(LocalDateTime.now());
            return menuItemRepository.save(menuItem);
        }

        public MenuItem updateMenuItem(Long id, MenuItem updatedItem) throws ImageSizeLimitExceededException {
            if(Objects.nonNull(updatedItem.getBase64Image())) {
                validateImageSize(updatedItem.getBase64Image(), MAX_IMAGE_SIZE);
            }
            MenuItem existingItem = menuItemRepository.findById(id).orElseThrow(() -> new RuntimeException("MenuItem not found"));
            existingItem.setRestaurant(updatedItem.getRestaurant());
            existingItem.setFood(updatedItem.getFood());
            existingItem.setPrice(updatedItem.getPrice());
            existingItem.setStock(updatedItem.getStock());
            existingItem.setUpdatedDttm(LocalDateTime.now());
            return menuItemRepository.save(existingItem);
        }

        public void deleteMenuItem(Long id) {
            menuItemRepository.deleteById(id);
        }
    }


