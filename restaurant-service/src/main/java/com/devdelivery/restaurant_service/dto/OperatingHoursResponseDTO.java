package com.devdelivery.restaurant_service.dto;

import com.devdelivery.restaurant_service.entity.OperatingHours;
import com.devdelivery.restaurant_service.entity.enums.DayWeek;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalTime;

@Data
@Schema(description = "Dados retornados dos horários de um restaurante")
public class OperatingHoursResponseDTO {
    @Schema(description = "Identificador único", example = "1")
    private Integer id;

    @Schema(description = "Horário de abertura do estabelecimento", example = "08:00")
    private LocalTime openHour;

    @Schema(description = "Horário de fechamento do estabelecimento", example = "18:00")
    private LocalTime closeHour;

    @Schema(description = "Dia da semana ao qual o horário de funcionamento se aplica", example = "MONDAY")
    private DayWeek day;

    public OperatingHoursResponseDTO() {}

    public OperatingHoursResponseDTO(OperatingHours operatingHours) {
        this.id = operatingHours.getId();
        this.openHour = operatingHours.getOpenHour();
        this.closeHour = operatingHours.getCloseHour();
        this.day = operatingHours.getDay();
    }
}
