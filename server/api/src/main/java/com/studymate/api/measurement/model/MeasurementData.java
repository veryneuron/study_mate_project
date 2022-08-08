package com.studymate.api.measurement.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Document(collection = "measurement_data")
public class MeasurementData {
    private Float temperature;
    private Float humidity;
    private LocalDateTime timestamp;
    private String raspberrypiAddress;

}
