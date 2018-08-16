package com.duobao.service;

import com.duobao.entity.Business;
import com.duobao.entity.BusinessUserRelation;

import java.util.List;

public interface BusinessService {

    public boolean insertBusiness(Business business);

    /**
     * 修改各种调用量
     */
    public boolean updateBusinessCallCount(Integer id,String orgId, Integer callCount);

    public boolean updateBusinessSucessCount(Integer id,String orgId, Integer sucessCount);

    public boolean updateBusinessPushCount(Integer id,String orgId, Integer pushCount);

    public boolean updateBusinessZmfCount(Integer id,String orgId, Integer zmfCount);

    public boolean updateBusinessHygzCount(Integer id,String orgId, Integer hygzCount);

    /**
     * 修改各种金额
     */
    public boolean updateBusinessTotalAmount(Integer id,String orgId, Double totalAmount);

    public boolean updateBusinessRemainAmount(Integer id,String orgId, Double remainAmount);

    public boolean updateBusinessZmfAmount(Integer id,String orgId, Double zmfAmount);

    public boolean updateBusinessHygzAmount(Integer id,String orgId, Double hygzAmount);

    /**把该商户置为无效*/
    public boolean becomeValidOrInVaild(Integer id,String orgId, Integer vaild);

    public boolean updateBusinessReturnUrl(Integer id,String orgId, String returnUrl);


    List<Business> getAllBusiness();

    Business getBusinessByOrgId(String orgId);

    Business getLastSecondRemainAmount(String orgId);

    boolean saveBusinessUserRelation(BusinessUserRelation businessUserRelation);
    String getOrgIdByCard(String card);

}
