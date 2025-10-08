package com.justintime.jit.service.impl;

import com.justintime.jit.entity.*;
import com.justintime.jit.dto.AddOnDTO;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.repository.AddOnRepository;
import com.justintime.jit.repository.ComboRepo.ComboRepository;
import com.justintime.jit.repository.MenuItemRepository;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.service.AddOnService;
import com.justintime.jit.util.CommonServiceImplUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AddOnServiceImpl extends BaseServiceImpl<AddOn, Long> implements AddOnService {

    @Autowired
    private AddOnRepository addOnRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private ComboRepository comboRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CommonServiceImplUtil commonServiceImplUtil;


    public List<AddOnDTO> getAllAddOnsForRestaurant() {
        String restaurantCode = getRestaurantCodeFromJWTBean();
        List<AddOn> addOns = addOnRepository.findAllByRestaurant_RestaurantCode(restaurantCode);
        GenericMapper<AddOn, AddOnDTO> mapper = MapperFactory.getMapper(AddOn.class, AddOnDTO.class);
        List<AddOnDTO> addOnDTOs = new ArrayList<>();
        for (AddOn addOn : addOns) {
            addOnDTOs.add(mapToDTO(addOn, mapper));
        }
        return addOnDTOs;
    }

    public List<AddOnDTO> getAllAddOnsForMenuItem(String menuItemName) {
        String restaurantCode = getRestaurantCodeFromJWTBean();
        List<AddOn> addOns = addOnRepository.findAllByRestaurantCodeAndMenuItemName(restaurantCode, menuItemName);
        GenericMapper<AddOn, AddOnDTO> mapper = MapperFactory.getMapper(AddOn.class, AddOnDTO.class);
        return addOns.stream()
                .map(addOn -> mapToDTO(addOn, mapper))
                .collect(Collectors.toList());
    }

    public List<AddOnDTO> getAllAddOnsForCombo(String comboName) {
        String restaurantCode = getRestaurantCodeFromJWTBean();
        List<AddOn> addOns = addOnRepository.findAllByRestaurantCodeAndComboName(restaurantCode, comboName);
        GenericMapper<AddOn, AddOnDTO> mapper = MapperFactory.getMapper(AddOn.class, AddOnDTO.class);
        return addOns.stream()
                .map(addOn -> mapToDTO(addOn, mapper))
                .collect(Collectors.toList());
    }

    public List<AddOnDTO> getAllAddOnsForOrderItem(String orderNumber, String orderItemName) {
        String restaurantCode = getRestaurantCodeFromJWTBean();
        List<AddOn> addOns = addOnRepository.findAddOnsByRestaurantCodeAndOrderNumberAndOrderItemName(restaurantCode, orderNumber, orderItemName);
        GenericMapper<AddOn, AddOnDTO> mapper = MapperFactory.getMapper(AddOn.class, AddOnDTO.class);
        return addOns.stream()
                .map(addOn -> mapToDTO(addOn, mapper))
                .collect(Collectors.toList());
    }

    public void createAddOn(AddOnDTO addOnDTO) {
        String restaurantCode = getRestaurantCodeFromJWTBean();
        GenericMapper<AddOn, AddOnDTO> mapper = MapperFactory.getMapper(AddOn.class, AddOnDTO.class);
        AddOn addOn = mapper.toEntity(addOnDTO);
        Restaurant restaurant = restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with code: " + restaurantCode));
        resolveRelationships(addOn, addOnDTO, new HashSet<>(), false);
        addOn.setRestaurant(restaurant);
        addOnRepository.save(addOn);
    }
    public AddOnDTO updateAddOn(AddOnDTO updatedAddOnDTO) {
        String restaurantCode = getRestaurantCodeFromJWTBean();
        AddOn existingAddOn = addOnRepository.findByLabel(updatedAddOnDTO.getLabel())
                .orElseThrow(() -> new RuntimeException("AddOn not found with label: " + updatedAddOnDTO.getLabel()));
        GenericMapper<AddOn, AddOnDTO> mapper = MapperFactory.getMapper(AddOn.class, AddOnDTO.class);
        AddOn updatedAddOn = mapper.toEntity(updatedAddOnDTO);
        existingAddOn.setRestaurant(
                restaurantRepository.findByRestaurantCode(restaurantCode)
                        .orElseThrow(() -> new RuntimeException("Restaurant not found with code: " + restaurantCode))
        );
        resolveRelationships(existingAddOn, updatedAddOnDTO, new HashSet<>(), false);
        BeanUtils.copyProperties(updatedAddOn, existingAddOn, "id", "createdDttm", "restaurant");
        existingAddOn.setUpdatedDttm(LocalDateTime.now());
        return mapToDTO(addOnRepository.save(existingAddOn), mapper);
    }

    public AddOnDTO patchAddOn(AddOnDTO addOnDTO, HashSet<String> propertiesToBeUpdated) {
        String restaurantCode = getRestaurantCodeFromJWTBean();
        GenericMapper<AddOn, AddOnDTO> mapper = MapperFactory.getMapper(AddOn.class, AddOnDTO.class);
        AddOn existingAddOn = addOnRepository.findByLabel(addOnDTO.getLabel())
                .orElseThrow(() -> new RuntimeException("AddOn not found with label: " + addOnDTO.getLabel()));
        if (!existingAddOn.getRestaurant().getRestaurantCode().equals(restaurantCode)) {
            throw new RuntimeException("AddOn does not belong to the restaurant with code: " + restaurantCode);
        }
        Restaurant restaurant = restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with code: " + restaurantCode));
        AddOn patchedAddOn = mapper.toEntity(addOnDTO);
        patchedAddOn.setRestaurant(restaurant);
        resolveRelationships(patchedAddOn, addOnDTO, propertiesToBeUpdated, true); // must come before copy
        commonServiceImplUtil.copySelectedProperties(patchedAddOn, existingAddOn, propertiesToBeUpdated);
        existingAddOn.setUpdatedDttm(LocalDateTime.now());
        return mapToDTO(addOnRepository.save(existingAddOn), mapper);
    }

    public void deleteAddOn(String label) {
        String restaurantCode = getRestaurantCodeFromJWTBean();
        AddOn addOn = addOnRepository.findByLabel(label)
                .orElseThrow(() -> new RuntimeException("AddOn not found with label: " + label));
        if (!addOn.getRestaurant().getRestaurantCode().equals(restaurantCode)) {
            throw new RuntimeException("AddOn does not belong to the restaurant with code: " + restaurantCode);
        }
//        if (orderItemRepository.existsInActiveOrderItemByAddOnsAndRestaurantCode(addOn, restaurantCode)) {
//            throw new RuntimeException("Cannot delete AddOn as it is associated with active OrderItems.");
//        }
        addOnRepository.delete(addOn);
    }

    private AddOnDTO mapToDTO(AddOn addOn, GenericMapper<AddOn, AddOnDTO> mapper) {
        AddOnDTO dto = mapper.toDto(addOn);
        dto.setMenuItemNames(
                addOn.getMenuItemSet().stream()
                        .map(MenuItem::getName)
                        .collect(Collectors.toSet())
        );
        dto.setComboNames(
                addOn.getComboSet().stream()
                .map(Combo::getComboName)
                .collect(Collectors.toSet()));
        return dto;
    }

    private void resolveRelationships(AddOn addOn, AddOnDTO addOnDTO, Set<String> propertiesToBeUpdated, Boolean isPatch) {
        if (!isPatch || propertiesToBeUpdated.contains("menuItemSet")) {
            if (addOnDTO.getMenuItemNames() != null) {
                Set<MenuItem> menuItemSet = menuItemRepository.findByMenuItemNamesAndRestaurantId(
                        addOnDTO.getMenuItemNames(), addOn.getRestaurant().getId());
                addOn.setMenuItemSet(menuItemSet);
            }
        }
        if (!isPatch || propertiesToBeUpdated.contains("comboSet")) {
            if (addOnDTO.getComboNames() != null) {
                Set<Combo> comboSet = comboRepository.findByComboNamesAndRestaurantId(
                        addOnDTO.getComboNames(), addOn.getRestaurant().getId());
                addOn.setComboSet(comboSet);
            }
        }
    }
}
