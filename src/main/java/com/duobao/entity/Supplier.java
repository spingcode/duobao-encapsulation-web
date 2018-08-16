package com.duobao.entity;

import java.io.Serializable;

public class Supplier implements Serializable {
    private Integer id;

    private String orgid;

    private String rc4Key;

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

    public String getRc4Key() {
        return rc4Key;
    }

    public void setRc4Key(String rc4Key) {
        this.rc4Key = rc4Key == null ? null : rc4Key.trim();
    }
}