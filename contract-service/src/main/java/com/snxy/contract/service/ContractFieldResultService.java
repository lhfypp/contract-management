package com.snxy.contract.service;

import com.snxy.contract.domain.ContractFieldResult;

import java.util.List;

public interface ContractFieldResultService {
    int batchSave(List<ContractFieldResult> cfrs);

    List<ContractFieldResult> getByContractMajorId(long contractMajorId);

    /**
     * 通过合同主id删除个性属性值
     * @param contractMajorId
     * @return
     */
    int deleteByContractMajorId(long contractMajorId);
}
