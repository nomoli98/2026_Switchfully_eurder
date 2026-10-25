package com.application.eurder_project.service;

import com.application.eurder_project.domain.ItemGroup;
import com.application.eurder_project.domain.Eurder;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.EurderRepository;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.security.UnauthorizedException;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.mapper.ReorderMapper;
import com.application.eurder_project.service.request.PostEurderRequest;
import com.application.eurder_project.service.response.ShippedToday;
import com.application.eurder_project.service.validator.PostEurderValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class EurderService {
    private final EurderRepository eurderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final PostEurderValidator validator;
    private static final Logger logger = LoggerFactory.getLogger(EurderService.class);


    public EurderService(EurderRepository orderRepository,
                         ItemRepository itemRepository,
                         UserRepository userRepository,
                         PostEurderValidator validator) {
        this.eurderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.validator = validator;
    }

    public List<Eurder> getAllEurders() {return eurderRepository.getAll();}

    public Eurder getOneEurderById(UUID id) {
        return eurderRepository.get(id)
                               .orElseThrow(() -> ExistenceException.notInDatabase(Eurder.class.getTypeName()));
    }

    public ShippedToday getOneUserItemsShippedToday(UUID userId) {
        logger.info("Verifying user.");
        User user = userRepository.get(userId)
                                  .orElseThrow(() -> ExistenceException.notInDatabase(User.class.getTypeName()));
        logger.info("User validated.");

        LocalDate today = LocalDate.now();
        List<ItemGroup> itemsShippedToday = eurderRepository.getMyEurdersWithItemsShippedToday(userId)
                                                            .stream()
                                                            .flatMap(eurder -> eurder.getItems().stream())
                                                            .filter(itemGroup -> today.equals(itemGroup.getShippingDate()))
                                                            .toList();
        return new ShippedToday(itemsShippedToday, user.getAddress());
    }

    public List<Eurder> getEurdersByUser(UUID userId) {
        logger.info("Verifying user.");
        User user = userRepository.get(userId)
                                  .orElseThrow(() -> ExistenceException.notInDatabase(User.class.getTypeName()));
        logger.info("User validated.");
        return eurderRepository.getByUser(user);
    }

    public Eurder createEurder(UUID userId, PostEurderRequest request) {
        logger.info("Verifying inputs.");
        validator.isValidRequest(request);
        logger.info("Input validated.");
        User user = userRepository.get(userId)
                                  .orElseThrow(UnauthorizedException::invalidCredentials);
        List<ItemGroup> groups = request.items()
                                        .stream()
                                        .map(r -> new ItemGroup(itemRepository.get(r.itemId())
                                                                              .orElseThrow(() -> ExistenceException.notInDatabase(
                                                                                      Eurder.class.getTypeName())),
                                                                r.amount()))
                                        .toList();

        groups.forEach(g -> g.getItem().setStock(g.getItem().getStock() - g.getAmount()));
        return eurderRepository.save(new Eurder(groups, user));
    }

    public Eurder reeurderAnExistingEurder(UUID userId,UUID eurderId){
        List<Eurder> userEurders = getEurdersByUser(userId);
        Eurder eurderToReeurder = validator.isInEurderList(userEurders, eurderId);
        PostEurderRequest newRequest = ReorderMapper.toEurderRequest(eurderToReeurder);
        return createEurder(userId, newRequest);
    }

}
