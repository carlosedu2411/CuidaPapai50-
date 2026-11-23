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
public class MedicationResponse {
    private String id;
    private String name;
    private String dosage;
    private String time;
    private String instructions;

    @JsonProperty("is_taken")
    private Boolean isTaken;
}
