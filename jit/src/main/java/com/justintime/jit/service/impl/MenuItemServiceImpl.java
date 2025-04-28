package com.justintime.jit.service.impl;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.dto.TimeIntervalDTO;
import com.justintime.jit.entity.*;
import com.justintime.jit.entity.Enums.Sort;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.*;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import com.justintime.jit.service.MenuItemService;
import com.justintime.jit.util.CommonServiceImplUtil;
import com.justintime.jit.util.filter.FilterItemsUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuItemServiceImpl extends BaseServiceImpl<MenuItem, Long> implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final CookRepository cookRepository;
    private final OrderItemRepository orderItemRepository;
    private final TimeIntervalRepository timeIntervalRepository;
    private final FilterItemsUtil filterItemsUtil;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    private CommonServiceImplUtil commonServiceImplUtil;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository,
                               CategoryRepository categoryRepository,
                               CookRepository cookRepository,
                               OrderItemRepository orderItemRepository,
                               TimeIntervalRepository timeIntervalRepository,
                               FilterItemsUtil filterItemsUtil,RestaurantRepository restaurantRepository) {

        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.cookRepository = cookRepository;
        this.orderItemRepository = orderItemRepository;
        this.timeIntervalRepository = timeIntervalRepository;
        this.filterItemsUtil = filterItemsUtil;
        this.restaurantRepository= restaurantRepository;
    }

    @Override
    public List<MenuItemDTO> getAllMenuItems() {
        GenericMapper<MenuItem, MenuItemDTO> mapper = MapperFactory.getMapper(MenuItem.class, MenuItemDTO.class);
        return menuItemRepository.findAll().stream()
                .map(item -> mapToDTO(item, mapper))
                .collect(Collectors.toList());
    }
  
    @Override
    public MenuItem addMenuItem(String restaurantCode,MenuItemDTO menuItemDTO) {
        GenericMapper<MenuItem, MenuItemDTO> mapper = MapperFactory.getMapper(MenuItem.class, MenuItemDTO.class);
        MenuItem menuItem = mapper.toEntity(menuItemDTO);
        menuItem.setRestaurant(restaurantRepository.findByRestaurantCode(restaurantCode).orElseThrow(() -> new RuntimeException("Restaurant not found")));
        resolveRelationships(menuItem, menuItemDTO);
        menuItem.setUpdatedDttm(LocalDateTime.now());
        return menuItemRepository.save(menuItem);
    }

    @Override
    public MenuItemDTO getMenuItemByRestaurantIdAndId(Long restaurantId, Long id){
        GenericMapper<MenuItem, MenuItemDTO> mapper = MapperFactory.getMapper(MenuItem.class, MenuItemDTO.class);
        MenuItem menuItem = menuItemRepository.findByRestaurantIdAndId(restaurantId, id);
        return mapToDTO(menuItem, mapper);
    }

    @Override
    public MenuItem updateMenuItem(String restaurantCode,Long id, MenuItemDTO menuItemDTO) {
        MenuItem existingItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));
        GenericMapper<MenuItem, MenuItemDTO> menuItemMapper = MapperFactory.getMapper(MenuItem.class, MenuItemDTO.class);
        MenuItem patchedItem = menuItemMapper.toEntity(menuItemDTO);
        patchedItem.setRestaurant(restaurantRepository.findByRestaurantCode(restaurantCode).orElseThrow(() -> new RuntimeException("Restaurant not found")));
        resolveRelationships(patchedItem, menuItemDTO);
        BeanUtils.copyProperties(patchedItem, existingItem, "id", "createdDttm");
        existingItem.setUpdatedDttm(LocalDateTime.now());
        return menuItemRepository.save(existingItem);
    }

    @Override
    public MenuItemDTO patchUpdateMenuItem(String restaurantCode, String menuItemName, MenuItemDTO menuItemDTO, HashSet<String> propertiesToBeUpdated) {
        MenuItem existingItem = menuItemRepository.findByRestaurantCodeAndMenuItemName(restaurantCode, menuItemName)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));
        GenericMapper<MenuItem, MenuItemDTO> menuItemMapper = MapperFactory.getMapper(MenuItem.class, MenuItemDTO.class);
        MenuItem patchedItem = menuItemMapper.toEntity(menuItemDTO);
        patchedItem.setRestaurant(restaurantRepository.findByRestaurantCode(restaurantCode).orElseThrow(() -> new RuntimeException("Restaurant not found")));
        resolveRelationships(patchedItem, menuItemDTO);
        commonServiceImplUtil.copySelectedProperties(patchedItem, existingItem, propertiesToBeUpdated);
        existingItem.setUpdatedDttm(LocalDateTime.now());
        MenuItem menuItem = menuItemRepository.save(existingItem);
        return mapToDTO(menuItem, menuItemMapper);
    }

    @Override
    public void deleteMenuItem(String restaurantCode, String menuItemName) {
        MenuItem menuItem = menuItemRepository.findByRestaurantCodeAndMenuItemName(restaurantCode, menuItemName)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
        menuItem.getCookSet().clear();
        menuItem.getCategorySet().clear();
        menuItem.getTimeIntervalSet().clear();
        menuItemRepository.deleteByRestaurantCodeAndMenuItemName(restaurantCode, menuItemName);
    }

    @Override
    public List<MenuItemDTO> getMenuItemsByRestaurantId(String restaurantCode, Sort sortBy, String priceRange, String category, Boolean onlyVeg, Boolean onlyForCombos) {
        List<MenuItem> menuItems = menuItemRepository.findByRestaurantCode(restaurantCode);
        GenericMapper<MenuItem, MenuItemDTO> mapper = MapperFactory.getMapper(MenuItem.class, MenuItemDTO.class);
//        return FilterItemsUtil.filterAndSortItems(menuItems, restaurantId, sortBy, priceRange, category, onlyVeg, onlyForCombos, orderItemRepository, mapper, MenuItemDTO.class);
        return menuItems.stream().map(menuItem -> mapToDTO(menuItem, mapper)).toList();
    }

    private MenuItemDTO mapToDTO(MenuItem menuItem, GenericMapper<MenuItem, MenuItemDTO> mapper) {
        MenuItemDTO dto = mapper.toDto(menuItem);
        dto.setCategorySet(menuItem.getCategorySet().stream()
                .map(Category::getCategoryName)
                .collect(Collectors.toSet()));
        dto.setCookSet(menuItem.getCookSet().stream()
                .map(Cook::getName)
                .collect(Collectors.toSet()));
        dto.setTimeIntervalSet(convertTimeIntervals(menuItem.getTimeIntervalSet()));
        return dto;
    }

    private void resolveRelationships(MenuItem menuItem, MenuItemDTO menuItemDTO) {
        if (menuItemDTO.getCategorySet() != null) {
            Set<Category> categories = categoryRepository.findByCategoryNamesAndRestaurantId(
                    menuItemDTO.getCategorySet(), menuItem.getRestaurant().getId());
            menuItem.setCategorySet(categories);
        }
        if (menuItemDTO.getCookSet() != null) {
            Set<Cook> cooks = cookRepository.findByNameInAndRestaurantId(
                    menuItemDTO.getCookSet(), menuItem.getRestaurant().getId());
            menuItem.setCookSet(cooks);
        }
        if (menuItemDTO.getTimeIntervalSet() != null) {
            menuItem.setTimeIntervalSet(menuItemDTO.getTimeIntervalSet().stream()
                    .map(dto -> timeIntervalRepository.findByStartTimeAndEndTime(dto.getStartTime(), dto.getEndTime())
                            .orElseGet(() -> {
                                TimeInterval newInterval = new TimeInterval();
                                newInterval.setStartTime(dto.getStartTime());
                                newInterval.setEndTime(dto.getEndTime());
                                return timeIntervalRepository.save(newInterval);
                            }))
                    .collect(Collectors.toSet()));
        }
    }

    public static Set<TimeIntervalDTO> convertTimeIntervals(Set<TimeInterval> timeIntervalSet) {
        if (timeIntervalSet == null) return Collections.emptySet();
        return timeIntervalSet.stream()
                .map(interval -> new TimeIntervalDTO(interval.getStartTime(), interval.getEndTime()))
                .collect(Collectors.toSet());
    }
}
