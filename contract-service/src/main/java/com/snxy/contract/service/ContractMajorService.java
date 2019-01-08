package com.snxy.contract.service;

import com.snxy.contract.domain.ContractMajor;
import com.snxy.contract.service.vo.ContractEditVo;

import java.util.Map;

public interface ContractMajorService {
    /**
     * 保存合同信息
     * @param valueMap
     * @return
     */
    Long saveContract(long creatorId,Map<String, Object> valueMap);

    /**
     *
     * @param contractMajorId
     * @return
     */
    ContractEditVo contractEdit(Long contractMajorId);

    /**
     *
     * @param templateId
     * @return
     */
    ContractEditVo contractAdd (Integer templateId);
    String getContractView(Long contractMajorId);
//    Long insertContract(long creatorId,int templateCategoryId, Map<String, Object> valueMap);


//    Long updateContract(long creatorId,long contractMajorId,int templateCategoryId,Map<String, Object> valueMap);
}
