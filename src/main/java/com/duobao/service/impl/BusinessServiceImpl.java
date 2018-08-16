package com.duobao.service.impl;

import com.duobao.entity.Business;
import com.duobao.entity.BusinessUserRelation;
import com.duobao.mapper.business.BusinessMapper;
import com.duobao.mapper.business.BusinessUserRelationMapper;
import com.duobao.service.BusinessService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("businessServiceImpl")
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private BusinessMapper businessMapper;

    @Autowired
    private BusinessUserRelationMapper businessUserRelationMapper;
    @Override
    public boolean insertBusiness(Business business) {
        if (business == null || StringUtils.isBlank(business.getOrgid())) {
            return false;
        }
        initBusinessAmount(business);
        if (business.getRemainAmount() == null || business.getRemainAmount() <= 0) {
            //1、表示无效
            business.setVaild(1);
        } else {
            //0、表示有效
            business.setVaild(0);
        }
        if (StringUtils.isBlank(business.getReturnUrl())) {
            business.setReturnUrl("www.baidu.com");
        }
        return businessMapper.insert(business)>0;
    }

    private void initBusinessAmount(Business business) {
        if (business.getTotalAmount() == null) {
            business.setTotalAmount(Double.valueOf(0));
        }
        if (business.getRemainAmount() == null) {
            business.setRemainAmount(Double.valueOf(0));
        }
        if (business.getZmfPrice() == null) {
            business.setZmfPrice(0.5);
        }
        if (business.getTotalAmount() == null) {
            business.setHygzPrice(0.3);
        }
    }


    @Override
    public boolean updateBusinessCallCount(Integer id,String orgId,Integer callCount) {
        if (StringUtils.isBlank(orgId) || callCount <= 0) {
            return false;
        }
        Business business = new Business();
        business.setOrgid(orgId);
        business.setCallCount(callCount);
        business.setId(id);
        return businessMapper.updateBusinessByOrgId(business)>0;
    }

    @Override
    public boolean updateBusinessSucessCount(Integer id,String orgId,Integer sucessCount) {
        if (StringUtils.isBlank(orgId) || sucessCount==null||sucessCount <= 0) {
            return false;
        }
        Business business = new Business();
        business.setOrgid(orgId);
        business.setSuccessCount(sucessCount);
        business.setId(id);
        return businessMapper.updateBusinessByOrgId(business)>0;
    }

    @Override
    public boolean updateBusinessPushCount(Integer id,String orgId,Integer pushCount) {
        if (StringUtils.isBlank(orgId) || pushCount==null||pushCount <= 0) {
            return false;
        }
        Business business = new Business();
        business.setOrgid(orgId);
        business.setPullCount(pushCount);
        business.setId(id);
        return businessMapper.updateBusinessByOrgId(business)>0;
    }

    @Override
    public boolean updateBusinessZmfCount(Integer id,String orgId,Integer zmfCount) {
        if (StringUtils.isBlank(orgId) || zmfCount==null||zmfCount <= 0) {
            return false;
        }
        Business business = new Business();
        business.setOrgid(orgId);
        business.setZmfCount(zmfCount);
        business.setId(id);
        return businessMapper.updateBusinessByOrgId(business)>0;
    }

    @Override
    public boolean updateBusinessHygzCount(Integer id,String orgId,Integer hygzCount) {
        if (StringUtils.isBlank(orgId) ||hygzCount==null|| hygzCount <= 0) {
            return false;
        }
        Business business = new Business();
        business.setOrgid(orgId);
        business.setHygzCount(hygzCount);
        business.setId(id);
        return businessMapper.updateBusinessByOrgId(business)>0;
    }

    @Override
    public boolean updateBusinessTotalAmount(Integer id,String orgId,Double totalAmount) {
        if (StringUtils.isBlank(orgId) || totalAmount == null || totalAmount <= 0) {
            return false;
        }
        Business business = new Business();
        business.setOrgid(orgId);
        business.setTotalAmount(totalAmount);
        business.setId(id);
        return businessMapper.updateBusinessByOrgId(business)>0;
    }

    @Override
    public boolean updateBusinessRemainAmount(Integer id,String orgId,Double remainAmount) {
        if (StringUtils.isBlank(orgId) || remainAmount == null || remainAmount <= 0) {
            return false;
        }
        Business business = new Business();
        business.setOrgid(orgId);
        business.setRemainAmount(remainAmount);
        business.setId(id);
        return businessMapper.updateBusinessByOrgId(business)>0;
    }

    @Override
    public boolean updateBusinessZmfAmount(Integer id,String orgId,Double zmfAmount) {
        if (StringUtils.isBlank(orgId) || zmfAmount == null || zmfAmount <= 0) {
            return false;
        }
        Business business = new Business();
        business.setOrgid(orgId);
        business.setZmfPrice(zmfAmount);
        business.setId(id);
        return businessMapper.updateBusinessByOrgId(business)>0;
    }

    @Override
    public boolean updateBusinessHygzAmount(Integer id,String orgId,Double hygzAmount) {
        if (StringUtils.isBlank(orgId) || hygzAmount == null || hygzAmount <= 0) {
            return false;
        }
        Business business = new Business();
        business.setOrgid(orgId);
        business.setHygzPrice(hygzAmount);
        business.setId(id);
        return businessMapper.updateBusinessByOrgId(business)>0;
    }

    @Override
    public boolean becomeValidOrInVaild(Integer id,String orgId,Integer vaild) {

        if (StringUtils.isBlank(orgId) || vaild == null) {
            return false;
        }
        Business business = new Business();
        business.setOrgid(orgId);
        business.setVaild(vaild);
        business.setId(id);
        return businessMapper.updateBusinessByOrgId(business)>0;
    }

    @Override
    public boolean updateBusinessReturnUrl(Integer id,String orgId, String returnUrl) {
        if (StringUtils.isBlank(orgId) || StringUtils.isBlank(returnUrl)) {
            return false;
        }
        Business business = new Business();
        business.setOrgid(orgId);
        business.setReturnUrl(returnUrl);
        business.setId(id);
        return businessMapper.updateBusinessByOrgId(business)>0;
    }

    @Override
    public List<Business> getAllBusiness() {
        return businessMapper.getAllBusiness();
    }

    @Override
    public Business getBusinessByOrgId(String orgId) {
        if (StringUtils.isBlank(orgId)) {
            return null;
        }
        return businessMapper.selectByOrgId(orgId);
    }

    @Override
    public Business getLastSecondRemainAmount(String orgId) {
        if (StringUtils.isBlank(orgId)) {
            return null;
        }
        return businessMapper.selectLastSecondRemainAmount(orgId);
    }

    @Override
    public boolean saveBusinessUserRelation(BusinessUserRelation businessUserRelation) {
        if (businessUserRelation == null || StringUtils.isBlank(businessUserRelation.getOrgid())
                || StringUtils.isBlank(businessUserRelation.getCard())) {
            return false;
        }
        return businessUserRelationMapper.insert(businessUserRelation)>0;
    }

    @Override
    public String getOrgIdByCard(String card) {
        if (StringUtils.isBlank(card)) {
            return null;
        }
        return businessUserRelationMapper.selectOrgIdByCard(card);
    }

}
