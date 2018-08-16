package com.duobao.model;

import java.io.Serializable;

public class ZmfDecodeModel implements Serializable{
    private String orgid;

    private UserInfo userInfo;

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getOrgid() {
        return orgid;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public String toString() {
        return "ZmfDecodeModel{" +
                "orgid='" + orgid + '\'' +
                ", userInfo=" + userInfo +
                '}';
    }
}
