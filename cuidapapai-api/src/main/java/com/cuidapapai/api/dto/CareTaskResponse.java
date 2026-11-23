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
public class CareTaskResponse {
    private String id;
    private String category;
    private String title;
    private String subtitle;

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    @JsonProperty("color_hex")
    private String colorHex;
}
