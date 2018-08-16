package com.duobao.model;

import java.io.Serializable;

public class ZmfResultModel implements Serializable{


    private String status;
    private String orgid;
    private ZmfUserInfo data;

    public void setData(ZmfUserInfo data) {
        this.data = data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public ZmfUserInfo getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public String getOrgid() {
        return orgid;
    }

    @Override
    public String toString() {
        return "ZmfResultModel{" +
                "status='" + status + '\'' +
                ", orgid='" + orgid + '\'' +
                ", data=" + data +
                '}';
    }
}
