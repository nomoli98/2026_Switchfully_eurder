package com.application.eurder_project.service.mapper;

import com.application.eurder_project.domain.Eurder;
import com.application.eurder_project.domain.ItemGroup;
import com.application.eurder_project.service.request.PostEurderRequest;
import com.application.eurder_project.service.request.PostItemGroupRequest;

public class ReorderMapper {

    private ReorderMapper() {
    }

    public static PostEurderRequest toEurderRequest(Eurder eurder) {
        return new PostEurderRequest(
                eurder.getItems().stream()
                      .map(ReorderMapper::toItemGroupRequest)
                      .toList());
    }

    public static PostItemGroupRequest toItemGroupRequest(ItemGroup itemGroup) {
        return new PostItemGroupRequest(
                itemGroup.getItem().getId(),
                itemGroup.getAmount());
    }
}