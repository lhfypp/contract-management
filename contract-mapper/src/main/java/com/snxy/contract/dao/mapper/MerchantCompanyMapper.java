package com.snxy.contract.dao.mapper;

import com.snxy.contract.domain.MerchantCompany;

import java.util.List;

public interface MerchantCompanyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MerchantCompany record);

    int insertSelective(MerchantCompany record);

    MerchantCompany selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MerchantCompany record);

    int updateByPrimaryKey(MerchantCompany record);

    List<MerchantCompany> list();
}