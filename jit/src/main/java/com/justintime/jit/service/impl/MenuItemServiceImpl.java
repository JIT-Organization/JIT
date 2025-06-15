package com.justintime.jit.service.impl;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.dto.TimeIntervalDTO;
import com.justintime.jit.entity.*;
import com.justintime.jit.entity.Enums.Role;
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

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuItemServiceImpl extends BaseServiceImpl<MenuItem, Long> implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final TimeIntervalRepository timeIntervalRepository;
    private final FilterItemsUtil filterItemsUtil;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    private CommonServiceImplUtil commonServiceImplUtil;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository,
                               CategoryRepository categoryRepository,
                               OrderItemRepository orderItemRepository,
                               TimeIntervalRepository timeIntervalRepository,
                               FilterItemsUtil filterItemsUtil,
                               RestaurantRepository restaurantRepository,
                               UserRepository userRepository) {

        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.orderItemRepository = orderItemRepository;
        this.timeIntervalRepository = timeIntervalRepository;
        this.filterItemsUtil = filterItemsUtil;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
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
        resolveRelationships(menuItem, menuItemDTO, new HashSet<>(), false);
        menuItem.setUpdatedDttm(LocalDateTime.now());
        return menuItemRepository.save(menuItem);
    }

    @Override
    public MenuItemDTO getMenuItemByRestaurantIdAndMenuItemName(String restaurantCode,String menuItemName){
        Long restaurantId = restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found")).getId();
        GenericMapper<MenuItem, MenuItemDTO> mapper = MapperFactory.getMapper(MenuItem.class, MenuItemDTO.class);
        MenuItem menuItem = menuItemRepository.findByRestaurantIdAndMenuItemName(restaurantId, menuItemName);
        return mapToDTO(menuItem, mapper);
    }

    @Override
    public MenuItem updateMenuItem(String restaurantCode,Long id, MenuItemDTO menuItemDTO) {
        MenuItem existingItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));
        GenericMapper<MenuItem, MenuItemDTO> menuItemMapper = MapperFactory.getMapper(MenuItem.class, MenuItemDTO.class);
        MenuItem patchedItem = menuItemMapper.toEntity(menuItemDTO);
        patchedItem.setRestaurant(restaurantRepository.findByRestaurantCode(restaurantCode).orElseThrow(() -> new RuntimeException("Restaurant not found")));
        resolveRelationships(patchedItem, menuItemDTO, new HashSet<>(), false);
        BeanUtils.copyProperties(patchedItem, existingItem, "id", "createdDttm");
        existingItem.setUpdatedDttm(LocalDateTime.now());
        return menuItemRepository.save(existingItem);
    }

    @Override
    public MenuItemDTO patchUpdateMenuItem(String restaurantCode, String menuItemName, MenuItemDTO menuItemDTO, HashSet<String> propertiesToBeUpdated) {
        Restaurant restaurant= restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with code: " + restaurantCode));
        MenuItem existingItem = menuItemRepository.findByRestaurantIdAndMenuItemName(restaurant.getId(), menuItemName);
        GenericMapper<MenuItem, MenuItemDTO> menuItemMapper = MapperFactory.getMapper(MenuItem.class, MenuItemDTO.class);
        MenuItem patchedItem = menuItemMapper.toEntity(menuItemDTO);
        if (null != patchedItem.getCategorySet()) patchedItem.getCategorySet().clear();
        if (null != patchedItem.getCookSet()) patchedItem.getCookSet().clear();
        if (null != patchedItem.getTimeIntervalSet()) patchedItem.getTimeIntervalSet().clear();
        patchedItem.setRestaurant(restaurant);
        resolveRelationships(patchedItem, menuItemDTO, propertiesToBeUpdated, true);
        commonServiceImplUtil.copySelectedProperties(patchedItem, existingItem, propertiesToBeUpdated);
        existingItem.setUpdatedDttm(LocalDateTime.now());
        MenuItem menuItem = menuItemRepository.save(existingItem);
        return mapToDTO(menuItem, menuItemMapper);
    }

    @Override
    public void deleteMenuItem(String restaurantCode, String menuItemName) {
        Long restaurantId = restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found")).getId();
        menuItemRepository.deleteByRestaurantIdAndMenuItemName(restaurantId, menuItemName);
    }

    @Override
    public List<MenuItemDTO> getMenuItemsByRestaurantId(String restaurantCode, Sort sortBy, String priceRange, String category, Boolean onlyVeg, Boolean onlyForCombos) {
        List<MenuItem> menuItems = menuItemRepository.findByRestaurantCode(restaurantCode);
        if (menuItems.isEmpty()) {
            throw new ResourceNotFoundException("Restaurant not found with code: " + restaurantCode);
        }
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
                .map(User::getUserName)
                .collect(Collectors.toSet()));
        dto.setTimeIntervalSet(convertTimeIntervals(menuItem.getTimeIntervalSet()));
        return dto;
    }

    private void resolveRelationships(MenuItem menuItem, MenuItemDTO menuItemDTO, Set<String> propertiesToBeUpdated, Boolean isPatch) {
        if (!isPatch || propertiesToBeUpdated.contains("categorySet")) {
            if (menuItemDTO.getCategorySet() != null) {
                Set<Category> categories = categoryRepository.findByCategoryNamesAndRestaurantId(
                        menuItemDTO.getCategorySet(), menuItem.getRestaurant().getId());
                menuItem.setCategorySet(categories);
            }
        }
        if (!isPatch || propertiesToBeUpdated.contains("cookSet")) {
            if (menuItemDTO.getCookSet() != null) {
                Set<User> cooks = userRepository.findCooksByRestaurantIdAndRoleAndUserNames(
                        menuItem.getRestaurant().getId(), Role.COOK, menuItemDTO.getCookSet());
                menuItem.setCookSet(cooks);
            }
        }
        if (!isPatch || propertiesToBeUpdated.contains("timeIntervalSet")) {
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
    }


    public static Set<TimeIntervalDTO> convertTimeIntervals(Set<TimeInterval> timeIntervalSet) {
        if (timeIntervalSet == null) return Collections.emptySet();
        return timeIntervalSet.stream()
                .map(interval -> new TimeIntervalDTO(interval.getStartTime(), interval.getEndTime()))
                .collect(Collectors.toSet());
    }
}
