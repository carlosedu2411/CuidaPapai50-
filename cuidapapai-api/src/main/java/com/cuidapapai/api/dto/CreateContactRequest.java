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
public class CreateContactRequest {
    private String name;
    private String relation;
    private String initials;

    @JsonProperty("is_professional")
    private Boolean isProfessional;

    @JsonProperty("is_available_today")
    private Boolean isAvailableToday;

    private String phone;

    @JsonProperty("avatar_color_hex")
    private String avatarColorHex;
}
