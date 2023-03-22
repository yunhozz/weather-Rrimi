package com.inquiryservice.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "is_deleted = false")
public class Weather extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private RegionInfo regionInfo;

    private String baseDate;

    private String baseTime;

    private double temperature;

    private double rainfall;

    private double humid;

    private boolean isDeleted;

    @Builder
    private Weather(RegionInfo regionInfo, String baseDate, String baseTime, double temperature, double rainfall, double humid) {
        this.regionInfo = regionInfo;
        this.baseDate = baseDate;
        this.baseTime = baseTime;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.humid = humid;
        this.isDeleted = false;
    }
}