package com.snxy.contract.service;

import com.snxy.contract.domain.ContractMetaData;

import java.util.List;

public interface ContractFieldService {
    List<ContractMetaData> getContractMetaDataByTemplateId(int templateCategoryId);
}
