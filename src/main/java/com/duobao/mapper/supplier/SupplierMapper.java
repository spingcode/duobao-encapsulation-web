package com.duobao.mapper.supplier;

import com.duobao.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SupplierMapper {

    int insertSupplier(Supplier record);

    Supplier selecAllsupplier();

}