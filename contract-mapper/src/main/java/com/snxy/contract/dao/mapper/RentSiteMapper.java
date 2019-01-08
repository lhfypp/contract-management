package com.snxy.contract.dao.mapper;

import com.snxy.contract.domain.RentSite;

import java.util.List;

public interface RentSiteMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RentSite record);

    int insertSelective(RentSite record);

    RentSite selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RentSite record);

    int updateByPrimaryKey(RentSite record);

    List<RentSite> getRentSiteByRentAreaId(Long rentAreaId);
}