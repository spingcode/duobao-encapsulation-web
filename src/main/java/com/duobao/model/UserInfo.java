package com.duobao.model;

import java.io.Serializable;

public class UserInfo implements Serializable{
    private String name;

    private String card;

    private String phone;

    private String returnUrl;

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getCard() {
        return card;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", card='" + card + '\'' +
                ", phone='" + phone + '\'' +
                ", returnUrl='" + returnUrl + '\'' +
                '}';
    }
}