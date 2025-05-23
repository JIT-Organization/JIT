package com.justintime.jit.util.filter;

import com.justintime.jit.dto.ComboDTO;
import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.dto.TimeIntervalDTO;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.Cook;
import com.justintime.jit.entity.Enums.Sort;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.TimeInterval;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import com.justintime.jit.util.mapper.GenericMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.justintime.jit.service.impl.MenuItemServiceImpl.convertTimeIntervals;

@Component
public class FilterItemsUtil {

    public static <T extends FilterableItem, DTO> List<DTO> filterAndSortItems(
            List<T> items,
            Long restaurantId,
            Sort sortBy,
            String priceRange,
            String category,
            Boolean onlyVeg,
            Boolean onlyForCombos,
            OrderItemRepository orderItemRepository,
            GenericMapper<T, DTO> genericMapper,
            Class<DTO> dtoClass) {

        if (onlyVeg != null) {
            items = items.stream()
                    .filter(item -> onlyVeg.equals(item.getOnlyVeg()))
                    .collect(Collectors.toList());
        }

        if (category != null && !category.trim().isEmpty()) {
            items = items.stream()
                    .filter(item -> {
                        if (item instanceof MenuItem m) {
                            return m.getCategorySet().stream()
                                    .anyMatch(c -> category.equalsIgnoreCase(c.getCategoryName()));
                        } else if (item instanceof Combo c) {
                            return c.getCategorySet().stream()
                                    .anyMatch(cat -> category.equalsIgnoreCase(cat.getCategoryName()));
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }

        if (Sort.POPULARITY.equals(sortBy)) {
            List<Long> itemIds = items.stream()
                    .map(FilterableItem::getId)
                    .collect(Collectors.toList());

            List<Object[]> result = new ArrayList<>();
            if (dtoClass.equals(MenuItemDTO.class)) {
                result = orderItemRepository.findMenuItemsWithOrderCount(restaurantId, itemIds);
            } else if (dtoClass.equals(ComboDTO.class)) {
                result = orderItemRepository.findCombosWithOrderCount(restaurantId, itemIds);
            }

            final var idToCountMap = result.stream()
                    .collect(Collectors.toMap(
                            obj -> ((T) obj[0]).getId(),
                            obj -> ((Number) obj[1]).intValue()
                    ));

            return items.stream()
                    .filter(item -> (!onlyForCombos || item.getOnlyForCombos()) && isWithinPriceRange(item, priceRange))
                    .sorted(Comparator.comparing(item -> idToCountMap.getOrDefault(item.getId(), 0), Comparator.reverseOrder()))
                    .map(item -> convertToDTO(item, genericMapper, dtoClass))
                    .collect(Collectors.toList());
        }
        Predicate<T> comboFilter = onlyForCombos ? FilterableItem::getOnlyForCombos : item -> !item.getOnlyForCombos();
        Comparator<T> comparator = getComparator(sortBy != null ? sortBy : Sort.DEFAULT);

        return items.stream()
                .filter(comboFilter)
                .filter(item -> isWithinPriceRange(item, priceRange))
                .sorted(comparator)
                .map(item -> convertToDTO(item, genericMapper, dtoClass))
                .collect(Collectors.toList());

    }

    private static <T extends FilterableItem, DTO> DTO convertToDTO(T item, GenericMapper<T, DTO> genericMapper, Class<DTO> dtoClass) {
        DTO dto = genericMapper.toDto(item);

        if (dto instanceof ComboDTO comboDTO && item instanceof Combo combo) {
            comboDTO.setCategorySet(
                    combo.getCategorySet().stream()
                            .map(Category::getCategoryName)
                            .collect(Collectors.toSet())
            );
            comboDTO.setTimeIntervalSet(convertTimeIntervals(combo.getTimeIntervalSet())); // Add conversion
        } else if (dto instanceof MenuItemDTO menuItemDTO && item instanceof MenuItem menuItem) {
            menuItemDTO.setCategorySet(
                    menuItem.getCategorySet().stream()
                            .map(Category::getCategoryName)
                            .collect(Collectors.toSet())
            );
            menuItemDTO.setTimeIntervalSet(convertTimeIntervals(menuItem.getTimeIntervalSet())); // Add conversion
        }
        return dto;
    }


    private static <T extends FilterableItem> Comparator<T> getComparator(Sort sortBy) {
        return switch (sortBy) {
            case OLDEST -> Comparator.comparing((T item) -> item.getUpdatedDttm(), Comparator.nullsLast(Comparator.naturalOrder()));
            case NEWEST -> Comparator.comparing((T item) -> item.getUpdatedDttm(), Comparator.nullsLast(Comparator.naturalOrder())).reversed();
            case RATING -> Comparator.comparing((T item) -> item.getRating(), Comparator.nullsLast(Comparator.naturalOrder())).reversed();
            default -> Comparator.comparing((T item) -> item.getName(), Comparator.nullsLast(Comparator.naturalOrder()));
        };
    }

    private static boolean isWithinPriceRange(FilterableItem item, String priceRange) {
        if (priceRange == null || priceRange.isEmpty()) {
            return true;
        }

        BigDecimal price = item.getPrice();
        if (price == null) {
            return false;
        }

        if (priceRange.matches("\\d+-\\d+")) {
            String[] parts = priceRange.split("-");
            BigDecimal minPrice = new BigDecimal(parts[0].trim());
            BigDecimal maxPrice = new BigDecimal(parts[1].trim());
            return price.compareTo(minPrice) >= 0 && price.compareTo(maxPrice) <= 0;
        } else if (priceRange.matches("above-\\d+")) {
            BigDecimal minPrice = new BigDecimal(priceRange.split("-")[1].trim());
            return price.compareTo(minPrice) > 0;
        }
        return true;
    }
}
