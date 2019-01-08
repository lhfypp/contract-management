package com.snxy.contract.business.impl;

import com.snxy.common.exception.BizException;
import com.snxy.contract.dao.mapper.MerchantCompanyMapper;
import com.snxy.contract.dao.mapper.OnlineUserMapper;
import com.snxy.contract.domain.MerchantCompany;
import com.snxy.contract.domain.OnlineUser;
import com.snxy.contract.service.MerchantCompanyService;
import com.snxy.contract.service.vo.MerchantCompanyVo;
import com.snxy.contract.service.vo.OnlineUserVo;
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
public class MerchantCompanyImpl implements MerchantCompanyService {
    private static final int  FOUNDER = 1;

    @Resource
    private MerchantCompanyMapper merchantCompanyMapper;
    @Resource
    private OnlineUserMapper onlineUserMapper;

    @Override
    public List<MerchantCompanyVo> list() {
        List<MerchantCompany> mcs = merchantCompanyMapper.list();
        List<MerchantCompanyVo> mcvos = mcs.parallelStream().map(mc ->
        {
            MerchantCompanyVo mcvo = new MerchantCompanyVo();
            BeanUtils.copyProperties(mc, mcvo);
            return mcvo;
        }).collect(Collectors.toList());
        return mcvos;
    }

    @Override
    public List<MerchantCompanyVo> listByCompanyName(String companyName) {
        return null;
    }

    @Override
    public OnlineUserVo getFounderByCompanyId(Integer companyId) {
        OnlineUser user=onlineUserMapper.getFounderUserByCompanyId(companyId,FOUNDER);
        if(user==null){
            log.warn("没有找到公司id为{}对应的主负责人",companyId);
            throw  new BizException(String.format("没有找到公司id为%d对应的主负责人",companyId));
        }
        OnlineUserVo onlineUserVo=new OnlineUserVo();
        BeanUtils.copyProperties(user,onlineUserVo);
        return onlineUserVo;
    }
}
