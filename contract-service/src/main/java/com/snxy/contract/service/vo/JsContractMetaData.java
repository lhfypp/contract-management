package com.snxy.contract.service.vo;

import lombok.Data;

/**
 * Created by lvhai on 2019/1/5.
 */
@Data
public class JsContractMetaData {
    private String code;
    private String name;
    private Integer allowNull;
    private int dataType;
    private String checkRegex;
}
