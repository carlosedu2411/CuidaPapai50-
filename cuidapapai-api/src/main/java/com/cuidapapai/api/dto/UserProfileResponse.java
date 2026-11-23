package com.cuidapapai.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private String id;
    private String name;

    @JsonProperty("short_name")
    private String shortName;

    private String role;
    private UserPreferencesDto preferences;
}
