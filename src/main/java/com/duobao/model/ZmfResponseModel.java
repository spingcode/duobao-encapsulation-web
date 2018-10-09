package com.duobao.model;

import java.io.Serializable;

public class ZmfResponseModel implements Serializable{


    private String status;
    private String orgid;
    private String data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
