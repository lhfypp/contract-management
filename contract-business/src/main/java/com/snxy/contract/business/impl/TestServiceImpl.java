package com.snxy.contract.business.impl;

import com.snxy.contract.dao.mapper.AnnouncementMapper;
import com.snxy.contract.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by 24398 on 2019/1/2.
 */
@Service
@Slf4j
public class TestServiceImpl implements TestService {

    @Resource
    private AnnouncementMapper announcementMapper;

    @Override
    public void test() {
      this.announcementMapper.list(false);
    }
}
