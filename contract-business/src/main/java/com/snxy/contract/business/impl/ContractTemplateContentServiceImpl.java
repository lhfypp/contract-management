package com.snxy.contract.business.impl;

import com.snxy.common.exception.BizException;
import com.snxy.contract.dao.mapper.ContractTemplateContentMapper;
import com.snxy.contract.domain.ContractTemplateContent;
import com.snxy.contract.service.ContractTemplateContentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ContractTemplateContentServiceImpl implements ContractTemplateContentService {
    @Resource
    private ContractTemplateContentMapper templateContentMapper;

    @Override
    public String getTemplateByTemplateId(Integer templateId) {
        ContractTemplateContent contractTemplateContent=templateContentMapper.selectByTemplateId(templateId);
        if(contractTemplateContent==null){
            throw new BizException(String.format("合同模版id[%d]没有找到对应模版定义",templateId));
        }
        return contractTemplateContent.getTemplateContent();
    }
}
