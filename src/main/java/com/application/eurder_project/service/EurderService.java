package com.application.eurder_project.service;

import com.application.eurder_project.domain.Eurder;
import com.application.eurder_project.domain.Item;
import com.application.eurder_project.domain.ItemGroup;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.EurderRepository;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.security.UnauthorizedException;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.mapper.ReorderMapper;
import com.application.eurder_project.service.request.PostEurderRequest;
import com.application.eurder_project.service.request.PostItemGroupRequest;
import com.application.eurder_project.service.response.ShippedToday;
import com.application.eurder_project.service.validator.EurderValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EurderService {
    private static final Logger logger = LoggerFactory.getLogger(EurderService.class);

    private final EurderRepository eurderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public EurderService(EurderRepository eurderRepository,
                         ItemRepository itemRepository,
                         UserRepository userRepository) {
        this.eurderRepository = eurderRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public List<Eurder> getAllEurders() {
        return eurderRepository.getAll();
    }

    public List<Eurder> getEurdersByUser(UUID userId) {
        findUser(userId);
        return eurderRepository.getByUserId(userId);
    }

    /** Customer view: the item groups of this user's eurders that ship today. */
    public ShippedToday getMyItemsShippedToday(UUID userId) {
        User user = findUser(userId);
        LocalDate today = LocalDate.now();
        List<ItemGroup> itemsShippedToday = eurderRepository.getByUserId(userId)
                                                            .stream()
                                                            .flatMap(eurder -> eurder.getItemGroupsShippingOn(today).stream())
                                                            .toList();
        return new ShippedToday(itemsShippedToday, user.getAddress());
    }

    /** Admin view: per eurder, the item groups that ship today and where to ship them. */
    public List<ShippedToday> getAllItemsShippedToday() {
        LocalDate today = LocalDate.now();
        return eurderRepository.getAll()
                               .stream()
                               .map(eurder -> new ShippedToday(eurder.getItemGroupsShippingOn(today),
                                                               eurder.getUser().getAddress()))
                               .filter(shippedToday -> !shippedToday.items().isEmpty())
                               .toList();
    }

    public Eurder createEurder(UUID userId, PostEurderRequest request) {
        logger.info("Verifying inputs.");
        EurderValidator.validateNewEurder(request);
        logger.info("Input validated.");
        User user = userRepository.get(userId)
                                  .orElseThrow(UnauthorizedException::invalidCredentials);
        List<ItemGroup> groups = createItemGroups(request.items());
        return eurderRepository.save(new Eurder(groups, user));
    }

    public Eurder reeurderAnExistingEurder(UUID userId, UUID eurderId) {
        Eurder eurderToReeurder = getEurdersByUser(userId)
                .stream()
                .filter(eurder -> eurder.getId().equals(eurderId))
                .findAny()
                .orElseThrow(() -> ExistenceException.notInDatabase("eurder"));
        return createEurder(userId, ReorderMapper.toEurderRequest(eurderToReeurder));
    }

    private List<ItemGroup> createItemGroups(List<PostItemGroupRequest> requests) {
        // Every item must exist before any stock is changed.
        requests.forEach(request -> findItem(request.itemId()));

        // One group after the other: the shipping date of a group depends on the stock left by the previous groups.
        List<ItemGroup> groups = new ArrayList<>();
        for (PostItemGroupRequest request : requests) {
            Item item = findItem(request.itemId());
            groups.add(new ItemGroup(item, request.amount()));
            item.removeStock(request.amount());
        }
        return groups;
    }

    private User findUser(UUID userId) {
        return userRepository.get(userId)
                             .orElseThrow(() -> ExistenceException.notInDatabase("user"));
    }

    private Item findItem(UUID itemId) {
        return itemRepository.get(itemId)
                             .orElseThrow(() -> ExistenceException.notInDatabase("item"));
    }
}
