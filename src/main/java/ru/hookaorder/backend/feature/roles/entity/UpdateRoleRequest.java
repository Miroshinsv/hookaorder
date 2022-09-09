package ru.hookaorder.backend.feature.roles.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class UpdateRoleRequest {
    @JsonProperty(value = "user_id")
    private final Long userId;
    @JsonProperty(value = "role_ids")
    private final List<Long> roleIds;
}
