package com.snxy.contract.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RentSite {
    private Long id;

    private Integer siteType;

    private Long areaId;

    private String siteNo;

    private String siteName;

    private String gps;

    private Long longitude;

    private Long latitude;

    private Integer siteArea;

    private String outsideImage;

    private String insideImage;

    private String supportFacility;

    private String supportPark;

    private Byte beRented;

    private Date currContractEndTime;

    private String remark;

    private Byte isDelete;


}