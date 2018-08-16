package com.duobao.mapper.business;

import com.duobao.entity.Business;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BusinessMapper {

    int insert(Business business);

    int updateBusinessByOrgId(Business business);

    Business selectByOrgId(String orgId);

    Business selectLastSecondRemainAmount(String orgId);

    List<Business> getAllBusiness();

}