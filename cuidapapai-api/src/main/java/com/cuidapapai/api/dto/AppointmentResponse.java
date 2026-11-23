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
public class AppointmentResponse {
    private String id;
    private String date;

    @JsonProperty("day_of_week_short")
    private String dayOfWeekShort;

    @JsonProperty("day_number")
    private String dayNumber;

    private String time;
    private String title;
    private String subtitle;

    @JsonProperty("color_hex")
    private String colorHex;
}
