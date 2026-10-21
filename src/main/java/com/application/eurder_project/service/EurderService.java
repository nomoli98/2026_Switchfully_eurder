package com.application.eurder_project.service;

import com.application.eurder_project.domain.ItemGroup;
import com.application.eurder_project.domain.Eurder;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.EurderRepository;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.request.PostEurderRequest;
import com.application.eurder_project.service.validator.PostEurderValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EurderService {
    private final EurderRepository eurderRepository;
    private final ItemRepository itemRepository;
    private final PostEurderValidator validator;
    private static final Logger logger = LoggerFactory.getLogger(EurderService.class);


    public EurderService(EurderRepository eurderRepository,
                         ItemRepository itemRepository,
                         PostEurderValidator validator) {
        this.eurderRepository = eurderRepository;
        this.itemRepository = itemRepository;
        this.validator = validator;
    }

    public List<Eurder> getAllEurders() {return eurderRepository.getAll();}

    public Eurder getOneEurderById(UUID id) {
        return eurderRepository.get(id)
                               .orElseThrow(() -> ExistenceException.NotInDatabase(Eurder.class.getTypeName()));
    }

    public List<Eurder> getEurdersByUser(UUID userId) {
        logger.info("Verifying user.");
        User user = validator.isValidUser(userId);
        logger.info("User validated.");
        return eurderRepository.getByUser(user);
    }

    public Eurder createEurder(UUID userId, PostEurderRequest request) {
        logger.info("Verifying inputs.");
        validator.isValidRequest(request);
        User user = validator.isValidUser(userId);
        logger.info("Input validated.");

        List<ItemGroup> groups = request.items()
                                        .stream()
                                        .map(r -> new ItemGroup(itemRepository.get(r.itemId())
                                                                              .orElseThrow(() -> ExistenceException.NotInDatabase(
                                                                                      Eurder.class.getTypeName())),
                                                                r.amount()))
                                        .toList();

        groups.forEach(g -> g.getItem().setStock(g.getItem().getStock() - g.getAmount()));
        return eurderRepository.save(new Eurder(groups, user));
    }
}
