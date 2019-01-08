package com.snxy.contract.service.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by lvhai on 2019/1/5.
 */
@Data
@Builder
public class ContractEditVo {
    private String editView;
    List<JsContractMetaData> jsCmds;
    Map<String,Object> valueMap;
}
