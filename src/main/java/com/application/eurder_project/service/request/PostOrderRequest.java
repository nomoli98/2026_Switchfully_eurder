package com.application.eurder_project.service.request;

import java.util.List;

public record PostOrderRequest(List<PostItemGroupRequest> items) {}
