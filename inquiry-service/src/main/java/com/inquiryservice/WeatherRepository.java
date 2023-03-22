package com.inquiryservice;

import com.inquiryservice.domain.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<Weather, Long> {

    @Query("select w from Weather w " +
            "where w.regionInfo.parentRegion = :parent" +
            " and w.regionInfo.childRegion = :child " +
            "order by w.createdAt desc " +
            "limit 1")
    Optional<Weather> findLatestWeatherInfoByRegionName(@Param("parent") String parentRegion, @Param("child") String childRegion);

    @Query("select w from Weather w " +
            "where w.regionInfo.parentRegion = :parent" +
            " and w.regionInfo.childRegion = :child" +
            " and w.createdAt >= :threshold")
    List<Weather> findWeatherInfoAfterThresholdByRegionName(@Param("parent") String parentRegion, @Param("child") String childRegion, @Param("threshold") LocalDateTime threshold);
}