package com.inquiryservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegionNameRequestDto {

    @NotBlank
    private String parentName;

    @NotBlank
    private String childName;
}