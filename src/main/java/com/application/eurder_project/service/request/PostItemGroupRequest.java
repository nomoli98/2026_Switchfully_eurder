package com.application.eurder_project.service.request;

import java.util.UUID;

public record PostItemGroupRequest(UUID itemId, int amount) {}
