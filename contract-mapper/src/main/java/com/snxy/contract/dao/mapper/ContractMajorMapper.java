package com.snxy.contract.dao.mapper;

import com.snxy.contract.domain.ContractMajor;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ContractMajorMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ContractMajor record);

    int insertSelective(ContractMajor record);

    ContractMajor selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ContractMajor record);

    int updateByPrimaryKey(ContractMajor record);


    int insertMajorContract(ContractMajor record, @Param(value = "insertSql") String insertSql);

    int insertByMap(Map<String, Object> map);

    int updateBySql(@Param(value = "updateSql") String updateSql);

    Integer getTemplateIdById(Long contractMajorId);

    Map<String, Object> selectBySql(@Param(value = "selectSql") String selectSql);

    List<ContractMajor> findOverlap(@Param("rentAreaId") Long rentAreaId, @Param("rentStartTime") String rentStartTime, @Param("rentEndTime") String rentEndTime, @Param("contractMajorId") Long contractMajorId);
}