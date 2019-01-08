package com.snxy.contract.dao.mapper;

import com.snxy.contract.domain.ContractTemplateCategory;

public interface ContractTemplateCategoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ContractTemplateCategory record);

    int insertSelective(ContractTemplateCategory record);

    ContractTemplateCategory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ContractTemplateCategory record);

    int updateByPrimaryKey(ContractTemplateCategory record);
}