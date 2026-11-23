package com.cuidapapai.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToggleCareTaskRequest {
    @JsonProperty("is_completed")
    private Boolean isCompleted;

    private String value;

    @JsonProperty("completed_at")
    private OffsetDateTime completedAt;
}
