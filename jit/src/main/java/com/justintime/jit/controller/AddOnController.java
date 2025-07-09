package com.justintime.jit.controller;

import com.justintime.jit.dto.AddOnDTO;
import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.entity.Enums.Sort;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.exception.ImageSizeLimitExceededException;
import com.justintime.jit.service.AddOnService;
import com.justintime.jit.service.JwtService;
import com.justintime.jit.service.MenuItemService;
import com.justintime.jit.util.ImageValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/addons")
public class AddOnController extends BaseController{

    @Autowired
    private AddOnService addOnService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/{restaurantCode}")
    public ResponseEntity<ApiResponse<List<AddOnDTO>>> getAddOnsForRestaurant(@PathVariable String restaurantCode) {
        return success(addOnService.getAllAddOnsForRestaurant(restaurantCode));
    }

    @GetMapping("/{restaurantCode}/{menuItemName}")
    public ResponseEntity<ApiResponse<List<AddOnDTO>>> getAddOnsForMenuItem(@PathVariable String restaurantCode, @PathVariable String menuItemName) {
        return success(addOnService.getAllAddOnsForMenuItem(restaurantCode, menuItemName));
    }

    @GetMapping("/{restaurantCode}/combo/{comboName}")
    public ResponseEntity<ApiResponse<List<AddOnDTO>>> getAddOnsForCombo(@PathVariable String restaurantCode, @PathVariable String comboName) {
        return success(addOnService.getAllAddOnsForCombo(restaurantCode, comboName));
    }

    @GetMapping("/{restaurantCode}/order/{orderNumber}/item/{orderItemName}")
    public ResponseEntity<ApiResponse<List<AddOnDTO>>> getAddOnsForOrderItem(
            @PathVariable String restaurantCode,
            @PathVariable String orderNumber,
            @PathVariable String orderItemName) {
        return success(addOnService.getAllAddOnsForOrderItem(restaurantCode, orderNumber, orderItemName));
    }

    @PostMapping("/{restaurantCode}")
    public ResponseEntity<ApiResponse<Void>> createAddOn(@PathVariable String restaurantCode, @RequestBody AddOnDTO addOnDTO) {
        addOnService.createAddOn(restaurantCode, addOnDTO);
        return success(null, "Add-On created successfully");
    }

    @PutMapping("/{restaurantCode}")
    public ResponseEntity<ApiResponse<AddOnDTO>> updateAddOn(@PathVariable String restaurantCode, @RequestBody AddOnDTO addOnDTO) {
        return success(addOnService.updateAddOn(restaurantCode, addOnDTO), "Add-On updated successfully");
    }

    @PatchMapping("/{restaurantCode}")
    public ResponseEntity<ApiResponse<AddOnDTO>> patchUpdateAddOn(@PathVariable String restaurantCode, @RequestBody PatchRequest<AddOnDTO> payload) {
        return success(addOnService.patchAddOn(restaurantCode, payload.getDto(), payload.getPropertiesToBeUpdated()));
    }

    @DeleteMapping("/{restaurantCode}/{label}")
    public ResponseEntity<ApiResponse<Void>> deleteAddOn(@PathVariable String restaurantCode, @PathVariable String label) {
        addOnService.deleteAddOn(restaurantCode, label);
        return success(null, "Add-On deleted successfully");
    }

}
