package com.snxy.contract.service;

import com.snxy.contract.domain.ContractMajor;
import com.snxy.contract.service.vo.AppContractVo;
import com.snxy.contract.service.vo.ContractEditVo;

import java.util.List;
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

    List<AppContractVo> getAppContractVo(Long contractMajorId);
}
