package com.snxy.contract.business.impl;

import com.snxy.contract.dao.mapper.ContractFieldResultMapper;
import com.snxy.contract.domain.ContractFieldResult;
import com.snxy.contract.service.ContractFieldResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ContractFieldResultServiceImpl implements ContractFieldResultService {
    @Resource
    private ContractFieldResultMapper contractFieldResultMapper;
    @Override
    public int batchSave(List<ContractFieldResult> cfrs) {
        log.debug("批量保存个性化字段数据{}",cfrs.toArray(new ContractFieldResult[] {}).toString());
       return contractFieldResultMapper.batchSave(cfrs);
    }

    @Override
    public List<ContractFieldResult> getByContractMajorId(long contractMajorId) {
        return contractFieldResultMapper.getByContractMajorId(contractMajorId);
    }

    @Override
    public int deleteByContractMajorId(long contractMajorId) {

        return contractFieldResultMapper.deleteByContractMajorId(contractMajorId);
    }
}
