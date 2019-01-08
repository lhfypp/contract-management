package com.snxy.contract.web;

import com.snxy.common.exception.BizException;
import com.snxy.common.response.ResultData;
import com.snxy.contract.service.ContractMajorService;
import com.snxy.contract.service.MerchantCompanyService;
import com.snxy.contract.service.RentAreaService;
import com.snxy.contract.service.vo.ContractEditVo;
import com.snxy.contract.service.vo.OnlineUserVo;
import com.snxy.contract.service.vo.RentSiteVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by lvhai on 2019/1/3.
 */
@Controller
@Slf4j
@RequestMapping(value = "/contract")
public class ContractController {

    @Resource
    private ContractMajorService contractMajorService;
    @Resource
    private MerchantCompanyService merchantCompanyService;
    @Resource
    private RentAreaService rentAreaService;

    @RequestMapping(value = "edit")
    public String contractEdit(@RequestParam(value = "id", required = false) Long id,
                               @RequestParam(value = "templateId", required = false) Integer templateId,
                               @RequestParam(value = "companyId", required = false) Integer companyId,
                               @RequestParam(value = "creator",  required = false) Integer creator,
                               ModelMap modelMap) {
        //加载公司
        modelMap.put("companys", merchantCompanyService.list());
        //加载区域
        modelMap.put("areas", rentAreaService.list());

        ContractEditVo contractEditVo = null;
        if (id != null) {
            modelMap.put("id", id);
            contractEditVo = contractMajorService.contractEdit(id);

            modelMap.put("valueMap", contractEditVo.getValueMap());
            Long rentAreaId= contractEditVo.getValueMap().get("rent_area_id")==null? null:Long.valueOf(contractEditVo.getValueMap().get("rent_area_id").toString());
            if(rentAreaId!=null){

                List<RentSiteVo> rentSites = rentAreaService.getRentSiteByRentAreaId(rentAreaId);
                modelMap.put("rentSites",rentSites);
                log.debug("rentAreaId:{},rentSites数量",rentAreaId,rentSites.size());
            }
        } else {
            if (templateId != null) {
                modelMap.put("templateId", templateId);
                if (companyId != null) {
                    OnlineUserVo onlineUserVo = merchantCompanyService.getFounderByCompanyId(companyId);
                    modelMap.put("companyId", companyId);
                    modelMap.put("onlineUserVo", onlineUserVo);//
                }
                contractEditVo = contractMajorService.contractAdd(templateId);
            } else {
                throw new BizException("合同id和模版id不能同时为空");
            }
        }
        modelMap.put("creator", creator);
        modelMap.put("contractEdit", contractEditVo.getEditView());
        modelMap.put("metaDatas", contractEditVo.getJsCmds());
        return "contractEdit";
    }

    @PostMapping("/save")
    @ResponseBody
    public ResultData<Long> saveContract(@RequestParam Map<String, Object> paraMap) {
        ///TODO 前端传入参数,暂时当普通参数处理

        Object objCreator = paraMap.get("creator");
        long creator = Long.parseLong(objCreator.toString());


        if (paraMap != null) {
            paraMap.forEach((k, v) -> {
                log.debug("k:{},v:{}", k, v);
            });
        }
//        return ResultData.success(Long.valueOf("1"));
        return ResultData.success(contractMajorService.saveContract(creator, paraMap));
    }

    @RequestMapping(value = "/view")
    public String viewContract(@RequestParam(value = "id") Long id
            , ModelMap modelMap) {
        modelMap.put("contractView", contractMajorService.getContractView(id));
        return "contractView";
    }

    @RequestMapping(value = "/getFounder")
    @ResponseBody
    public ResultData<OnlineUserVo> getFounder(@RequestParam(value = "companyId") Integer companyId) {
        OnlineUserVo onlineUserVo = merchantCompanyService.getFounderByCompanyId(companyId);
        return ResultData.success(onlineUserVo);
    }

    @RequestMapping(value = "/getRentSite")
    @ResponseBody
    public ResultData<List<RentSiteVo>> getRentSite(@RequestParam(value = "rentAreaId") Long rentAreaId) {
        List<RentSiteVo> rentSites = rentAreaService.getRentSiteByRentAreaId(rentAreaId);
        return ResultData.success(rentSites);
    }
}
