package com.authservice.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    private String parentRegion;
    private String childRegion;

    public Address(String parentRegion, String childRegion) {
        this.parentRegion = parentRegion;
        this.childRegion = childRegion;
    }

    public void changeInfo(String parentRegion, String childRegion) {
        this.parentRegion = parentRegion;
        this.childRegion = childRegion;
    }
}