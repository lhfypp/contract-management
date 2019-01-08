package com.snxy.contract.business.impl;

import com.snxy.common.exception.BizException;
import com.snxy.contract.business.utils.ContractSqlEngine;
import com.snxy.contract.business.utils.ContractTemplateEngine;
import com.snxy.contract.dao.mapper.ContractMajorMapper;
import com.snxy.contract.domain.ContractFieldResult;
import com.snxy.contract.domain.ContractMetaData;
import com.snxy.contract.service.ContractFieldResultService;
import com.snxy.contract.service.ContractFieldService;
import com.snxy.contract.service.ContractMajorService;
import com.snxy.contract.service.ContractTemplateContentService;
import com.snxy.contract.service.vo.ContractEditVo;
import com.snxy.contract.service.vo.JsContractMetaData;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class ContractMajorServiceImpl implements ContractMajorService {

    private final static int STATUS_DEFULT = 0;
    private final static int IS_VALAD_DEFAULT = 0;

    private final static List<String> extraFields=Arrays.asList(
            "id",
            "contract_no",
            "contract_template_category_id"
    );

    @Resource
    private ContractMajorMapper contractMajorMapper;
    @Resource
    private ContractFieldService contractFieldService;
    @Resource
    private ContractFieldResultService contractFieldResultService;
    @Resource
    private ContractTemplateContentService contractTemplateContentService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveContract(long creatorId, Map<String, Object> valueMap) {
        if (!valueMap.containsKey("contract_template_category_id")) {
            throw new BizException("请传入合同模版id");
        }

        int templateCategoryId = Integer.parseInt(valueMap.get("contract_template_category_id").toString());
        List<ContractMetaData> contractMetaDatas = contractFieldService.getContractMetaDataByTemplateId(templateCategoryId);
        if (contractMetaDatas.size() == 0) {
            throw new BizException(String.format("templateCategoryId为%s没找到元数据", templateCategoryId));
        }


        if (valueMap.get("id") != null&& StringUtils.isNotBlank(valueMap.get("id").toString())) {//合同id
            long contractMajorId = Long.parseLong(valueMap.get("id").toString());
           return updateContract(creatorId, contractMajorId, contractMetaDatas, valueMap);
        }

        return insertContract(creatorId, contractMetaDatas, valueMap);
    }


    //自此处设置@Transactional(rollbackFor = Exception.class) 设置无效，不会启动事务,间接方法执行时会有此问题，出错不会回滚
    private Long insertContract(long creatorId, List<ContractMetaData> contractMetaDatas, Map<String, Object> valueMap) {

        String contractNo = buildContractNo();
        Map<String, String> extraMap = new HashMap<>();
        extraMap.put("contract_no", contractNo);
        extraMap.put("drafter_id", String.valueOf(creatorId));
        extraMap.put("is_valid", String.valueOf(IS_VALAD_DEFAULT));
        extraMap.put("status", String.valueOf(STATUS_DEFULT));
        //合同模版id
        extraMap.put("contract_template_category_id",valueMap.get("contract_template_category_id").toString());
        addTime(valueMap,extraMap,true);
        String insertSql = ContractSqlEngine.getMajorInsrtSql(contractMetaDatas, valueMap, extraMap);
//        ContractMajor contractMajor = new ContractMajor();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("insertSql", insertSql);
        // contractMajorMapper.insertMajorContract(contractMajor,insertSql);
        contractMajorMapper.insertByMap(map);

        log.debug(map.get("id").toString());
        Long contractMajorId = Long.valueOf(map.get("id").toString());
        List<ContractFieldResult> cfrs = ContractSqlEngine.getContractFileldResult(contractMetaDatas, valueMap, contractMajorId);
        //保存个性
        if(cfrs.size()>0) {
            contractFieldResultService.batchSave(cfrs);
        }
        return contractMajorId;
    }


    private String buildContractNo() {
        ///TODO
        java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
        Random random = new Random();

        String contractNo = "HT" + format2.format(new Date()) + random.nextInt(10);
        return contractNo;
    }


    private Long updateContract(long creatorId, long contractMajorId, List<ContractMetaData> contractMetaDatas, Map<String, Object> valueMap) {
        Map<String, String> extraMap = new HashMap<>();
        addTime(valueMap,extraMap,false);
        String updateSql = ContractSqlEngine.getUpdateSql(contractMetaDatas, valueMap,extraMap, contractMajorId);
        log.debug("updateSql:{}",updateSql);
        //  List<ContractFieldResult> dbCfrs = contractFieldResultService.getByContractMajorId(contractMajorId);
        //简单处理，删除原来值，插入新的值
        List<ContractFieldResult> curCfrs = ContractSqlEngine.getContractFileldResult(contractMetaDatas, valueMap, contractMajorId);


        contractMajorMapper.updateBySql(updateSql);
        contractFieldResultService.deleteByContractMajorId(contractMajorId);
        if(curCfrs.size()>0) {
            contractFieldResultService.batchSave(curCfrs);
        }
        return contractMajorId;
    }

    @Override
    public ContractEditVo contractEdit(Long contractMajorId) {
        ContractHelperResult contractHelperResult = getContractHelperResult(contractMajorId);
        String editView = ContractTemplateEngine.parseEditeView(contractHelperResult.template, contractHelperResult.contractMetaDatas, contractHelperResult.valueMap);
        List<JsContractMetaData> metaDatas = ContractTemplateEngine.getJsContractMetaData(contractHelperResult.contractMetaDatas);
        return ContractEditVo.builder().editView(editView).jsCmds(metaDatas).valueMap(contractHelperResult.valueMap).build();
    }

    @Override
    public ContractEditVo contractAdd(Integer templateId) {
        String template = contractTemplateContentService.getTemplateByTemplateId(templateId);
        List<ContractMetaData> contractMetaDatas = contractFieldService.getContractMetaDataByTemplateId(templateId);
        String editView = ContractTemplateEngine.parseEditeView(template, contractMetaDatas);
        List<JsContractMetaData> metaDatas = ContractTemplateEngine.getJsContractMetaData(contractMetaDatas);
        return ContractEditVo.builder().editView(editView).jsCmds(metaDatas).build();
    }

    @Override
    public String getContractView(Long contractMajorId) {
        ContractHelperResult contractHelperResult = getContractHelperResult(contractMajorId);
        return ContractTemplateEngine.parseReadView(contractHelperResult.template, contractHelperResult.contractMetaDatas, contractHelperResult.valueMap);
    }

    private ContractHelperResult getContractHelperResult(Long contractMajorId) {

        Integer templateId = contractMajorMapper.getTemplateIdById(contractMajorId);//
        List<ContractMetaData> contractMetaDatas = contractFieldService.getContractMetaDataByTemplateId(templateId);
        String selectSql = ContractSqlEngine.getSelectSql(contractMetaDatas, contractMajorId,extraFields);

        Map<String, Object> valueMap = contractMajorMapper.selectBySql(selectSql);

        List<ContractFieldResult> cfrs = contractFieldResultService.getByContractMajorId(contractMajorId);
        cfrs.parallelStream().forEach(cfr -> {
            ContractMetaData contractMetaData = contractMetaDatas.parallelStream().filter(cmd -> cmd.getId().equals(cfr.getContractFieldId())).findFirst().orElse(null);
            if (contractMetaData == null) {
                throw new BizException(String.format("合同%个性化存储属性值找不到字段定义", contractMajorId));
            }
            valueMap.put(contractMetaData.getCode(), cfr.getFieldValue());
        });
        String template = contractTemplateContentService.getTemplateByTemplateId(templateId);
        return ContractHelperResult.builder()
                .contractMetaDatas(contractMetaDatas)
                .template(template)
                .valueMap(valueMap).build();
    }
    private void addTime(Map<String, Object> valueMap, Map<String, String> extraMap,boolean isInsert) {
         if(valueMap.get("rent_start_time_year")!=null&&StringUtils.isNotBlank(valueMap.get("rent_start_time_year").toString())
                 &&valueMap.get("rent_start_time_month")!=null&&StringUtils.isNotBlank(valueMap.get("rent_start_time_month").toString())
                 &&valueMap.get("rent_start_time_day")!=null&&StringUtils.isNotBlank(valueMap.get("rent_start_time_day").toString())){
             extraMap.put("rent_start_time",valueMap.get("rent_start_time_year").toString()+"-"+
                     valueMap.get("rent_start_time_month").toString()+"-"+
                     valueMap.get("rent_start_time_day").toString()
             );
         }
         else {
             if(isInsert==false){
                 extraMap.put("rent_start_time","");
             }
         }

        if(valueMap.get("rent_end_time_year")!=null&&StringUtils.isNotBlank(valueMap.get("rent_end_time_year").toString())
                &&valueMap.get("rent_end_time_month")!=null&&StringUtils.isNotBlank(valueMap.get("rent_end_time_month").toString())
                &&valueMap.get("rent_end_time_day")!=null&&StringUtils.isNotBlank(valueMap.get("rent_end_time_day").toString())){
            extraMap.put("rent_end_time",valueMap.get("rent_end_time_year").toString()+"-"+
                    valueMap.get("rent_end_time_month").toString()+"-"+
                    valueMap.get("rent_end_time_day").toString()
            );
        }
        else {
            if(isInsert==false){
                extraMap.put("rent_end_time","");
            }
        }

        if(valueMap.get("sign_time_year")!=null&&StringUtils.isNotBlank(valueMap.get("sign_time_year").toString())
                &&valueMap.get("sign_time_month")!=null&&StringUtils.isNotBlank(valueMap.get("sign_time_month").toString())
                &&valueMap.get("sign_time_day")!=null&&StringUtils.isNotBlank(valueMap.get("sign_time_day").toString())){
            extraMap.put("sign_time",valueMap.get("sign_time_year").toString()+"-"+
                    valueMap.get("sign_time_month").toString()+"-"+
                    valueMap.get("sign_time_day").toString()
            );
        }
        else {
            if(isInsert==false){
                extraMap.put("sign_time","");
            }
        }
    }

    @Data
    @Builder
    private static class ContractHelperResult {
        Map<String, Object> valueMap;
        String template;
        List<ContractMetaData> contractMetaDatas;
    }
}
