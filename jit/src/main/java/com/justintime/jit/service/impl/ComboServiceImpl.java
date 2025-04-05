package com.justintime.jit.service.impl;

import com.justintime.jit.dto.ComboDTO;
import com.justintime.jit.dto.ComboItemDTO;
import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.Enums.Sort;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.repository.ComboRepo.ComboRepository;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import com.justintime.jit.service.ComboService;
import com.justintime.jit.entity.Category;
import com.justintime.jit.util.filter.FilterItemsUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.justintime.jit.service.impl.MenuItemServiceImpl.convertTimeIntervals;

@Service
public class ComboServiceImpl extends BaseServiceImpl<Combo,Long> implements ComboService {
    private final ComboRepository comboRepository;

    private final OrderItemRepository orderItemRepository;

    public ComboServiceImpl(ComboRepository comboRepository,
                            OrderItemRepository orderItemRepository
                            ){
        this.comboRepository = comboRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<ComboDTO> getAllCombos() {
        GenericMapper<Combo, ComboDTO> comboMapper = MapperFactory.getMapper(Combo.class, ComboDTO.class);
        return comboRepository.findAll()
                .stream()
                .map(combo -> {
                    ComboDTO comboDTO = comboMapper.toDto(combo);

                    // Map Combo Items
                    comboDTO.setComboItemSet(
                            combo.getComboItemSet().stream()
                                    .map(comboItem -> new ComboItemDTO(
                                            comboItem.getId(),
                                            comboItem.getMenuItem().getId(), // Assuming there's a getMenuItem() method
                                            comboItem.getMenuItem().getMenuItemName(), // Assuming getMenuItemName() exists
                                            comboItem.getQuantity()
                                    ))
                                    .collect(Collectors.toSet())
                    );

                    // Map Categories
                    comboDTO.setCategorySet(
                            combo.getCategorySet().stream()
                                    .map(Category::getCategoryName)
                                    .collect(Collectors.toSet())
                    );

                    // Map Time Intervals
                    comboDTO.setTimeIntervalSet(
                            convertTimeIntervals(combo.getTimeIntervalSet()) // Assuming this method exists
                    );

                    return comboDTO;
                })
                .collect(Collectors.toList());
    }
///////////////////////////////////////////////////////////////////////
    public List<ComboDTO> getCombosByRestaurantId(Long restaurantId, Sort sortBy, String priceRange, String category, Boolean onlyVeg, Boolean onlyForCombos) {
        List<Combo> combos = comboRepository.findByRestaurantId(restaurantId);
        GenericMapper<Combo, ComboDTO> comboMapper = MapperFactory.getMapper(Combo.class, ComboDTO.class);
        return FilterItemsUtil.filterAndSortItems(combos, restaurantId, sortBy, priceRange, category, onlyVeg, onlyForCombos, orderItemRepository, comboMapper, ComboDTO.class);
    }
//////////////////////////////////////////////////////////////////////////
    public Optional<Combo> getComboById(Long id) {
        return comboRepository.findById(id);
    }

    public Combo createCombo(Combo combo) {
        return comboRepository.save(combo);
    }

    public Combo updateCombo(Long id, Combo updatedCombo) {
        return comboRepository.findById(id)
                .map(combo -> {
                    combo.setComboItemSet(updatedCombo.getComboItemSet());
                    combo.setPrice(updatedCombo.getPrice());
                    combo.setStock(updatedCombo.getStock());
                    combo.setUpdatedDttm(updatedCombo.getUpdatedDttm());
                    return comboRepository.save(combo);
                }).orElseThrow(() -> new RuntimeException("Combo not found with id: " + id));
    }

    public void deleteCombo(Long id) {
        comboRepository.deleteById(id);
    }


}



