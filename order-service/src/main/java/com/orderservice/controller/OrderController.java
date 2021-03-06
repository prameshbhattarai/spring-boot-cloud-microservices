package com.orderservice.controller;

import com.orderservice.entity.Item;
import com.orderservice.entity.Order;
import com.orderservice.entity.User;
import com.orderservice.feignClient.UserClient;
import com.orderservice.service.CartService;
import com.orderservice.service.OrderService;
import com.orderservice.utils.OrderUtilities;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/order")
@Log4j2
public class OrderController {

    private final UserClient userClient;
    private final OrderService orderService;
    private final CartService cartService;

    @Autowired
    public OrderController(UserClient userClient, OrderService orderService, CartService cartService) {
        this.userClient = userClient;
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Order> saveOrder(@PathVariable("userId") Long userId,
                                           @RequestHeader(value = "Cookie") String cartId) {
        List<Item> cart = cartService.getAllItemsFromCart(cartId);
        User user = userClient.getUserById(userId);

        if (cart != null && user != null) {
            Order order = this.createOrder(cart, user);
            try {
                orderService.saveOrder(order);
                cartService.deleteCart(cartId);
                log.info("Successfully saved order for user: {}", user.getUserName());
                return new ResponseEntity<>(order, HttpStatus.CREATED);
            } catch (Exception ex) {
                log.error("Unable to save order for user: {}", user.getUserName(), ex);
                ex.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        if (cart == null) log.error("Unable to find cart by id: {}", cartId);
        if (user == null) log.error("Unable to find user by id: {}", userId);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private Order createOrder(List<Item> cart, User user) {
        Order order = new Order();
        order.setItems(cart);
        order.setUser(user);
        order.setTotal(OrderUtilities.countTotalPrice(cart));
        order.setOrderedDate(LocalDate.now());
        order.setStatus("PAYMENT_EXPECTED");
        return order;
    }
}
