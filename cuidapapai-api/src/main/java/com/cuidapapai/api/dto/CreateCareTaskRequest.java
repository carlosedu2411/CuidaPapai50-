package com.cuidapapai.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCareTaskRequest {
    private String category;
    private String title;
    private String subtitle;

    @JsonProperty("color_hex")
    private String colorHex;

    @JsonProperty("task_date")
    private LocalDate taskDate;
}
