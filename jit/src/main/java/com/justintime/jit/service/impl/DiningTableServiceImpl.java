package com.justintime.jit.service.impl;

import com.justintime.jit.dto.DiningTableDTO;
import com.justintime.jit.entity.DiningTable;
import com.justintime.jit.entity.Reservation;
import com.justintime.jit.entity.Restaurant;
import com.justintime.jit.repository.DiningTableRepository;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.service.DiningTableService;
import com.justintime.jit.util.CommonServiceImplUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DiningTableServiceImpl implements DiningTableService {
    @Autowired
    private DiningTableRepository diningTableRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CommonServiceImplUtil commonServiceImplUtil;

    private final GenericMapper<DiningTable, DiningTableDTO> mapper = MapperFactory.getMapper(DiningTable.class, DiningTableDTO.class);

    @Override
    public List<DiningTableDTO> getDiningTablesByRestaurantCode(String restaurantCode) {
        List<DiningTable> diningTables = diningTableRepository.findByRestaurantCode(restaurantCode);
        return diningTables.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public DiningTableDTO patchUpdateTablesByRestaurantCode(String restaurantCode, DiningTableDTO dto, HashSet<String> propertiesToBeUpdated) {
        DiningTable existingDiningTable = diningTableRepository.findByRestaurantCodeAndTableNumber(restaurantCode, dto.getTableNumber());
        if(Objects.nonNull(existingDiningTable)) {
            DiningTable patchedDiningTable = mapper.toEntity(dto);
            commonServiceImplUtil.copySelectedProperties(patchedDiningTable, existingDiningTable, propertiesToBeUpdated);
            existingDiningTable.setUpdatedDttm(LocalDateTime.now());
            diningTableRepository.save(existingDiningTable);
        }
        return mapper.toDto(existingDiningTable);
    }

    @Override
    public DiningTableDTO createTable(String restaurantCode, DiningTableDTO dto) {
        DiningTable table = mapper.toEntity(dto);
        // TODO Add validations for the fields entering into the db
        Restaurant restaurant = restaurantRepository.findByRestaurantCode(restaurantCode);
        table.setRestaurant(restaurant);
        diningTableRepository.save(table);
        return dto;
    }

    @Override
    @Transactional
    public void deleteTable(String restaurantCode, String tableNumber) {
        DiningTable table = diningTableRepository.findByRestaurantCodeAndTableNumber(restaurantCode, tableNumber);
        for (Reservation reservation : table.getReservationSet()) {
            reservation.getDiningTableSet().remove(table);
        }
        table.getReservationSet().clear();
        diningTableRepository.deleteTableByRestaurantCodeAndTableNumber(restaurantCode, tableNumber);
    }
}
