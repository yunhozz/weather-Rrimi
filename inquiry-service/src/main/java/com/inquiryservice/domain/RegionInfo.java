package com.inquiryservice.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegionInfo {

    private String parentRegion;
    private String childRegion;
    private int nx;
    private int ny;

    @Builder
    private RegionInfo(String parentRegion, String childRegion, int nx, int ny) {
        this.parentRegion = parentRegion;
        this.childRegion = childRegion;
        this.nx = nx;
        this.ny = ny;
    }
}