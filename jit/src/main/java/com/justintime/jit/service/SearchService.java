package com.justintime.jit.service;

import com.justintime.jit.dto.SearchResultDTO;
import java.util.List;

public interface SearchService {
    List<SearchResultDTO> searchByName(String query);
    void checkMenuItemExistsInRestaurant(String restaurantCode, String menuItemName);
}
