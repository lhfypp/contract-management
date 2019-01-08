package com.snxy.contract.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RentArea {
    private Long id;

    private String areaName;

    private Long longitude;

    private Long latitude;

    private Byte isDelete;


}