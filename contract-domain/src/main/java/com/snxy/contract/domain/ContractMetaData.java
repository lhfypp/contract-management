package com.snxy.contract.domain;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ContractMetaData {
    private Long id;

    private String code;

    private String name;

    private Integer isCommon;

    private Integer allowNull;

    private Integer dataType;

    private String checkRegex;

    private String cssName;

    private Integer width;

    private Integer height;

    private Short showOrder;

    private String category;

    private Short categoryOrder;

    private Integer controlType;

    private String dict;

    private String defaultValue;

    private Long templateId;

    private Date gmtCreate;

    private Date gmtModified;

    private Long creatorId;

    private Long modifierId;

    private Byte isDelete;

}