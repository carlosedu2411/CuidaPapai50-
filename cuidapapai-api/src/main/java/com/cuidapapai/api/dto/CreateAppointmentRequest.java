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
public class CreateAppointmentRequest {
    private String title;
    private String subtitle;
    private String date;

    @JsonProperty("event_datetime")
    private OffsetDateTime eventDatetime;

    @JsonProperty("color_hex")
    private String colorHex;
}
