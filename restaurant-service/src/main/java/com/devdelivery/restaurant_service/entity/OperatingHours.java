package com.devdelivery.restaurant_service.entity;

import com.devdelivery.restaurant_service.entity.enums.DayWeek;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "operating_hours")
public class OperatingHours extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "open_hour")
    private LocalTime openHour;

    @Column(name = "close_hour")
    private LocalTime closeHour;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private DayWeek day;

    @ManyToOne
    @JoinColumn(name = "id_restaurant")
    private Restaurant restaurant;
}
