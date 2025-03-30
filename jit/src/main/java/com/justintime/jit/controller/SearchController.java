package com.justintime.jit.controller;

import com.justintime.jit.dto.SearchResultDTO;
import com.justintime.jit.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jit-api/search")
public class SearchController {
    //this is a search controller helper class
    @Autowired
    private SearchService searchService;

    @GetMapping
    public List<SearchResultDTO> searchByName(@RequestParam String query) {
        return searchService.searchByName(query);
    }
}
