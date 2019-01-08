package com.snxy.contract.dao.mapper;

import com.snxy.contract.domain.Announcement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AnnouncementMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Announcement record);

    int insertSelective(Announcement record);

    Announcement selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Announcement record);

    int updateByPrimaryKey(Announcement record);

    List<Announcement> list(@Param("isDelete") boolean isDelete);

    Long selectNum(@Param("isDelete") boolean isDelete);
}