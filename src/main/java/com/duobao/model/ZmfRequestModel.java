package com.duobao.model;

import java.io.Serializable;

public class ZmfRequestModel implements Serializable {
    private String orgid;
    private String data;

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOrgid() {
        return orgid;
    }

    public String getData() {
        return data;
    }
}
