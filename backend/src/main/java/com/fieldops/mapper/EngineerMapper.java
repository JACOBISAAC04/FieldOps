package com.fieldops.mapper;

import com.fieldops.dto.EngineerResponse;
import com.fieldops.entity.Engineer;

public class EngineerMapper {

    private EngineerMapper() {
    }

    public static EngineerResponse toResponse(Engineer engineer) {
        EngineerResponse response = new EngineerResponse();

        response.setId(engineer.getId());
        response.setUserId(engineer.getUser().getId());
        response.setName(engineer.getUser().getName());
        response.setEmail(engineer.getUser().getEmail());
        response.setSpecialization(engineer.getSpecialization());
        response.setLocation(engineer.getLocation());
        response.setAvailability(engineer.getAvailability());

        return response;
    }
}