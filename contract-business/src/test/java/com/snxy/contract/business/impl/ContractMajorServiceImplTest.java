package com.snxy.contract.business.impl;

import com.snxy.contract.business.utils.ContractSqlEngine;
import com.snxy.contract.domain.ContractMajor;
import com.snxy.contract.domain.ContractMetaData;
import com.snxy.contract.service.ContractFieldService;
import com.snxy.contract.service.ContractMajorService;
import com.snxy.contract.service.vo.AppContractVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lvhai on 2019/1/4.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ContractMajorServiceImplTest {
    @Resource
    ContractMajorService contractMajorService;
    @Resource
    private ContractFieldService contractFieldService;

    private int templateCategoryId = 1;
    Map<String, Object> valueMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {

        valueMap.put("company_mobile", "1381837418");
        valueMap.put("merchant_name", "神农");
        valueMap.put("rent_frequency", 2);
    }

    @Test
    public void insertContract() throws Exception {
//        Long contractId=contractMajorService.insertContract(templateCategoryId,valueMap);
//        if(contractId!=null) {
//            log.debug(contractId.toString());
//        }
    }

    @Test
    public void getContractMetaDataByTemplateId() throws Exception {
        List<ContractMetaData> contractMetaDatas = contractFieldService.getContractMetaDataByTemplateId(templateCategoryId);
        Assert.assertEquals(contractMetaDatas.size(),2);
        contractMetaDatas.forEach(contractMetaData -> log.debug(contractMetaData.toString()));
    }

    @Test
    public void getSql() throws Exception {
        List<ContractMetaData> contractMetaDatas = contractFieldService.getContractMetaDataByTemplateId(templateCategoryId);
        Map<String, String> extraMap = new HashMap<>();
        extraMap.put("contract_no", "1");
        extraMap.put("drafter_id", String.valueOf(1));
        extraMap.put("is_valid", String.valueOf(0));
        extraMap.put("status", String.valueOf(0));
        String insertSql= ContractSqlEngine.getMajorInsrtSql(contractMetaDatas,valueMap,extraMap);
        log.debug(insertSql);
    }

    @Test
    public void insert() throws Exception{
        ContractMajor cm=ContractMajor.builder().companyMobile("111").merchantName("aaa").build();
//        Long contractId=contractMajorService.insert(cm);
//        log.debug(contractId.toString());
    }
    @Test
    public void getAppContractVo(){
        Long id=17L;
        List<AppContractVo> appContractVos=contractMajorService.getAppContractVo(id);
        appContractVos.forEach(ac->{
            log.debug(ac.toString());
        });
    }
}