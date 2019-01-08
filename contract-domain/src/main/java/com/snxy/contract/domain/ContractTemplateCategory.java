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
public class ContractTemplateCategory {
    private Long id;

    private String contractTemplateName;

    private Integer contractType;

    private Integer isCharge;

    private Long parentId;

    private String remark;

    private Date gmtCreate;

    private Date gmtModified;

    private Byte isDelete;

}