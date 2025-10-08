package com.justintime.jit.controller;

import com.justintime.jit.dto.AddOnDTO;
import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.service.AddOnService;
import com.justintime.jit.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/addons")
public class AddOnController extends BaseController{

    @Autowired
    private AddOnService addOnService;

    @Autowired
    private JwtService jwtService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddOnDTO>>> getAddOnsForRestaurant() {
        return success(addOnService.getAllAddOnsForRestaurant());
    }

    @GetMapping("/{menuItemName}")
    public ResponseEntity<ApiResponse<List<AddOnDTO>>> getAddOnsForMenuItem(@PathVariable String menuItemName) {
        return success(addOnService.getAllAddOnsForMenuItem(menuItemName));
    }

    @GetMapping("/combo/{comboName}")
    public ResponseEntity<ApiResponse<List<AddOnDTO>>> getAddOnsForCombo(@PathVariable String comboName) {
        return success(addOnService.getAllAddOnsForCombo(comboName));
    }

    @GetMapping("/order/{orderNumber}/item/{orderItemName}")
    public ResponseEntity<ApiResponse<List<AddOnDTO>>> getAddOnsForOrderItem(
            @PathVariable String orderNumber,
            @PathVariable String orderItemName) {
        return success(addOnService.getAllAddOnsForOrderItem(orderNumber, orderItemName));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createAddOn(@RequestBody AddOnDTO addOnDTO) {
        addOnService.createAddOn(addOnDTO);
        return success(null, "Add-On created successfully");
    }

    @PutMapping
    public ResponseEntity<ApiResponse<AddOnDTO>> updateAddOn(@RequestBody AddOnDTO addOnDTO) {
        return success(addOnService.updateAddOn(addOnDTO), "Add-On updated successfully");
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<AddOnDTO>> patchUpdateAddOn(@RequestBody PatchRequest<AddOnDTO> payload) {
        return success(addOnService.patchAddOn(payload.getDto(), payload.getPropertiesToBeUpdated()));
    }

    @DeleteMapping("/{label}")
    public ResponseEntity<ApiResponse<Void>> deleteAddOn(@PathVariable String label) {
        addOnService.deleteAddOn(label);
        return success(null, "Add-On deleted successfully");
    }

}
