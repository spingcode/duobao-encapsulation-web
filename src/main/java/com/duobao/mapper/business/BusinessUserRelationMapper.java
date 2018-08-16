package com.duobao.mapper.business;

import com.duobao.entity.BusinessUserRelation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BusinessUserRelationMapper {
    int insert(BusinessUserRelation businessUserRelation);


    String selectOrgIdByCard(String card);

}