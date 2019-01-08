package com.snxy.contract.service.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RentSiteVo {
    private Long id;

    private Integer siteType;

    private Long areaId;

    private String siteNo;

    private String siteName;

}