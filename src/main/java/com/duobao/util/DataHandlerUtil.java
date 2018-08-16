package com.duobao.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duobao.model.*;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class DataHandlerUtil {

    private static Logger logger = LoggerFactory.getLogger(DataHandlerUtil.class);
    private static String RC4_KEY="UCJYLVVL";
    private static String orgId="duobao";
    private static String returnUrl="http://118.24.127.29/business/zhiMaFenCallBackHL?";
    public static ZmfDecodeModel decode(String param) throws UnsupportedEncodingException {
        ZmfRequestModel zmfRequestModel = JSON.parseObject(param, ZmfRequestModel.class);
        String data = zmfRequestModel.getData();
        String decodeString =Base64Utils.decode(data);
        data = Ts.decry_RC4(decodeString, RC4_KEY);
        UserInfo userInfo = JSON.parseObject(data, UserInfo.class);
        ZmfDecodeModel zmfDecodeModel = new ZmfDecodeModel();
        zmfDecodeModel.setOrgid(zmfRequestModel.getOrgid());
        zmfDecodeModel.setUserInfo(userInfo);
        return zmfDecodeModel;
    }

    public static String encode(ZmfDecodeModel zmfDecodeModel) throws UnsupportedEncodingException {
        UserInfo userInfo = zmfDecodeModel.getUserInfo();
        JSONObject json = new JSONObject();
        json.put("orgid", orgId);
        JSONObject jsonSec = new JSONObject();
        jsonSec.put("name", userInfo.getName());
        jsonSec.put("card", userInfo.getCard());
        jsonSec.put("phone", userInfo.getPhone());
        jsonSec.put("returnUrl", returnUrl);
        logger.info("得到我将要发送给供应商未加密的数据：data={}",jsonSec.toJSONString());
        byte[] bytes = Ts.encry_RC4_byte(jsonSec.toJSONString(), RC4_KEY);
        String sign = Base64Utils.encodeBase64(bytes);
        json.put("data", sign);
        return json.toJSONString();
    }
    public static String encodeHygz(ZmfDecodeModel zmfDecodeModel) throws UnsupportedEncodingException {
        UserInfo userInfo = zmfDecodeModel.getUserInfo();
        JSONObject json = new JSONObject();
        json.put("orgid", orgId);
        JSONObject jsonSec = new JSONObject();
        jsonSec.put("name", userInfo.getName());
        jsonSec.put("card", userInfo.getCard());
        jsonSec.put("phone", userInfo.getPhone());
        logger.info("encodeHygz,得到我将要发送给供应商未加密的数据：data={}",jsonSec.toJSONString());
        byte[] bytes = Ts.encry_RC4_byte(jsonSec.toJSONString(), RC4_KEY);
        String sign = Base64Utils.encodeBase64(bytes);
        json.put("data", sign);
        return json.toJSONString();
    }
    public static ZmfResultModel decodeZmf(String param) throws UnsupportedEncodingException {
        byte[] tt =  Base64.decodeBase64(param);
        String result = Ts.decry_RC4(tt, "UCJYLVVL");
        logger.info("得到供应商调用我的returnUrl给我的解密之后的数据（字符串）：result={}",result);
        return JSON.parseObject(result, ZmfResultModel.class);
    }

    public static String decodeResult(ZmfResultModel zmfResultModel) throws UnsupportedEncodingException {
        JSONObject jsonSec = new JSONObject();
        jsonSec.put("name", zmfResultModel.getData().getName());
        jsonSec.put("card", zmfResultModel.getData().getCard());
        jsonSec.put("phone", zmfResultModel.getData().getPhone());
        jsonSec.put("zmf",zmfResultModel.getData().getZmf());
        String s = Ts.encry_RC4_string(jsonSec.toJSONString(), RC4_KEY);
        return Base64Utils.encodeBase64(s.getBytes());
    }

}