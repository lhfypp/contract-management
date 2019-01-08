package com.snxy.contract.dao.mapper;

import com.snxy.contract.domain.OnlineUser;
import org.apache.ibatis.annotations.Param;

public interface OnlineUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OnlineUser record);

    int insertSelective(OnlineUser record);

    OnlineUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OnlineUser record);

    int updateByPrimaryKey(OnlineUser record);

    OnlineUser getFounderUserByCompanyId(@Param("companyId") Integer companyId
            ,@Param("founder")  Integer founder );
}