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
public class MerchantCompany {
    private Long id;

    private String merchantName;

    private String socialInfoCode;

    private String corporateCertificationUrl;

    private Byte certificationStatus;

    private String legalPerson;

    private String legalPersonMobile;

    private String legalPersonIdentityNo;

    private String businessScope;

    private Long rentAreaId;

    private Long operationTypeId;

    private String registryLocaltion;

    private String emergencyContact;

    private String emergencyContactMobile;

    private String detailLocation;

    private Date gmtCreate;

    private Date gmtModified;

    private Byte isDelete;

}