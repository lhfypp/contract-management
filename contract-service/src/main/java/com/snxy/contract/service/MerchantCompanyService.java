package com.snxy.contract.service;

import com.snxy.contract.service.vo.MerchantCompanyVo;
import com.snxy.contract.service.vo.OnlineUserVo;

import java.util.List;

/**
 * Created by lvhai on 2019/1/7.
 */
public interface MerchantCompanyService {
    List<MerchantCompanyVo> list();

    List<MerchantCompanyVo> listByCompanyName(String companyName);

    OnlineUserVo getFounderByCompanyId(Integer companyId);
}
