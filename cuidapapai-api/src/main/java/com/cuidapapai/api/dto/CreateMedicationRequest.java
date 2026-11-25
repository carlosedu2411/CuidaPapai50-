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
public class CreateMedicationRequest {
    private String name;
    private String dosage;
    private String time;

    @JsonProperty("scheduled_time")
    private String scheduledTime;

    private String instructions;

    @JsonProperty("scheduled_date")
    private LocalDate scheduledDate;
}
