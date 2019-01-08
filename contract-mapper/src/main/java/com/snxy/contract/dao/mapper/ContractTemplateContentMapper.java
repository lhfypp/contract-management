package com.snxy.contract.dao.mapper;

import com.snxy.contract.domain.ContractTemplateContent;

public interface ContractTemplateContentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ContractTemplateContent record);

    int insertSelective(ContractTemplateContent record);

    ContractTemplateContent selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ContractTemplateContent record);

    int updateByPrimaryKeyWithBLOBs(ContractTemplateContent record);

    int updateByPrimaryKey(ContractTemplateContent record);

    ContractTemplateContent selectByTemplateId(Integer templateId);
}