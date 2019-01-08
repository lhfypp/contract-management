package com.snxy.contract.dao.mapper;

import com.snxy.contract.domain.RentArea;

import java.util.List;

public interface RentAreaMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RentArea record);

    int insertSelective(RentArea record);

    RentArea selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RentArea record);

    int updateByPrimaryKey(RentArea record);

    List<RentArea> list();
}