package com.snxy.contract.dao.mapper;

import com.snxy.contract.domain.ContractMetaData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContractFieldMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ContractMetaData record);

    int insertSelective(ContractMetaData record);

    ContractMetaData selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ContractMetaData record);

    int updateByPrimaryKey(ContractMetaData record);

    List<ContractMetaData> getContractMetaDataByTemplateId(int templateCategoryId);

}