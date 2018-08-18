package com.duobao.controller;

import com.duobao.entity.Business;
import com.duobao.service.BusinessService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("freemarker")
public class FreemarkerController {

    @Autowired
    private BusinessService businessService;
    @RequestMapping("getAllBusiness")
    public String getAllBusiness(Map<String,Object> map) {
        List<Business> allBusiness = businessService.getAllBusiness();
        map.put("msg", allBusiness);
        return "hello";
    }


    @RequestMapping("edit")
    public String editBusiness(Map<String,Object> map,Integer businessId) {
        Business business = businessService.getBusinessById(businessId);
        map.put("msg", business);
        return "editBusiness";
    }

    @RequestMapping("details")
    public String detailsBusiness(Map<String,Object> map,Integer businessId) {
        Business business = businessService.getBusinessById(businessId);
        map.put("msg", business);
        return "detailsBusiness";
    }
    @RequestMapping("saveBusiness")
    public String saveBusiness(Map<String,Object> map,Business business) {
        businessService.updateBusiness(business);
        return getAllBusiness(map);
    }

    @RequestMapping("getHistoryByOrgid")
    public String getHistoryByOrgid(Map<String,Object> map,String orgid) {
        if (StringUtils.isBlank(orgid)) {
            return null;
        }
        List<Business> businessList = businessService.getAllBusinessByOrgId(orgid);
        map.put("msg", businessList);
        return "history";
    }


}
