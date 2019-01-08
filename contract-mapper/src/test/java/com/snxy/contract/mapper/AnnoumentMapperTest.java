package com.snxy.contract.mapper;

import com.snxy.contract.dao.mapper.AnnouncementMapper;
import com.snxy.contract.domain.Announcement;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 24398 on 2019/1/2.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class AnnoumentMapperTest {

    @Resource
    private AnnouncementMapper announcementMapper;

    @Test
    public void listTest(){
        List<Announcement> list = announcementMapper.list(false);
        log.info("listSize : [{}]",list.size());
        list.forEach(announcement -> System.out.println(announcement));
    }



}
