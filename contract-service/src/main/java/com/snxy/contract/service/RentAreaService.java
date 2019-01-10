package com.snxy.contract.service;

import com.snxy.contract.domain.RentArea;
import com.snxy.contract.service.vo.RentAreaVo;
import com.snxy.contract.service.vo.RentSiteVo;

import java.util.List;

/**
 * Created by lvhai on 2019/1/7.
 */
public interface RentAreaService {
    List<RentAreaVo> list();

    List<RentSiteVo> getRentSiteByRentAreaId(Long rentAreaId);

    RentArea getRentAreaById(Long rentId);
}
