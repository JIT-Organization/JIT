package com.justintime.jit.service.impl;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.entity.Enums.Filter;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import com.justintime.jit.service.MenuItemService;
import com.justintime.jit.repository.MenuItemRepository;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.util.filter.FilterItemsUtil;
import com.justintime.jit.util.mapper.GenericMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
    public class MenuItemServiceImpl extends BaseServiceImpl<MenuItem,Long> implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    private final OrderItemRepository orderItemRepository;

    @Autowired
    private GenericMapperImpl<MenuItem, MenuItemDTO> menuItemMapper;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository, OrderItemRepository orderItemRepository){
        this.menuItemRepository = menuItemRepository;
        this.orderItemRepository = orderItemRepository;
    }

        public List<MenuItemDTO> getAllMenuItems() {
            return menuItemRepository.findAll()
                    .stream()
                    .map(menuItem -> menuItemMapper.toDTO(menuItem, MenuItemDTO.class))
                    .collect(Collectors.toList());
        }

        public MenuItem addMenuItem(MenuItem menuItem) {
            menuItem.setUpdatedDttm(LocalDateTime.now());
            return menuItemRepository.save(menuItem);
        }

        public MenuItem updateMenuItem(Long id, MenuItem updatedItem) {
            MenuItem existingItem = menuItemRepository.findById(id).orElseThrow(() -> new RuntimeException("MenuItem not found"));
            existingItem.setRestaurant(updatedItem.getRestaurant());
            existingItem.setMenuItemName(updatedItem.getMenuItemName());
            existingItem.setDescription(updatedItem.getDescription());
            existingItem.setPrice(updatedItem.getPrice());
            existingItem.setOfferPrice(updatedItem.getOfferPrice());
            existingItem.setStock(updatedItem.getStock());
            existingItem.setCount(updatedItem.getCount());
            existingItem.setTimeIntervalSet(updatedItem.getTimeIntervalSet());
            existingItem.setCookSet(updatedItem.getCookSet());
            existingItem.setPreparationTime(updatedItem.getPreparationTime());
            existingItem.setOnlyVeg(updatedItem.getOnlyVeg());
            existingItem.setOnlyForCombos(updatedItem.getOnlyForCombos());
            existingItem.setBase64Image(updatedItem.getBase64Image());
            existingItem.setHotelSpecial(updatedItem.getHotelSpecial());
            existingItem.setRating(updatedItem.getRating());
            return menuItemRepository.save(existingItem);
        }

        public void deleteMenuItem(Long id) {
            menuItemRepository.deleteById(id);
        }

    public List<MenuItemDTO> getMenuItemsByRestaurantId(Long restaurantId, Filter sortBy, String priceRange, String category, boolean onlyForCombos) {
        List<MenuItem> menuItems = menuItemRepository.findByRestaurantId(restaurantId);
        GenericMapperImpl<MenuItem, MenuItemDTO> genericMapper = new GenericMapperImpl<>(new ModelMapper());
        FilterItemsUtil filterItemsUtil = new FilterItemsUtil(genericMapper);
        return filterItemsUtil.filterAndSortItems(menuItems, restaurantId, sortBy, priceRange, category, onlyForCombos,orderItemRepository);
    }
}


