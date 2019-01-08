package com.snxy.contract.domain;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ContractFieldResult {
    private Long id;

    private Long contractMajorId;

    private Long contractFieldId;

    private String fieldValue;

    private Date gmtCreate;

    private Date gmtModified;

    private Byte isDelete;

}