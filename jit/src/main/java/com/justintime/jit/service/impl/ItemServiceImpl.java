package com.justintime.jit.service.impl;

import com.justintime.jit.dto.AddOnDTO;
import com.justintime.jit.dto.ComboItemDTO;
import com.justintime.jit.dto.ItemDTO;
import com.justintime.jit.entity.*;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.ComboEntities.ComboItem;
import com.justintime.jit.entity.Enums.FoodType;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.*;
import com.justintime.jit.repository.ComboRepo.ComboItemRepository;
import com.justintime.jit.repository.ComboRepo.ComboRepository;
import com.justintime.jit.service.ItemService;
import com.justintime.jit.util.CommonServiceImplUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
        import java.util.stream.Collectors;

import static com.justintime.jit.service.impl.MenuItemServiceImpl.convertTimeIntervals;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private ComboRepository comboRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ComboItemRepository comboItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimeIntervalRepository timeIntervalRepository;

    @Autowired
    private CommonServiceImplUtil commonServiceImplUtil;

    @Override
    public List<ItemDTO> getAllItemsForRestaurant(String restaurantCode) {
        List<ItemDTO> itemDTOs = new ArrayList<>();
        List<MenuItem> menuItems = menuItemRepository.findByRestaurantCode(restaurantCode);
        List<Combo> combos = comboRepository.findByRestaurantCode(restaurantCode);
        GenericMapper<MenuItem, ItemDTO> menuItemMapper = MapperFactory.getMapper(MenuItem.class, ItemDTO.class);
        GenericMapper<Combo, ItemDTO> comboMapper = MapperFactory.getMapper(Combo.class, ItemDTO.class);
        if (menuItems != null) {
            itemDTOs.addAll(menuItems.stream()
                    .map(menuItem -> mapMenuItemToItemDTO(menuItem, menuItemMapper))
                    .toList());
        }
        if (combos != null) {
            itemDTOs.addAll(combos.stream()
                    .map(combo -> mapComboToItemDTO(combo, comboMapper))
                    .toList());
        }
        return itemDTOs;
    }

    @Override
    public List<ItemDTO> getAllItemsForRestaurantAndFoodType(String restaurantCode, FoodType foodType) {
        List<ItemDTO> itemDTOs = new ArrayList<>();
        if (foodType == FoodType.MENU_ITEM) {
            List<MenuItem> menuItems = menuItemRepository.findByRestaurantCode(restaurantCode);
            GenericMapper<MenuItem, ItemDTO> menuItemMapper = MapperFactory.getMapper(MenuItem.class, ItemDTO.class);
            if (menuItems != null) {
                itemDTOs.addAll(menuItems.stream()
                        .map(menuItem -> mapMenuItemToItemDTO(menuItem, menuItemMapper))
                        .toList());
            }
        } else if (foodType == FoodType.COMBO) {
            List<Combo> combos = comboRepository.findByRestaurantCode(restaurantCode);
            GenericMapper<Combo, ItemDTO> comboMapper = MapperFactory.getMapper(Combo.class, ItemDTO.class);
            if (combos != null) {
                itemDTOs.addAll(combos.stream()
                        .map(combo -> mapComboToItemDTO(combo, comboMapper))
                        .toList());
            }
        } else if (foodType == FoodType.VARIANT){
            throw new UnsupportedOperationException("Variant type is not supported yet.");
        } else {
            throw new ResourceNotFoundException("Invalid food type: " + foodType);
        }
        return itemDTOs;
    }

    @Override
    public List<String> getAllItemNamesForRestaurant(String restaurantCode) {
        List<String> menuItemNames = menuItemRepository.findMenuItemNamesByRestaurantCode(restaurantCode);
        List<String> comboNames = comboRepository.findComboNamesByRestaurantCode(restaurantCode);
        List<String> allItems = new ArrayList<>(menuItemNames);
        allItems.addAll(comboNames);
        return allItems;
    }

    @Override
    public List<String> getAllItemNamesForRestaurantAndFoodType(String restaurantCode, FoodType foodType) {
        return switch (foodType) {
            case MENU_ITEM -> menuItemRepository.findMenuItemNamesByRestaurantCode(restaurantCode);
            case COMBO -> comboRepository.findComboNamesByRestaurantCode(restaurantCode);
            case VARIANT -> throw new UnsupportedOperationException("Variant type is not supported yet.");
            default -> throw new ResourceNotFoundException("Invalid food type: " + foodType);
        };
    }

    @Override
    public ItemDTO getItemByRestaurantAndNameAndFoodType(String restaurantCode, String itemName, FoodType foodType) {
        Restaurant restaurant = restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with code: " + restaurantCode));
        if (foodType == FoodType.MENU_ITEM) {
            MenuItem menuItem = menuItemRepository.findByRestaurantIdAndMenuItemName(restaurant.getId(), itemName);
            if (menuItem == null) {
                throw new ResourceNotFoundException("MenuItem not found with name: " + itemName);
            }
            GenericMapper<MenuItem, ItemDTO> menuItemMapper = MapperFactory.getMapper(MenuItem.class, ItemDTO.class);
            return mapMenuItemToItemDTO(menuItem, menuItemMapper);
        } else if (foodType == FoodType.COMBO) {
            Combo combo = comboRepository.findByRestaurantIdAndComboName(restaurant.getId(), itemName);
            if (combo == null) {
                throw new ResourceNotFoundException("Combo not found with name: " + itemName);
            }
            GenericMapper<Combo, ItemDTO> comboMapper = MapperFactory.getMapper(Combo.class, ItemDTO.class);
            return mapComboToItemDTO(combo, comboMapper);
        } else if (foodType == FoodType.VARIANT) {
            throw new UnsupportedOperationException("Variant type is not supported yet.");
        } else {
            throw new ResourceNotFoundException("Invalid food type: " + foodType);
        }
    }

    @Override
    public ItemDTO createItem(String restaurantCode, FoodType foodType, ItemDTO itemDTO) {
        Restaurant restaurant = restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with code: " + restaurantCode));
        if (foodType == FoodType.MENU_ITEM) {
            GenericMapper<MenuItem, ItemDTO> menuItemMapper = MapperFactory.getMapper(MenuItem.class, ItemDTO.class);
            MenuItem menuItem = menuItemMapper.toEntity(itemDTO);
            if (menuItem != null) {
                menuItem.setMenuItemName(itemDTO.getItemName());
                menuItem.setRestaurant(restaurant);
                mapItemDTOToMenuItem(itemDTO, menuItem, menuItemMapper, Set.of("categorySet", "cookSet", "timeIntervalSet"), false);
                return mapMenuItemToItemDTO(menuItemRepository.save(menuItem), menuItemMapper);
            }
        } else if (foodType == FoodType.COMBO) {
            GenericMapper<Combo, ItemDTO> comboMapper = MapperFactory.getMapper(Combo.class, ItemDTO.class);
            Combo combo = comboMapper.toEntity(itemDTO);
            if (combo != null) {
                combo.setComboName(itemDTO.getItemName());
                combo.setRestaurant(restaurant);
                mapItemDTOToCombo(itemDTO, combo, comboMapper, Set.of("categorySet", "comboItemSet", "timeIntervalSet"), false);
                return mapComboToItemDTO(comboRepository.save(combo), comboMapper);
            }
        }
        throw new ResourceNotFoundException("Invalid food type: " + foodType);
    }

    @Override
    public ItemDTO updateItem(String restaurantCode, String itemName, FoodType foodType, ItemDTO itemDTO) {
        Restaurant restaurant = restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with code: " + restaurantCode));
        if (foodType == FoodType.MENU_ITEM) {
            MenuItem menuItem = menuItemRepository.findByRestaurantIdAndMenuItemName(restaurant.getId(), itemName);
            if (menuItem == null) {
                throw new ResourceNotFoundException("MenuItem not found with name: " + itemName);
            }
            menuItem.setMenuItemName(itemName);
            menuItem.setRestaurant(restaurant);
            GenericMapper<MenuItem, ItemDTO> menuItemMapper = MapperFactory.getMapper(MenuItem.class, ItemDTO.class);
            mapItemDTOToMenuItem(itemDTO, menuItem, menuItemMapper, new HashSet<>(), false);
            return mapMenuItemToItemDTO(menuItemRepository.save(menuItem), menuItemMapper);
        } else if (foodType == FoodType.COMBO) {
            Combo combo = comboRepository.findByRestaurantIdAndComboName(restaurant.getId(), itemName);
            if (combo == null) {
                throw new ResourceNotFoundException("Combo not found with name: " + itemName);
            }
            combo.setComboName(itemName);
            combo.setRestaurant(restaurant);
            GenericMapper<Combo, ItemDTO> comboMapper = MapperFactory.getMapper(Combo.class, ItemDTO.class);
            mapItemDTOToCombo(itemDTO, combo, comboMapper, Set.of("categorySet", "comboItemSet", "timeIntervalSet"), false);
            return mapComboToItemDTO(comboRepository.save(combo), comboMapper);
        } else if (foodType == FoodType.VARIANT) {
            throw new UnsupportedOperationException("Variant type is not supported yet.");
        } else {
            throw new ResourceNotFoundException("Invalid food type: " + foodType);
        }
    }
    @Override
    public ItemDTO patchItem(String restaurantCode, String itemName, FoodType foodType, ItemDTO itemDTO, HashSet<String> propertiesToBeUpdated) {
        Restaurant restaurant = restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with code: " + restaurantCode));
        if (foodType == FoodType.MENU_ITEM) {
            MenuItem existingMenuItem = menuItemRepository.findByRestaurantIdAndMenuItemName(restaurant.getId(), itemName);
            if (existingMenuItem == null) {
                throw new ResourceNotFoundException("MenuItem not found with name: " + itemName);
            }
            GenericMapper<MenuItem, ItemDTO> menuItemMapper = MapperFactory.getMapper(MenuItem.class, ItemDTO.class);
            MenuItem patchedMenuItem = menuItemMapper.toEntity(itemDTO);
            if (patchedMenuItem != null) {
                patchedMenuItem.setMenuItemName(itemName);
                if (patchedMenuItem.getCategorySet() != null) patchedMenuItem.getCategorySet().clear();
                if (patchedMenuItem.getCookSet() != null) patchedMenuItem.getCookSet().clear();
                if (patchedMenuItem.getTimeIntervalSet() != null) patchedMenuItem.getTimeIntervalSet().clear();
                if (patchedMenuItem.getAddOnSet() != null) patchedMenuItem.getAddOnSet().clear();
                patchedMenuItem.setRestaurant(restaurant);
                mapItemDTOToMenuItem(itemDTO, patchedMenuItem, menuItemMapper, propertiesToBeUpdated, true);
                commonServiceImplUtil.copySelectedProperties(patchedMenuItem, existingMenuItem, propertiesToBeUpdated);
                existingMenuItem.setUpdatedDttm(LocalDateTime.now());
                return mapMenuItemToItemDTO(menuItemRepository.save(existingMenuItem), menuItemMapper);
            }
        } else if (foodType == FoodType.COMBO) {
            Combo existingCombo = comboRepository.findByRestaurantIdAndComboName(restaurant.getId(), itemName);
            if (existingCombo == null) {
                throw new ResourceNotFoundException("Combo not found with name: " + itemName);
            }
            GenericMapper<Combo, ItemDTO> comboMapper = MapperFactory.getMapper(Combo.class, ItemDTO.class);
            Combo patchedCombo = comboMapper.toEntity(itemDTO);
            if (patchedCombo != null) {
                patchedCombo.setComboName(itemName);
                if (patchedCombo.getCategorySet() != null) patchedCombo.getCategorySet().clear();
                if (patchedCombo.getComboItemSet() != null) patchedCombo.getComboItemSet().clear();
                if (patchedCombo.getTimeIntervalSet() != null) patchedCombo.getTimeIntervalSet().clear();
                if (patchedCombo.getAddOnSet() != null) patchedCombo.getAddOnSet().clear();
                patchedCombo.setRestaurant(restaurant);
                mapItemDTOToCombo(itemDTO, existingCombo, comboMapper, propertiesToBeUpdated, true);
                commonServiceImplUtil.copySelectedProperties(patchedCombo, existingCombo, propertiesToBeUpdated);
                existingCombo.setUpdatedDttm(LocalDateTime.now());
                return mapComboToItemDTO(comboRepository.save(existingCombo), comboMapper);
            }
        } else if (foodType == FoodType.VARIANT) {
            throw new UnsupportedOperationException("Variant type is not supported yet.");
        } else {
            throw new ResourceNotFoundException("Invalid food type: " + foodType);
        }
        return null;
    }

    @Override
    public void deleteItem(String restaurantCode, String itemName, FoodType foodType) {
        Restaurant restaurant = restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with code: " + restaurantCode));
        if (foodType == FoodType.MENU_ITEM) {
            MenuItem menuItem = menuItemRepository.findByRestaurantIdAndMenuItemName(restaurant.getId(), itemName);
            if (menuItem == null) {
                throw new ResourceNotFoundException("MenuItem not found with name: " + itemName);
            }
            menuItemRepository.delete(menuItem);
        } else if (foodType == FoodType.COMBO) {
            Combo combo = comboRepository.findByRestaurantIdAndComboName(restaurant.getId(), itemName);
            if (combo == null) {
                throw new ResourceNotFoundException("Combo not found with name: " + itemName);
            }
            comboRepository.delete(combo);
        } else if (foodType == FoodType.VARIANT) {
            throw new UnsupportedOperationException("Variant type is not supported yet.");
        } else {
            throw new ResourceNotFoundException("Invalid food type: " + foodType);
        }
    }

    private ItemDTO mapMenuItemToItemDTO(MenuItem menuItem, GenericMapper<MenuItem, ItemDTO> mapper) {
        ItemDTO itemDTO = mapper.toDto(menuItem);
        itemDTO.setItemName(menuItem.getMenuItemName());
        if (menuItem.getAvailability()!=null){
            itemDTO.setAvailability(menuItem.getAvailability());
        }
        if (menuItem.getCategorySet() != null) {
            itemDTO.setCategorySet(menuItem.getCategorySet().stream()
                    .map(Category::getCategoryName)
                    .collect(Collectors.toSet()));
        }
        if (menuItem.getCookSet() != null) {
            itemDTO.setCookSet(menuItem.getCookSet().stream()
                    .map(User::getUsername)
                    .collect(Collectors.toSet()));
        }
        if (menuItem.getAddOnSet() != null) {
            itemDTO.setAddOnSet(menuItem.getAddOnSet().stream()
                    .map(addOn -> {
                        AddOnDTO dto = new AddOnDTO();
                        dto.setLabel(addOn.getLabel());
                        dto.setPrice(addOn.getPrice());
                        dto.setOptions(addOn.getOptions());
                        return dto;
                    })
                    .collect(Collectors.toSet()));
        }
        itemDTO.setTimeIntervalSet(convertTimeIntervals(menuItem.getTimeIntervalSet()));
        return itemDTO;
    }

    private ItemDTO mapComboToItemDTO(Combo combo, GenericMapper<Combo, ItemDTO> mapper) {
        ItemDTO itemDTO = mapper.toDto(combo);
        itemDTO.setItemName(combo.getComboName());
        if (combo.getAvailability()!=null){
            itemDTO.setAvailability(combo.getAvailability());
        }
        if (combo.getCategorySet() != null) {
            itemDTO.setCategorySet(combo.getCategorySet().stream()
                    .map(Category::getCategoryName)
                    .collect(Collectors.toSet()));
        }
        if (combo.getComboItemSet() != null) {
            itemDTO.setComboItemSet(combo.getComboItemSet().stream()
                    .map(comboItem -> {
                        ComboItemDTO comboItemDTO = new ComboItemDTO();
                        if (comboItem.getMenuItem() != null) {
                            comboItemDTO.setComboItemName(comboItem.getMenuItem().getMenuItemName());
                        }
                        return comboItemDTO;
                    })
                    .collect(Collectors.toSet()));
        }
        if (combo.getAddOnSet() != null) {
            itemDTO.setAddOnSet(combo.getAddOnSet().stream()
                    .map(addOn -> {
                        AddOnDTO dto = new AddOnDTO();
                        dto.setLabel(addOn.getLabel());
                        dto.setPrice(addOn.getPrice());
                        dto.setOptions(addOn.getOptions());
                        return dto;
                    })
                    .collect(Collectors.toSet()));
        }
        itemDTO.setTimeIntervalSet(convertTimeIntervals(combo.getTimeIntervalSet()));
        return itemDTO;
    }

    private void mapItemDTOToMenuItem(ItemDTO itemDTO, MenuItem menuItem, GenericMapper<MenuItem, ItemDTO> menuItemMapper, Set<String> propertiesToBeUpdated, boolean isPatch) {
        if (!isPatch || propertiesToBeUpdated.contains("availability")){
            if (itemDTO.getAvailability()!=null){
                menuItem.setAvailability(itemDTO.getAvailability());
            }
        }
        if (!isPatch || propertiesToBeUpdated.contains("categorySet")) {
            if (itemDTO.getCategorySet() != null) {
                Set<Category> categories = categoryRepository.findByCategoryNamesAndRestaurantId(
                        itemDTO.getCategorySet(), menuItem.getRestaurant().getId());
                menuItem.setCategorySet(categories);
            }
        }
        if (!isPatch || propertiesToBeUpdated.contains("cookSet")) {
            if (itemDTO.getCookSet() != null) {
                Set<User> cooks = userRepository.findCooksByRestaurantIdAndRoleAndUserNames(
                        menuItem.getRestaurant().getId(), Role.COOK, itemDTO.getCookSet());
                menuItem.setCookSet(cooks);
            }
        }
        if (!isPatch || propertiesToBeUpdated.contains("timeIntervalSet")) {
            if (itemDTO.getTimeIntervalSet() != null) {
                menuItem.setTimeIntervalSet(itemDTO.getTimeIntervalSet().stream()
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

    private void mapItemDTOToCombo(ItemDTO itemDTO, Combo combo, GenericMapper<Combo, ItemDTO> comboMapper, Set<String> propertiesToBeUpdated, boolean isPatch) {
        if (!isPatch || propertiesToBeUpdated.contains("categorySet")) {
            if (itemDTO.getCategorySet() != null) {
                Set<Category> categories = categoryRepository.findByCategoryNamesAndRestaurantId(
                        itemDTO.getCategorySet(), combo.getRestaurant().getId());
                combo.setCategorySet(categories);
            }
        }
        if (!isPatch || propertiesToBeUpdated.contains("comboItemSet")) {
            if (itemDTO.getComboItemSet() != null) {
                Set<ComboItem> comboItems = comboItemRepository.findByRestaurantIdAndComboItemNames(
                        combo.getRestaurant().getId(),
                        itemDTO.getComboItemSet().stream()
                                .map(ComboItemDTO::getComboItemName)
                                .collect(Collectors.toSet()));
                combo.setComboItemSet(comboItems);
            }
        }
        if (!isPatch || propertiesToBeUpdated.contains("timeIntervalSet")) {
            if (itemDTO.getTimeIntervalSet() != null) {
                combo.setTimeIntervalSet(itemDTO.getTimeIntervalSet().stream()
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
}

