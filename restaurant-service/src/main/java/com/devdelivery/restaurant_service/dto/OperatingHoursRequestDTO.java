package com.devdelivery.restaurant_service.dto;

import com.devdelivery.restaurant_service.entity.enums.DayWeek;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
@Schema(description = "Dados para criação ou atualização dos horários de funcionamento")
public class OperatingHoursRequestDTO {
    @NotNull(message = "Open hour is required.")
    @Schema(description = "Horário de abertura do estabelecimento", example = "08:00")
    private LocalTime openHour;

    @NotNull(message = "Close hour is required.")
    @Schema(description = "Horário de fechamento do estabelecimento", example = "18:00")
    private LocalTime closeHour;

    @NotNull(message = "Day is required.")
    @Schema(description = "Dia da semana ao qual o horário de funcionamento se aplica", example = "MONDAY", allowableValues = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"})
    private DayWeek day;
}
