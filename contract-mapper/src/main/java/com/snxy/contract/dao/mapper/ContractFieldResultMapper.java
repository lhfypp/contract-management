package com.snxy.contract.dao.mapper;

import com.snxy.contract.domain.ContractFieldResult;

import java.util.List;

public interface ContractFieldResultMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ContractFieldResult record);

    int insertSelective(ContractFieldResult record);

    ContractFieldResult selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ContractFieldResult record);

    int updateByPrimaryKey(ContractFieldResult record);

    int deleteByContractMajorId(long contractMajorId);

    int batchSave(List<ContractFieldResult> cfrs);

    List<ContractFieldResult> getByContractMajorId(long contractMajorId);
}