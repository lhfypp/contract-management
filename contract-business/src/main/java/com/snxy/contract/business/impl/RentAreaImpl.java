package com.snxy.contract.business.impl;

import com.snxy.contract.dao.mapper.RentAreaMapper;
import com.snxy.contract.dao.mapper.RentSiteMapper;
import com.snxy.contract.domain.RentArea;
import com.snxy.contract.domain.RentSite;
import com.snxy.contract.service.RentAreaService;
import com.snxy.contract.service.vo.RentAreaVo;
import com.snxy.contract.service.vo.RentSiteVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lvhai on 2019/1/7.
 */
@Service
@Slf4j
public class RentAreaImpl implements RentAreaService {
    @Resource
    private RentAreaMapper rentAreaMapper;
    @Resource
    private RentSiteMapper rentSiteMapper;

    @Override
    public List<RentAreaVo> list() {
        List<RentArea> rentAreas = rentAreaMapper.list();
        List<RentAreaVo> rentAreaVos =
                rentAreas.parallelStream().map(rentArea -> {
                    RentAreaVo rentAreaVo = new RentAreaVo();
                    BeanUtils.copyProperties(rentArea, rentAreaVo);
                    return rentAreaVo;
                }).collect(Collectors.toList());

        return rentAreaVos;
    }

    @Override
    public List<RentSiteVo> getRentSiteByRentAreaId(Long rentAreaId) {
        List<RentSite> rentSites = rentSiteMapper.getRentSiteByRentAreaId(rentAreaId);
        List<RentSiteVo> rentSiteVos = rentSites.parallelStream().map(rentSite -> {
            RentSiteVo rentSiteVo = new RentSiteVo();
            BeanUtils.copyProperties(rentSite, rentSiteVo);
            return rentSiteVo;
        }).collect(Collectors.toList());

        return rentSiteVos;
    }
}
