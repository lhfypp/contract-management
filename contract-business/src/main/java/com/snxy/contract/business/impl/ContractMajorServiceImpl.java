package com.snxy.contract.business.impl;

import com.snxy.common.exception.BizException;
import com.snxy.contract.business.utils.ContractSqlEngine;
import com.snxy.contract.business.utils.ContractTemplateEngine;
import com.snxy.contract.dao.mapper.ContractMajorMapper;
import com.snxy.contract.domain.*;
import com.snxy.contract.service.*;
import com.snxy.contract.service.vo.AppContractVo;
import com.snxy.contract.service.vo.ContractEditVo;
import com.snxy.contract.service.vo.JsContractMetaData;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContractMajorServiceImpl implements ContractMajorService {

    private final static int STATUS_DEFULT = 0;
    private final static int IS_VALAD_DEFAULT = 0;

    private final static String CATEGORY_DEFAULT = "合同信息";
    private final static short CATEGORY_DEFAULT_ORDER = 100;

    private final static List<String> extraFields = Arrays.asList(
            "id",
            "contract_no",
            "contract_template_category_id",
            "drafter_id"
    );

    private final static List<String> moneyFields = Arrays.asList(
            "total_fee"

    );

    @Resource
    private ContractMajorMapper contractMajorMapper;
    @Resource
    private ContractFieldService contractFieldService;
    @Resource
    private ContractFieldResultService contractFieldResultService;
    @Resource
    private ContractTemplateContentService contractTemplateContentService;
    @Resource
    private MerchantCompanyService merchantCompanyService;
    @Resource
    private RentAreaService rentAreaService;

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

        //金额特殊处理
        dealMoney(valueMap, true);

        if (valueMap.get("id") != null && StringUtils.isNotBlank(valueMap.get("id").toString())) {//合同id
            long contractMajorId = Long.parseLong(valueMap.get("id").toString());
            return updateContract(creatorId, contractMajorId, contractMetaDatas, valueMap);
        }

        return insertContract(creatorId, contractMetaDatas, valueMap);
    }

    private void dealMoney(Map<String, Object> valueMap, boolean isSave) {
        if (isSave) {
            moneyFields.parallelStream().forEach(moneyField -> {
                if (valueMap.get(moneyField) != null) {
                    if (StringUtils.isNotBlank(valueMap.get(moneyField).toString())) {
                        long money = Long.parseLong(valueMap.get(moneyField).toString());
                        valueMap.put(moneyField, (money * 100));
                    }
                }
            });
        } else {
            moneyFields.parallelStream().forEach(moneyField -> {
                if (valueMap.get(moneyField) != null) {
                    if (StringUtils.isNotBlank(valueMap.get(moneyField).toString())) {
                        long money = Long.parseLong(valueMap.get(moneyField).toString());
                        valueMap.put(moneyField, (money / 100));
                    }
                }
            });
        }
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
        extraMap.put("contract_template_category_id", valueMap.get("contract_template_category_id").toString());
        //合同默认费用处理
        dealExtraMoney(valueMap, extraMap);

        addTime(valueMap, extraMap, true);
        checkRentTime(contractMetaDatas, valueMap, extraMap, null);
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
        if (cfrs.size() > 0) {
            contractFieldResultService.batchSave(cfrs);
        }
        return contractMajorId;
    }

    private void dealExtraMoney(Map<String, Object> valueMap, Map<String, String> extraMap) {
        extraMap.put("received_fee", "0");//新增已收为0
        //欠款默认为总金额
        if (valueMap.get("total_fee") != null) {
            if (StringUtils.isNotBlank(valueMap.get("total_fee").toString())) {
                extraMap.put("residual_fee", valueMap.get("total_fee").toString());
            }
        }
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
        //合同默认费用处理
        dealExtraMoney(valueMap, extraMap);
        addTime(valueMap, extraMap, false);
        checkRentTime(contractMetaDatas, valueMap, extraMap, contractMajorId);
        String updateSql = ContractSqlEngine.getUpdateSql(contractMetaDatas, valueMap, extraMap, contractMajorId);
        log.debug("updateSql:{}", updateSql);
        //  List<ContractFieldResult> dbCfrs = contractFieldResultService.getByContractMajorId(contractMajorId);
        //简单处理，删除原来值，插入新的值
        List<ContractFieldResult> curCfrs = ContractSqlEngine.getContractFileldResult(contractMetaDatas, valueMap, contractMajorId);


        contractMajorMapper.updateBySql(updateSql);
        contractFieldResultService.deleteByContractMajorId(contractMajorId);
        if (curCfrs.size() > 0) {
            contractFieldResultService.batchSave(curCfrs);
        }
        return contractMajorId;
    }

    /**
     * 检验是否存在当前签订时间范围内合同
     *
     * @param extraMap
     * @param contractMajorId 排除自己
     */
    private void checkRentTime(List<ContractMetaData> contractMetaDatas, Map<String, Object> valueMap, Map<String, String> extraMap, Long contractMajorId) {
        boolean hasRentTime = contractMetaDatas.parallelStream().anyMatch(cmd -> cmd.getCode().equals("rent_start_time_year"));
        if (hasRentTime == false)
            return;
        String rentStartTime = extraMap.get("rent_start_time");
        String rentEndTime = extraMap.get("rent_end_time");

        if (StringUtils.isBlank(rentStartTime) || StringUtils.isBlank(rentEndTime)) {
            throw new BizException("合同起止时间不能为空");
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date st= format.parse(rentStartTime);
            Date et=format.parse(rentEndTime);
            if(st.after(et)||st.equals(et)){
                throw new BizException(String.format("合同签订开始时间%s应该小于截止时间%s",rentStartTime,rentEndTime));
            }
        } catch (ParseException e) {
            throw new BizException("合同签订时间转换出错"+e.getMessage());
           // e.printStackTrace();
        }

        Long reantAreaId = valueMap.get("rent_area_id") == null ? null : Long.valueOf(valueMap.get("rent_area_id").toString());
        if (reantAreaId == null)
            return;
        String rent_site_no = valueMap.get("rent_site_no") == null ? "" : valueMap.get("rent_site_no").toString();
        if (StringUtils.isBlank(rent_site_no))
            return;
        //查找时间范围内有重叠的相同区域的合同
        List<ContractMajor> contractMajors = contractMajorMapper.findOverlap(reantAreaId,rentStartTime,rentEndTime,contractMajorId);
        //比对区域下租赁地点是否有相同的
        String[] rentSiteNos = rent_site_no.split(",");

//        boolean exist = contractMajors.parallelStream().anyMatch(contractMajor -> {
//            String innerRentSizteNo = contractMajor.getRentSiteNo();
//            if (StringUtils.isBlank(innerRentSizteNo))
//                return false;
//            String[] innerRentSizteNos = innerRentSizteNo.split(",");
//            return Arrays.stream(innerRentSizteNos).anyMatch(irs -> {
//                return true;
//            });
//        });

        Map<String, String> existContractInfo = new HashMap<>();
        contractMajors.parallelStream().forEach(
                contractMajor -> {
                    String innerRentSizteNo = contractMajor.getRentSiteNo();
                    if (StringUtils.isNotBlank(innerRentSizteNo)) {
                        String[] innerRentSizteNos = innerRentSizteNo.split(",");
                        for (String rsn : rentSiteNos) {
                            boolean exist = Arrays.stream(innerRentSizteNos).anyMatch(irsn -> rsn.equals(irsn));
                            if (exist) {
                                existContractInfo.put(contractMajor.getContractNo(), rsn);
                                break;
                            }
                        }
                    }
                }
        );
        if (existContractInfo.size() > 0) {
            StringBuilder errSb = new StringBuilder();
            errSb.append("时间与如下合同租赁地点重叠");
            existContractInfo.forEach((k, v) -> {
                errSb.append("[合同号:");
                errSb.append(k);
                errSb.append("地点编号:");
                errSb.append(v);
                errSb.append("]");
            });

            throw new BizException(errSb.toString());
        }
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
        String selectSql = ContractSqlEngine.getSelectSql(contractMetaDatas, contractMajorId, extraFields);

        Map<String, Object> valueMap = contractMajorMapper.selectBySql(selectSql);
        dealMoney(valueMap, false);
        List<ContractFieldResult> cfrs = contractFieldResultService.getByContractMajorId(contractMajorId);
        cfrs.parallelStream().forEach(cfr -> {
            ContractMetaData contractMetaData = contractMetaDatas.parallelStream().filter(cmd -> cmd.getId().equals(cfr.getContractFieldId())).findFirst().orElse(null);
            if (contractMetaData == null) {
                throw new BizException(String.format("合同%d个性化存储属性值找不到字段定义%d", contractMajorId, cfr.getContractFieldId()));
            }
            valueMap.put(contractMetaData.getCode(), cfr.getFieldValue());
        });
        String template = contractTemplateContentService.getTemplateByTemplateId(templateId);
        return ContractHelperResult.builder()
                .contractMetaDatas(contractMetaDatas)
                .template(template)
                .valueMap(valueMap).build();
    }

    private void addTime(Map<String, Object> valueMap, Map<String, String> extraMap, boolean needDefault) {
        if (valueMap.get("rent_start_time_year") != null && StringUtils.isNotBlank(valueMap.get("rent_start_time_year").toString())
                && valueMap.get("rent_start_time_month") != null && StringUtils.isNotBlank(valueMap.get("rent_start_time_month").toString())
                && valueMap.get("rent_start_time_day") != null && StringUtils.isNotBlank(valueMap.get("rent_start_time_day").toString())) {
            extraMap.put("rent_start_time", valueMap.get("rent_start_time_year").toString() + "/" +
                    valueMap.get("rent_start_time_month").toString() + "/" +
                    valueMap.get("rent_start_time_day").toString()
            );
        } else {
            if (needDefault == false) {
                extraMap.put("rent_start_time", "");
            }
        }

        if (valueMap.get("rent_end_time_year") != null && StringUtils.isNotBlank(valueMap.get("rent_end_time_year").toString())
                && valueMap.get("rent_end_time_month") != null && StringUtils.isNotBlank(valueMap.get("rent_end_time_month").toString())
                && valueMap.get("rent_end_time_day") != null && StringUtils.isNotBlank(valueMap.get("rent_end_time_day").toString())) {
            extraMap.put("rent_end_time", valueMap.get("rent_end_time_year").toString() + "/" +
                    valueMap.get("rent_end_time_month").toString() + "/" +
                    valueMap.get("rent_end_time_day").toString()
            );
        } else {
            if (needDefault == false) {
                extraMap.put("rent_end_time", "");
            }
        }

        if (valueMap.get("sign_time_year") != null && StringUtils.isNotBlank(valueMap.get("sign_time_year").toString())
                && valueMap.get("sign_time_month") != null && StringUtils.isNotBlank(valueMap.get("sign_time_month").toString())
                && valueMap.get("sign_time_day") != null && StringUtils.isNotBlank(valueMap.get("sign_time_day").toString())) {
            extraMap.put("sign_time", valueMap.get("sign_time_year").toString() + "/" +
                    valueMap.get("sign_time_month").toString() + "/" +
                    valueMap.get("sign_time_day").toString()
            );
        } else {
            if (needDefault == false) {
                extraMap.put("sign_time", "");
            }
        }
    }

    //region APP数据
    @Override
    public List<AppContractVo> getAppContractVo(Long contractMajorId) {
        ContractHelperResult contractHelperResult = getContractHelperResult(contractMajorId);
        //去掉app不可见字段
        List<ContractMetaData> contractMetaDatas = contractHelperResult.contractMetaDatas;
        for (int i = contractMetaDatas.size() - 1; i >= 0; i--) {
            //null 为默认可见, 0,不可见，即是除了0外,都可见
            int appVisible = contractMetaDatas.get(i).getAppVisible() == null ? 1 : contractMetaDatas.get(i).getAppVisible().intValue();
            if (appVisible == 0) {
                contractMetaDatas.remove(i);
            }
        }
        List<AppContractVo> appContractVos = contractMetaDatas.stream()
                .map(cmd -> {
                    String category = StringUtils.isNoneBlank(cmd.getCategory()) ? cmd.getCategory() : CATEGORY_DEFAULT;
                    int order = cmd.getCategoryOrder() != null ? (int) cmd.getCategoryOrder() : CATEGORY_DEFAULT_ORDER;
                    return AppContractVo.builder().category(category)
                            .categoryOrder(order).build();
                }).distinct().sorted(Comparator.comparingInt(AppContractVo::getCategoryOrder)).collect(Collectors.toList());

        Map<String, String> extraMap = new HashMap<>();//时间
        //处理时间，并删除年月日的元数据,添加整合的年月日
        List<TimeHelperResult> timeHelperResults = dealTime(contractHelperResult, extraMap);
        //id字段替换为名称 （商户公司，区域）
        dealIdForName(contractHelperResult.getValueMap());
        appContractVos.forEach(ac -> {
            ac.setFieldValues(
                    contractMetaDatas.stream().filter(cmd -> {
                        String category = StringUtils.isNoneBlank(cmd.getCategory()) ? cmd.getCategory() : CATEGORY_DEFAULT;
                        return category.equals(ac.getCategory());
                    }).map(cmd -> {
                        ///TODO  生成对象，获取值，尤其对时间处理需要考虑

                        String value = ContractTemplateEngine.getFieldValue(cmd, contractHelperResult.getValueMap());//contractHelperResult.getValueMap().get(cmd.getCode()) == null ? "" : contractHelperResult.getValueMap().get(cmd.getCode()).toString();
                        int order = cmd.getShowOrder() == null ? Integer.MAX_VALUE : cmd.getShowOrder().intValue();
                        return AppContractVo.FieldValue.builder().code(cmd.getCode()).name(cmd.getName()).value(value).fieldOrder(order).build();
                    })
                            //统一排序
                            //.sorted(Comparator.comparingInt(AppContractVo.FieldValue::getFieldOrder))
                            .collect(Collectors.toList()));
        });
        //简化处理，时间放到默认的"合同信息"最前
        timeHelperResults.forEach(tr -> {
            AppContractVo appContractVo = appContractVos.stream().filter(ac -> tr.getCategory().equals(ac.getCategory())).findFirst().orElse(null);
            appContractVo.getFieldValues().add(
                    AppContractVo.FieldValue.builder().code(tr.getCode()).name(tr.getName()).value(tr.getValue()).fieldOrder(tr.getOrder()).build()
            );
        });
        appContractVos.forEach(ac -> {
            ac.getFieldValues().sort(Comparator.comparingInt(AppContractVo.FieldValue::getFieldOrder));
        });
        return appContractVos;
    }

    private void dealIdForName(Map<String, Object> valueMap) {
        if (valueMap.get("company_id") != null) {
            Long companyId = Long.valueOf(valueMap.get("company_id").toString());
            MerchantCompany mc = merchantCompanyService.getMerchantCompanyById(companyId);
            if (mc != null) {
                valueMap.put("company_id", mc.getMerchantName());
            }
        }

        if (valueMap.get("rent_area_id") != null) {
            Long rentAreaId = Long.valueOf(valueMap.get("rent_area_id").toString());
            RentArea rentArea = rentAreaService.getRentAreaById(rentAreaId);
            if (rentArea != null) {
                valueMap.put("rent_area_id", rentArea.getAreaName());
            }
        }
    }

    private List<TimeHelperResult> dealTime(ContractHelperResult contractHelperResult, Map<String, String> extraMap) {

        List<TimeHelperResult> timeHelperResults = new ArrayList<>();
        Map<String, Object> valueMap = contractHelperResult.getValueMap();
        addTime(valueMap, extraMap, false);
        valueMap.remove("rent_start_time_year");
        valueMap.remove("rent_start_time_month");
        valueMap.remove("rent_start_time_day");
        valueMap.remove("rent_end_time_year");
        valueMap.remove("rent_end_time_month");
        valueMap.remove("rent_end_time_day");
        valueMap.remove("sign_time_year");
        valueMap.remove("sign_time_month");
        valueMap.remove("sign_time_day");
        List<ContractMetaData> contractMetaDatas = contractHelperResult.getContractMetaDatas();

        ///todo
//        extraMap.forEach((k, v) -> {
//            String code = k + "_year";
//            ContractMetaData contractMetaData = contractMetaDatas.parallelStream().filter(cmd -> code.equals(cmd.getCode())).findFirst().orElse(null);
//            String category = contractMetaData.getCategory() == null ? CATEGORY_DEFAULT : contractMetaData.getCategory();
//            String name = contractMetaData.getName().substring(0, contractMetaData.getName().length() - 1);
//            int order = contractMetaData.getShowOrder() == null ? Integer.MAX_VALUE : contractMetaData.getShowOrder().intValue();
//            timeHelperResults.add(
//                    TimeHelperResult.builder()
//                            .category(category)
//                            .code(code)
//                            .value(v)
//                            .name(name)
//                            .order(order)
//                            .build()
//            );
//        });
        //合同起止时间
        String startTime = extraMap.get("rent_start_time");
        String endTime = extraMap.get("rent_end_time");
        String stCode = "rent_start_time_year";
        ContractMetaData stcmd = contractMetaDatas.parallelStream().filter(cmd -> stCode.equals(cmd.getCode())).findFirst().orElse(null);
        if (stcmd != null) {
            String stCategory = stcmd.getCategory() == null ? CATEGORY_DEFAULT : stcmd.getCategory();
            //     String name = stcmd.getName().substring(0, stcmd.getName().length() - 1);
            int stOrder = stcmd.getShowOrder() == null ? Integer.MAX_VALUE : stcmd.getShowOrder().intValue();
            String startEndTime;
            if (StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {
                startEndTime = startTime + endTime;
            } else {
                startEndTime = startTime + "至" + endTime;
            }
            timeHelperResults.add(
                    TimeHelperResult.builder()
                            .category(stCategory)
                            .code(stCode)
                            .value(startEndTime)
                            .name("合同起止时间")
                            .order(stOrder)
                            .build()
            );
        }
        //合同签订时间
        String signTimeCode = "sign_time_year";
        String signTimeVal = extraMap.get("sign_time");
        ContractMetaData contractMetaData = contractMetaDatas.parallelStream().filter(cmd -> signTimeCode.equals(cmd.getCode())).findFirst().orElse(null);
        if (contractMetaData != null) {
            String category = contractMetaData.getCategory() == null ? CATEGORY_DEFAULT : contractMetaData.getCategory();
            String name = contractMetaData.getName().substring(0, contractMetaData.getName().length() - 1);
            int order = contractMetaData.getShowOrder() == null ? Integer.MAX_VALUE : contractMetaData.getShowOrder().intValue();
            timeHelperResults.add(
                    TimeHelperResult.builder()
                            .category(category)
                            .code(signTimeCode)
                            .value(signTimeVal)
                            .name(name)
                            .order(order)
                            .build()
            );
        }

        for (int i = contractMetaDatas.size() - 1; i >= 0; i--) {
            String code = contractMetaDatas.get(i).getCode();
            if (code.equals("rent_start_time_year")
                    || code.equals("rent_start_time_month")
                    || code.equals("rent_start_time_day")
                    || code.equals("rent_end_time_year")
                    || code.equals("rent_end_time_month")
                    || code.equals("rent_end_time_day")
                    || code.equals("sign_time_year")
                    || code.equals("sign_time_month")
                    || code.equals("sign_time_day")) {
                log.debug("index[{}],code[{}]", i, code);
                contractMetaDatas.remove(i);
                //  i--;
            }
        }
        return timeHelperResults;
    }
    //endregion

    @Data
    @Builder
    private static class ContractHelperResult {
        Map<String, Object> valueMap;
        String template;
        List<ContractMetaData> contractMetaDatas;
    }

    @Data
    @Builder
    private static class TimeHelperResult {
        private String category;
        private String code;
        private String name;
        private String value;
        private int order;
    }
}
