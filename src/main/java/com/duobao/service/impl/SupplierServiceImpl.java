package com.duobao.service.impl;

import com.duobao.entity.Supplier;
import com.duobao.mapper.supplier.SupplierMapper;
import com.duobao.service.SupplierService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class SupplierServiceImpl implements SupplierService{

    @Autowired
    private SupplierMapper supplierMapper;
    @Override
    public boolean insertSupplier(Supplier supplier) {
        if (supplier == null || StringUtils.isBlank(supplier.getOrgid())
                || StringUtils.isBlank(supplier.getRc4Key())) {
            return false;
        }
        return supplierMapper.insertSupplier(supplier)>0;
    }
}
