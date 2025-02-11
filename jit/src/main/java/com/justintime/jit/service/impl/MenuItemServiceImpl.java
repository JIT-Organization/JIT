package com.justintime.jit.service.impl;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.Cook;
import com.justintime.jit.entity.Enums.Filter;
import com.justintime.jit.repository.CategoryRepository;
import com.justintime.jit.repository.CookRepository;
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

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CookRepository cookRepository;

    private final OrderItemRepository orderItemRepository;

    private final GenericMapperImpl<MenuItem, MenuItemDTO> menuItemMapper;

    private final FilterItemsUtil filterItemsUtil;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository,
                               OrderItemRepository orderItemRepository,
                               GenericMapperImpl<MenuItem, MenuItemDTO> menuItemMapper,
                               FilterItemsUtil filterItemsUtil) {
        this.menuItemRepository = menuItemRepository;
        this.orderItemRepository = orderItemRepository;
        this.menuItemMapper = menuItemMapper;
        this.filterItemsUtil = filterItemsUtil;
    }

    public List<MenuItemDTO> getAllMenuItems() {
        return menuItemRepository.findAll()
                .stream()
                .map(item -> {
                    MenuItemDTO menuItemDTO = menuItemMapper.toDTO(item, MenuItemDTO.class);
                    menuItemDTO.setCategorySet(
                            item.getCategorySet().stream()
                                    .map(Category::getCategoryName)
                                    .collect(Collectors.toSet())
                    );
                    menuItemDTO.setCookSet(
                            item.getCookSet().stream()
                                    .map(Cook::getName)
                                    .collect(Collectors.toSet())
                    );
                    return menuItemDTO;
                })
                .collect(Collectors.toList());
    }

    public MenuItem addMenuItem(MenuItemDTO menuItemDTO) {
        MenuItem menuItem = menuItemMapper.toEntity(menuItemDTO, MenuItem.class);
        menuItem.setCategorySet(menuItemDTO.getCategorySet().stream()
                .map(categoryName -> categoryRepository.findByCategoryName(categoryName)
//                        .orElseGet(() -> {
//                            Category newCategory = new Category();
//                            newCategory.setCategoryName(categoryName);
//                            return newCategory;
//                        })
                )
                .collect(Collectors.toSet()));
        menuItem.setCookSet(menuItemDTO.getCookSet().stream()
                .map(cookName -> cookRepository.findByName(cookName)
//                        .orElseGet(() -> {
//                            Cook newCook = new Cook();
//                            newCook.setName(cookName);
//                            return newCook;
//                        })
                )
                .collect(Collectors.toSet()));
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
        return filterItemsUtil.filterAndSortItems(menuItems, restaurantId, sortBy, priceRange, category, onlyForCombos, orderItemRepository);
    }
}
