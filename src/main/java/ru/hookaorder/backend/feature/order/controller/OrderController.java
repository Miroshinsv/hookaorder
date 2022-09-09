package ru.hookaorder.backend.feature.order.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hookaorder.backend.feature.order.entity.OrderEntity;
import ru.hookaorder.backend.feature.order.repository.OrderRepository;

import java.util.List;

@RestController
@RequestMapping(value = "/order")
@Api(description = "Контроллер заказов")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/get/{id}")
    @ApiOperation("Получение заказа по id")
    ResponseEntity<OrderEntity> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get/all")
    @ApiOperation("Получение всех заказов")
    ResponseEntity<List<OrderEntity>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @PostMapping("/create")
    @ApiOperation("Создание заказа")
    ResponseEntity<OrderEntity> createOrder(@RequestBody OrderEntity orderEntity) {
        return ResponseEntity.ok(orderRepository.save(orderEntity));
    }

    @PostMapping("/update")
    @ApiOperation("Обновление заказа по id")
    ResponseEntity<OrderEntity> updateOrder(@PathVariable Long id) {
        return orderRepository.findById(id).map((val) -> ResponseEntity.ok(orderRepository.save(val)))
                .orElse(ResponseEntity.badRequest().build());
    }
}
