package com.snxy.contract.business.utils;

import com.snxy.contract.domain.ContractFieldResult;
import com.snxy.contract.domain.ContractMetaData;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by lvhai on 2019/1/4.
 */
public class ContractSqlEngine {
    private static final String MAJOR_TABLE = "contract_major";
    private static final String PRIMARY_KEY = "id";
    private static final String SECONDARY_TABLE = "contract_fileld_result";

    public static String getMajorInsrtSql(List<ContractMetaData> contractMetaDatas, Map<String, Object> valueMap, Map<String, String> extraMap) {
        StringBuilder sqlFiled = new StringBuilder("insert into ");
        StringBuilder sqlValue = new StringBuilder();
        sqlFiled.append(MAJOR_TABLE);
        sqlFiled.append("(");
        sqlValue.append("values (");
        contractMetaDatas.forEach(cmd -> {
            if (cmd.getIsCommon() == 1) {
//                if (valueMap.containsKey(cmd.getCode())) {
                Object objectVal = valueMap.get(cmd.getCode());
                if (objectVal != null&& StringUtils.isNotBlank(objectVal.toString())) {
                    sqlFiled.append(cmd.getCode());
                    sqlFiled.append(",");
                    if (cmd.getDataType() == 2) {
                        sqlValue.append(objectVal.toString());
                    } else {
                        sqlValue.append("'");
                        sqlValue.append(objectVal.toString());
                        sqlValue.append("'");
                    }
                    sqlValue.append(",");
                }
            }
//            }
        });
        extraMap.forEach((k, v) -> {
            sqlFiled.append(k);
            sqlFiled.append(",");
            sqlValue.append("'");
            sqlValue.append(v);
            sqlValue.append("',");
        });
        sqlFiled.deleteCharAt(sqlFiled.length() - 1);
        sqlValue.deleteCharAt(sqlValue.length() - 1);
        sqlValue.append(")");
        sqlFiled.append(") ");
        return sqlFiled.toString() + sqlValue.toString();
    }

    public static List<ContractFieldResult> getContractFileldResult(List<ContractMetaData> contractMetaDatas, Map<String, Object> valueMap, Long contractMajorId) {
        List<ContractFieldResult> cfrs = new ArrayList<>();
        contractMetaDatas.parallelStream().forEach(cmd -> {
            if (cmd.getIsCommon() != 1) {
                Object objectVal = valueMap.get(cmd.getCode());
                if (objectVal != null&& StringUtils.isNotBlank(objectVal.toString())) {
                    ContractFieldResult cfr = ContractFieldResult.builder()
                            .contractFieldId(cmd.getId())
                            .contractMajorId(contractMajorId)
                            .fieldValue(objectVal.toString())
                            .isDelete((byte) 0)
                            .gmtCreate(new Date())
                            .gmtModified(new Date())
                            .build();
                    cfrs.add(cfr);
                }
            }
        });

        return cfrs;
    }

    public static String getUpdateSql(List<ContractMetaData> contractMetaDatas, Map<String, Object> valueMap, Map<String, String> extraMap, Long contractId) {
        StringBuilder sbUpdateSql = new StringBuilder();
        sbUpdateSql.append("update ");
        sbUpdateSql.append(MAJOR_TABLE);
        sbUpdateSql.append(" set ");
        contractMetaDatas.forEach(cmd -> {
            if (cmd.getIsCommon() == 1) {
                sbUpdateSql.append(cmd.getCode());
                sbUpdateSql.append("=");
                Object objectVal = valueMap.get(cmd.getCode());
                if (objectVal == null|| StringUtils.isBlank(objectVal.toString())) {
                    sbUpdateSql.append("null");
                } else {
                    if (cmd.getDataType() == 2) {
                        sbUpdateSql.append(objectVal.toString());
                    } else {
                        sbUpdateSql.append("'");
                        sbUpdateSql.append(objectVal.toString());
                        sbUpdateSql.append("'");
                    }
                }
                sbUpdateSql.append(",");

            }
        });
        extraMap.forEach((k, v) -> {
            sbUpdateSql.append(k);
            sbUpdateSql.append("=");
            if(StringUtils.isNotBlank(v)) {
                sbUpdateSql.append("'");
                sbUpdateSql.append(v);
                sbUpdateSql.append("',");
            }
            else{
                sbUpdateSql.append("null");
                sbUpdateSql.append(",");
            }
        });
        sbUpdateSql.deleteCharAt(sbUpdateSql.length() - 1);
        sbUpdateSql.append(" where id=");
        sbUpdateSql.append(contractId);
        return sbUpdateSql.toString();
    }

    public static String getSelectSql(List<ContractMetaData> contractMetaDatas, Long contractMajorId, List<String> extraFields) {
        StringBuilder sbSelectSql = new StringBuilder();
        sbSelectSql.append("select ");
        contractMetaDatas.forEach(cmd -> {
            if (cmd.getIsCommon() == 1) {
                sbSelectSql.append(cmd.getCode());
                sbSelectSql.append(",");
            }
        });
        extraFields.forEach( field -> {
            sbSelectSql.append(field);
            sbSelectSql.append(",");
        });
        sbSelectSql.deleteCharAt(sbSelectSql.length() - 1);
        sbSelectSql.append(" from ");
        sbSelectSql.append(MAJOR_TABLE);
        sbSelectSql.append(" where  ");
        sbSelectSql.append(PRIMARY_KEY);
        sbSelectSql.append("=");
        sbSelectSql.append(contractMajorId);
        return sbSelectSql.toString();
    }
}
