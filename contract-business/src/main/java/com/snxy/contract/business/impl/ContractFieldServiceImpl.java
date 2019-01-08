package com.snxy.contract.business.impl;

import com.snxy.contract.dao.mapper.ContractFieldMapper;
import com.snxy.contract.domain.ContractMetaData;
import com.snxy.contract.service.ContractFieldService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ContractFieldServiceImpl implements ContractFieldService {

    @Resource
    ContractFieldMapper contractFieldMapper;

    @Override
    public List<ContractMetaData> getContractMetaDataByTemplateId(int templateCategoryId) {
        return contractFieldMapper.getContractMetaDataByTemplateId(templateCategoryId);
    }
}
