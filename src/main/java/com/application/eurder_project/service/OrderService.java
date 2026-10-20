package com.application.eurder_project.service;

import com.application.eurder_project.domain.Order;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.OrderRepository;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.request.PostOrderRequest;
import com.application.eurder_project.service.validator.OrderValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final OrderValidator validator;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);


    public OrderService(OrderRepository orderRepository,
                        ItemRepository itemRepository,
                        UserRepository userRepository,
                        OrderValidator validator) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.validator = validator;
    }

    public List<Order> getAllItems() {return orderRepository.getAll();}

    public Order createOrder(PostOrderRequest request){
        logger.info("Verifying inputs.");
        validator.isValidRequest(request);
        logger.info("Input validated.");
        return orderRepository.save(new Order(request.items(), request.user()));
    }
}
