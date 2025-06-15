package com.justintime.jit.service.impl;

import com.justintime.jit.entity.OrderEntities.OrderActivity;
import com.justintime.jit.entity.OrderEntities.OrderActivity;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.repository.OrderRepo.OrderActivityRepository;
import com.justintime.jit.repository.OrderRepo.OrderActivityRepository;
import com.justintime.jit.service.OrderActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderActivityServiceImpl extends BaseServiceImpl<OrderActivity, Long> implements OrderActivityService {

    @Autowired
    private OrderActivityRepository orderActivityRepository;

    public List<OrderActivity> getAllOrderActivities() {
        return orderActivityRepository.findAll();
    }

    public Optional<OrderActivity> getOrderActivityById(Long id) {
        return orderActivityRepository.findById(id);
    }

    public OrderActivity createOrderActivity(OrderActivity orderActivity) {
        return orderActivityRepository.save(orderActivity);
    }

    public void deleteOrderActivity(Long id) {
        orderActivityRepository.deleteById(id);
    }
}
