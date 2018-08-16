package com.duobao.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Business implements Serializable {
    private Integer id;

    private String orgid;

    private Integer callCount;

    private Integer successCount;

    private Integer pullCount;

    private Integer zmfCount;

    private Integer hygzCount;

    private Double remainAmount;

    private Double totalAmount;

    private Double zmfPrice;

    private Double hygzPrice;

    private Integer vaild;

    private String returnUrl;

    private Date createTime;

    private Date dbUpdateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid == null ? null : orgid.trim();
    }

    public Integer getCallCount() {
        return callCount;
    }

    public void setCallCount(Integer callCount) {
        this.callCount = callCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getPullCount() {
        return pullCount;
    }

    public void setPullCount(Integer pullCount) {
        this.pullCount = pullCount;
    }

    public Integer getZmfCount() {
        return zmfCount;
    }

    public void setZmfCount(Integer zmfCount) {
        this.zmfCount = zmfCount;
    }

    public Integer getHygzCount() {
        return hygzCount;
    }

    public void setHygzCount(Integer hygzCount) {
        this.hygzCount = hygzCount;
    }

    public Double getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(Double remainAmount) {
        this.remainAmount = remainAmount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getZmfPrice() {
        return zmfPrice;
    }

    public void setZmfPrice(Double zmfPrice) {
        this.zmfPrice = zmfPrice;
    }

    public Double getHygzPrice() {
        return hygzPrice;
    }

    public void setHygzPrice(Double hygzPrice) {
        this.hygzPrice = hygzPrice;
    }

    public Integer getVaild() {
        return vaild;
    }

    public void setVaild(Integer vaild) {
        this.vaild = vaild;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getDbUpdateTime() {
        return dbUpdateTime;
    }

    public void setDbUpdateTime(Date dbUpdateTime) {
        this.dbUpdateTime = dbUpdateTime;
    }

    @Override
    public String toString() {
        return "Business{" +
                "id=" + id +
                ", orgid='" + orgid + '\'' +
                ", callCount=" + callCount +
                ", successCount=" + successCount +
                ", pullCount=" + pullCount +
                ", zmfCount=" + zmfCount +
                ", hygzCount=" + hygzCount +
                ", remainAmount=" + remainAmount +
                ", totalAmount=" + totalAmount +
                ", zmfPrice=" + zmfPrice +
                ", hygzPrice=" + hygzPrice +
                ", vaild=" + vaild +
                ", returnUrl='" + returnUrl + '\'' +
                ", createTime=" + createTime +
                ", dbUpdateTime=" + dbUpdateTime +
                '}';
    }
}