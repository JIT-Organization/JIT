package com.justintime.jit.service.impl;

import com.justintime.jit.entity.Enums.Filter;
import com.justintime.jit.entity.Food;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import com.justintime.jit.service.MenuItemService;
import com.justintime.jit.repository.MenuItemRepository;
import com.justintime.jit.entity.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
    public class MenuItemServiceImpl extends BaseServiceImpl<MenuItem,Long> implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    private final OrderItemRepository orderItemRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository, OrderItemRepository orderItemRepository) {
        this.menuItemRepository = menuItemRepository;
        this.orderItemRepository = orderItemRepository;
    }

        public List<MenuItem> getAllMenuItems() {
            return menuItemRepository.findAll();
        }

        public MenuItem addMenuItem(MenuItem menuItem) {
            menuItem.setUpdatedDttm(LocalDateTime.now());
            return menuItemRepository.save(menuItem);
        }

        public MenuItem updateMenuItem(Long id, MenuItem updatedItem) {
            MenuItem existingItem = menuItemRepository.findById(id).orElseThrow(() -> new RuntimeException("MenuItem not found"));
            existingItem.setRestaurant(updatedItem.getRestaurant());
            existingItem.setFood(updatedItem.getFood());
            existingItem.setDescription(updatedItem.getDescription());
            existingItem.setPrice(updatedItem.getPrice());
            existingItem.setOfferPrice(updatedItem.getOfferPrice());
            existingItem.setStock(updatedItem.getStock());
            existingItem.setCount(updatedItem.getCount());
            existingItem.setAddress(updatedItem.getAddress());
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

    public List<MenuItem> getMenuItemsByAddressId(Long addressId, Filter sortBy, String priceRange, boolean onlyForCombos) {
        List<MenuItem> menuItems = menuItemRepository.findByAddressId(addressId);

        if (Filter.POPULARITY.equals(sortBy)) { // Prevent NullPointerException
            List<Object[]> result = orderItemRepository.findMenuItemsWithOrderCount(addressId);

            // Convert List<Object[]> to Map<MenuItem, Integer>
            Map<MenuItem, Integer> orderCounts = result.stream()
                    .collect(Collectors.toMap(
                            obj -> (MenuItem) obj[0],   // MenuItem
                            obj -> ((Number) obj[1]).intValue() // Safely convert COUNT result
                    ));

            // Apply filters and sort by popularity
            return orderCounts.keySet().stream()
                    .filter(item -> (!onlyForCombos || item.getOnlyForCombos()) && isWithinPriceRange(item, priceRange)) // Apply both filters
                    .sorted(Comparator.comparingInt(orderCounts::get).reversed()) // Sort by order count descending
                    .collect(Collectors.toList());
        }

        // Apply combo filtering
        Predicate<MenuItem> comboFilter = onlyForCombos ? MenuItem::getOnlyForCombos : item -> !item.getOnlyForCombos();

        // Get appropriate comparator
        Comparator<MenuItem> comparator = getComparator(sortBy != null ? sortBy : Filter.DEFAULT);

        // Filter, sort, and return
        return menuItems.stream()
                .filter(comboFilter)
                .filter(item -> isWithinPriceRange(item, priceRange))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private Comparator<MenuItem> getComparator(Filter sortBy) {
        return switch (sortBy) {
            case OLDEST -> Comparator.comparing(MenuItem::getUpdatedDttm, Comparator.nullsLast(Comparator.naturalOrder()));
            case NEWEST -> Comparator.comparing(MenuItem::getUpdatedDttm, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
            case RATING -> Comparator.comparing(MenuItem::getRating, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
            default -> Comparator.comparing(item -> item.getFood().getFoodName(), Comparator.nullsLast(Comparator.naturalOrder()));
        };
    }


        // Unified Price Filtering Logic
        private boolean isWithinPriceRange(MenuItem item, String priceRange) {
            if (priceRange == null || priceRange.isEmpty()) {
                return true; // No filtering applied
            }

            BigDecimal price = item.getPrice();

            if (priceRange.matches("\\d+-\\d+")) { // Matches "100-500" format
                String[] parts = priceRange.split("-");
                BigDecimal minPrice = new BigDecimal(parts[0]);
                BigDecimal maxPrice = new BigDecimal(parts[1]);
                return price.compareTo(minPrice) >= 0 && price.compareTo(maxPrice) <= 0;
            }
            else if (priceRange.matches("above-\\d+")) { // Matches "above-500"
                BigDecimal minPrice = new BigDecimal(priceRange.split("-")[1]);
                return price.compareTo(minPrice) > 0;
            }

            return true; // Default to including all if format is invalid
        }
    }


