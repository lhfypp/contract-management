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
public class ContractTemplateContent {
    private Long id;

    private Long creatorId;

    private Long modifierId;

    private Long templateCategoryId;

    private String remark;

    private Date gmtCreate;

    private Date gmtModified;

    private Byte isDelete;

    private String templateContent;

}