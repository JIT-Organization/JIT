package com.justintime.jit.util.filter;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.entity.Enums.Filter;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import com.justintime.jit.util.mapper.GenericMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class FilterItemsUtil {

    private final GenericMapperImpl<MenuItem, MenuItemDTO> genericMapper;

    @Autowired
    public FilterItemsUtil(GenericMapperImpl<MenuItem, MenuItemDTO> genericMapper) {
        this.genericMapper = genericMapper;
    }

    public List<MenuItemDTO> filterAndSortItems(
            List<MenuItem> menuItems,
            Long addressId,
            Filter sortBy,
            String priceRange,
            String category,
            boolean onlyForCombos,
            OrderItemRepository orderItemRepository) {

        if (category != null && !category.trim().isEmpty()) {
            menuItems = menuItems.stream()
                    .filter(item -> item.getCategorySet().stream()
                            .anyMatch(c -> category.equalsIgnoreCase(c.getCategoryName()))) // Corrected for Set<Category>
                    .collect(Collectors.toList());
        }

        if (Filter.POPULARITY.equals(sortBy)) {
            List<Long> menuItemIds = menuItems.stream()
                    .map(MenuItem::getId)
                    .collect(Collectors.toList());

            List<Object[]> result = orderItemRepository.findMenuItemsWithOrderCount(addressId, menuItemIds);

            Map<Long, Integer> idToCountMap = result.stream()
                    .collect(Collectors.toMap(
                            obj -> ((MenuItem) obj[0]).getId(),
                            obj -> ((Number) obj[1]).intValue()
                    ));

            return menuItems.stream()
                    .filter(item -> (!onlyForCombos || item.getOnlyForCombos()) && isWithinPriceRange(item, priceRange))
                    .sorted(Comparator.comparingInt(
                            (MenuItem item) -> idToCountMap.getOrDefault(item.getId(), 0)
                    ).reversed())
                    .map(item -> genericMapper.toDTO(item, MenuItemDTO.class)) // Using GenericMapper
                    .collect(Collectors.toList());
        }

        Predicate<MenuItem> comboFilter = onlyForCombos ? MenuItem::getOnlyForCombos : item -> !item.getOnlyForCombos();
        Comparator<MenuItem> comparator = getComparator(sortBy != null ? sortBy : Filter.DEFAULT);

        return menuItems.stream()
                .filter(comboFilter)
                .filter(item -> isWithinPriceRange(item, priceRange))
                .sorted(comparator)
                .map(item -> genericMapper.toDTO(item, MenuItemDTO.class)) // Using GenericMapper
                .collect(Collectors.toList());
    }

    private Comparator<MenuItem> getComparator(Filter sortBy) {
        return switch (sortBy) {
            case OLDEST -> Comparator.comparing(
                    MenuItem::getUpdatedDttm, Comparator.nullsLast(Comparator.naturalOrder())
            );
            case NEWEST -> Comparator.comparing(
                    MenuItem::getUpdatedDttm, Comparator.nullsLast(Comparator.naturalOrder())
            ).reversed();
            case RATING -> Comparator.comparing(
                    MenuItem::getRating, Comparator.nullsLast(Comparator.naturalOrder())
            ).reversed();
            default -> Comparator.comparing(
                    MenuItem::getMenuItemName, Comparator.nullsLast(Comparator.naturalOrder())
            );
        };
    }

    private boolean isWithinPriceRange(MenuItem item, String priceRange) {
        if (priceRange == null || priceRange.isEmpty()) {
            return true;
        }

        BigDecimal price = item.getPrice();
        if (price == null) {
            return false;
        }

        if (priceRange.matches("\\d+-\\d+")) {
            String[] parts = priceRange.split("-");
            BigDecimal minPrice = new BigDecimal(parts[0]);
            BigDecimal maxPrice = new BigDecimal(parts[1]);
            return price.compareTo(minPrice) >= 0 && price.compareTo(maxPrice) <= 0;
        } else if (priceRange.matches("above-\\d+")) {
            BigDecimal minPrice = new BigDecimal(priceRange.split("-")[1]);
            return price.compareTo(minPrice) > 0;
        }

        return true;
    }
}
