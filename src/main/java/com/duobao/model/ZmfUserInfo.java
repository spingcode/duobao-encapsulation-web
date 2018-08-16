package com.duobao.model;

import java.io.Serializable;

public class ZmfUserInfo implements Serializable{

    private String name;

    private String card;

    private String phone;

    private String zmf;

    private static final long serialVersionUID = 1L;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card == null ? null : card.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getZmf() {
        return zmf;
    }

    public void setZmf(String zmf) {
        this.zmf = zmf == null ? null : zmf.trim();
    }

    @Override
    public String toString() {
        return "ZmfUserInfo{" +
                "name='" + name + '\'' +
                ", card='" + card + '\'' +
                ", phone='" + phone + '\'' +
                ", zmf='" + zmf + '\'' +
                '}';
    }
}
