package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.SearchResultDTO;
import com.justintime.jit.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jit-api/search")
public class SearchController extends BaseController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SearchResultDTO>>> searchByName(@RequestParam String query) {
        List<SearchResultDTO> results = searchService.searchByName(query);
        return success(results, "Search completed");
    }

    @GetMapping("/menuItem-exists")
    public ResponseEntity<ApiResponse<Void>> checkMenuItemInRestaurant(
            @RequestParam String restaurantCode,
            @RequestParam String menuItemName) {
        try {
            searchService.checkMenuItemExistsInRestaurant(menuItemName, restaurantCode);
            return success(null, "Menu item can be added");
        } catch (IllegalStateException e) {
            return error(e.getMessage(), HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
