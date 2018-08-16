package com.duobao.model;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

public class ResultModel implements Serializable{
    private int status;
    private String message;
    private Info info;

    public void setInfo(Info info) {
        this.info = info;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public Info getInfo() {
        return info;
    }

    public String getMessage() {
        return message;
    }

    public static String wrapError() {
        ResultModel resultModel = new ResultModel();
        resultModel.setStatus(0);
        resultModel.setMessage("数据格式错误");
        return JSON.toJSONString(resultModel);
    }
}
