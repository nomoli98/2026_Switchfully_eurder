package com.application.eurder_project.security;

import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.exception.ForbiddenException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SecurityService {
    private final UserRepository repository;

    public SecurityService(UserRepository repository) {
        this.repository = repository;
    }

    public void validateAuthorization(UUID id, Permission permissionNeeded) {
        if (id == null) throw UnauthorizedException.invalidCredentials();
        User user = repository.get(id).orElseThrow(UnauthorizedException::invalidCredentials);
        if (!user.hasPermission(permissionNeeded)) throw ForbiddenException.invalidPermission();
    }
}
