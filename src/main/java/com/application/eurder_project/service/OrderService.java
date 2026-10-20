package com.application.eurder_project.service;

import com.application.eurder_project.domain.ItemGroup;
import com.application.eurder_project.domain.Order;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.OrderRepository;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.security.UnauthorizedException;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.request.PostOrderRequest;
import com.application.eurder_project.service.validator.PostOrderValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final PostOrderValidator validator;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);


    public OrderService(OrderRepository orderRepository,
                        ItemRepository itemRepository,
                        UserRepository userRepository,
                        PostOrderValidator validator) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.validator = validator;
    }

    public List<Order> getAllOrders() {return orderRepository.getAll();}

    public Order getOneOrderById(UUID id) {
        return orderRepository.get(id)
                              .orElseThrow(() -> ExistenceException.NotInDatabase(Order.class.getTypeName()));
    }

    public List<Order> getOrdersByUser(UUID userId) {
        logger.info("Verifying user.");
        User user = userRepository.get(userId)
                                  .orElseThrow(() -> ExistenceException.NotInDatabase(
                                          User.class.getTypeName()));
        logger.info("User validated.");
        return orderRepository.getByUser(user);
    }

    public Order createOrder(UUID userId, PostOrderRequest request) {
        logger.info("Verifying inputs.");
        validator.isValidRequest(request);
        logger.info("Input validated.");
        User user = userRepository.get(userId)
                                  .orElseThrow(UnauthorizedException::invalidCredentials);
        List<ItemGroup> groups = request.items()
                                        .stream()
                                        .map(r -> new ItemGroup(itemRepository.get(r.itemId())
                                                                              .orElseThrow(() -> ExistenceException.NotInDatabase(
                                                                                      Order.class.getTypeName())),
                                                                r.amount()))
                                        .toList();

        groups.forEach(g -> g.getItem().setStock(g.getItem().getStock() - g.getAmount()));
        return orderRepository.save(new Order(groups, user));
    }
}
