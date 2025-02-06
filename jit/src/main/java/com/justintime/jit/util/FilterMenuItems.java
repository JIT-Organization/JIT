package com.justintime.jit.util;

import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.entity.Enums.Filter;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilterMenuItems {

        public List<MenuItem> filterAndSortMenuItems(
                List<MenuItem> menuItems,
                Long addressId,
                Filter sortBy,
                String priceRange,
                boolean onlyForCombos,
                OrderItemRepository orderItemRepository) {

            if (Filter.POPULARITY.equals(sortBy)) {
                // Extract menu item IDs from the provided list
                List<Long> menuItemIds = menuItems.stream()
                        .map(MenuItem::getId)
                        .collect(Collectors.toList());

                // Fetch order counts for the specific menu items and address
                List<Object[]> result = orderItemRepository.findMenuItemsWithOrderCount(addressId, menuItemIds);

                // Map menu item ID to order count
                Map<Long, Integer> idToCountMap = result.stream()
                        .collect(Collectors.toMap(
                                obj -> ((MenuItem) obj[0]).getId(), // Get the id of MenuItem
                                obj -> ((Number) obj[1]).intValue() // Safely convert order count to Integer
                        ));

                // Process original menuItems to include all, using counts from the map (default to 0)
                return menuItems.stream()
                        .filter(item -> (!onlyForCombos || item.getOnlyForCombos()) && isWithinPriceRange(item, priceRange))
                        .sorted(Comparator.comparingInt(
                                (MenuItem item) -> idToCountMap.getOrDefault(item.getId(), 0)
                        ).reversed())
                        .collect(Collectors.toList());
            }

            // Existing logic for other filters
            Predicate<MenuItem> comboFilter = onlyForCombos ? MenuItem::getOnlyForCombos : item -> !item.getOnlyForCombos();
            Comparator<MenuItem> comparator = getComparator(sortBy != null ? sortBy : Filter.DEFAULT);

            return menuItems.stream()
                    .filter(comboFilter)
                    .filter(item -> isWithinPriceRange(item, priceRange))
                    .sorted(comparator)
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
                        item -> item.getFood().getFoodName(), Comparator.nullsLast(Comparator.naturalOrder())
                );
            };
        }

        private boolean isWithinPriceRange(MenuItem item, String priceRange) {
            if (priceRange == null || priceRange.isEmpty()) {
                return true;
            }

            BigDecimal price = item.getPrice();
            if (price == null) {
                return false; // or true, depending on your business logic
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
