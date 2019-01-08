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
public class ContractMajor {
    private Long id;

    private Long contractTemplateCategoryId;

    private String contractNo;

    private Long companyId;

    private String companyMobile;

    private String merchantName;

    private Long rentAreaId;

    private String rentSiteNo;

    private String supportFacility;

    private String remark;

    private String secondParty;

    private Integer roomNumber;

    private Short area;

    private String rentAim;

    private Float agreementPeriod;

    private Date rentStartTime;

    private Short rentStartTimeYear;

    private Short rentStartTimeMonth;

    private Short rentStartTimeDay;

    private Date rentEndTime;

    private Short rentEndTimeYear;

    private Short rentEndTimeMonth;

    private Short rentEndTimeDay;

    private Short rentFrequency;

    private Integer rentFee;

    private Integer deposit;

    private Integer paymentFrequency;

    private Integer healthFee;

    private Integer waterFee;

    private String signerMobile;

    private Date signTime;

    private Short signTimeYear;

    private Short signTimeMonth;

    private Short signTimeDay;

    private Long drafterId;

    private Integer isValid;

    private Integer status;

    private Integer totalFee;

    private Integer receivedFee;

    private Integer currentFee;

    private Integer residualFee;

    private Byte isDelete;

}