package com.justintime.jit.service.impl;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.dto.TimeIntervalDTO;
import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.Cook;
import com.justintime.jit.entity.Enums.Sort;
import com.justintime.jit.entity.TimeInterval;
import com.justintime.jit.repository.CategoryRepository;
import com.justintime.jit.repository.CookRepository;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import com.justintime.jit.repository.TimeIntervalRepository;
import com.justintime.jit.service.MenuItemService;
import com.justintime.jit.repository.MenuItemRepository;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.util.filter.FilterItemsUtil;
import com.justintime.jit.util.mapper.GenericMapperImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
    public class MenuItemServiceImpl extends BaseServiceImpl<MenuItem,Long> implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    private final CategoryRepository categoryRepository;

    private final CookRepository cookRepository;

    private final OrderItemRepository orderItemRepository;

    private final TimeIntervalRepository timeIntervalRepository;

    private final GenericMapperImpl<MenuItem, MenuItemDTO> menuItemMapper;

    private final FilterItemsUtil filterItemsUtil;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository,
                               CategoryRepository categoryRepository,
                               CookRepository cookRepository,
                               OrderItemRepository orderItemRepository,
                               TimeIntervalRepository timeIntervalRepository,
                               GenericMapperImpl<MenuItem, MenuItemDTO> menuItemMapper,
                               FilterItemsUtil filterItemsUtil) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.cookRepository = cookRepository;
        this.orderItemRepository = orderItemRepository;
        this.timeIntervalRepository = timeIntervalRepository;
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
                    menuItemDTO.setTimeIntervalSet(convertTimeIntervals(item.getTimeIntervalSet())); // Add conversion
                    return menuItemDTO;
                })
                .collect(Collectors.toList());
    }

    public MenuItemDTO addMenuItem(MenuItemDTO menuItemDTO) {
        MenuItem menuItem = menuItemMapper.toEntity(menuItemDTO, MenuItem.class);
        menuItem.setCategorySet(menuItemDTO.getCategorySet().stream()
                .map(categoryRepository::findByCategoryName
//                        .orElseGet(() -> {
//                            Category newCategory = new Category();
//                            newCategory.setCategoryName(categoryName);
//                            return newCategory;
//                        })
                )
                .collect(Collectors.toSet()));
        menuItem.setCookSet(menuItemDTO.getCookSet().stream()
                .map(cookRepository::findByName
//                        .orElseGet(() -> {
//                            Cook newCook = new Cook();
//                            newCook.setName(cookName);
//                            return newCook;
//                        })
                )
                .collect(Collectors.toSet()));
        menuItem.setTimeIntervalSet(menuItemDTO.getTimeIntervalSet().stream()
                .map(timeIntervalDTO -> {
                    return timeIntervalRepository.findByStartTimeAndEndTime(
                            timeIntervalDTO.getStartTime(), timeIntervalDTO.getEndTime()
                    ).orElseGet(() -> {
                        TimeInterval newInterval = new TimeInterval();
                        newInterval.setStartTime(timeIntervalDTO.getStartTime());
                        newInterval.setEndTime(timeIntervalDTO.getEndTime());
                        return timeIntervalRepository.save(newInterval);
                    });
                })
                .collect(Collectors.toSet()));

        menuItem.setUpdatedDttm(LocalDateTime.now());
        menuItemRepository.save(menuItem);
        return menuItemDTO;
    }

    public MenuItemDTO updateMenuItem(Long id, MenuItemDTO menuItemDTO) {
        MenuItem existingItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));

        MenuItem updatedItem = menuItemMapper.toEntity(menuItemDTO, MenuItem.class);

        updatedItem.setCategorySet(menuItemDTO.getCategorySet().stream()
                .map(categoryRepository::findByCategoryName)
                .collect(Collectors.toSet()));

        updatedItem.setCookSet(menuItemDTO.getCookSet().stream()
                .map(cookRepository::findByName)
                .collect(Collectors.toSet()));

        updatedItem.setTimeIntervalSet(menuItemDTO.getTimeIntervalSet().stream()
                .map(timeIntervalDTO -> timeIntervalRepository.findByStartTimeAndEndTime(
                        timeIntervalDTO.getStartTime(), timeIntervalDTO.getEndTime()
                ).orElseGet(() -> {
                    TimeInterval newInterval = new TimeInterval();
                    newInterval.setStartTime(timeIntervalDTO.getStartTime());
                    newInterval.setEndTime(timeIntervalDTO.getEndTime());
                    return timeIntervalRepository.save(newInterval);
                }))
                .collect(Collectors.toSet()));

        updatedItem.setUpdatedDttm(LocalDateTime.now());

        BeanUtils.copyProperties(updatedItem, existingItem, "id", "createdDttm");

        menuItemRepository.save(existingItem);
        return menuItemDTO;
    }


    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    public List<MenuItemDTO> getMenuItemsByRestaurantId(Long restaurantId, Sort sortBy, String priceRange, String category, Boolean onlyVeg, Boolean onlyForCombos) {
        List<MenuItem> menuItems = menuItemRepository.findByRestaurantId(restaurantId);
        return FilterItemsUtil.filterAndSortItems(menuItems, restaurantId, sortBy, priceRange, category, onlyVeg, onlyForCombos, orderItemRepository, menuItemMapper ,MenuItemDTO.class);
    }

    public static Set<TimeIntervalDTO> convertTimeIntervals(Set<TimeInterval> timeIntervalSet) {
        if (timeIntervalSet == null) return Collections.emptySet(); // Return empty set

        return timeIntervalSet.stream()
                .map(interval -> new TimeIntervalDTO(interval.getStartTime(), interval.getEndTime())) // Get start and end
                .collect(Collectors.toSet());
    }
}
