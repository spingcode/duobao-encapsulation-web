package com.duobao.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duobao.Cache.LocalCacheUtils;
import com.duobao.entity.Business;
import com.duobao.entity.User;
import com.duobao.model.*;
import com.duobao.redis.RedisUtil;
import com.duobao.service.BusinessService;
import com.duobao.service.UserService;
import com.duobao.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping(value = "/business/java/")
public class BusinessJAVAController {
    Logger logger = LoggerFactory.getLogger(BusinessJAVAController.class);
    @Autowired
    private BusinessService businessService;
    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    String pushMessageUrl = "http://www.hlqz.top//index.php?g=Interfaces&m=Zmf&a=zmf_url";
    String obtainZmfUrl="http://www.hlqz.top//index.php?g=Interfaces&m=ZmfMf&a=getZmf";
    String hygzUrl = "http://www.hlqz.top//index.php?g=Interfaces&m=Hydt&a=zs_hydt";
    @RequestMapping(value = "/authentication", method = RequestMethod.POST)
    public String authentication(String param) {
        //1、获取B端数据
        try {
            if (StringUtils.isBlank(param)) {
                return ResultModel.wrapError("参数为空");
            }
            param = param.replaceAll(" ", "+");
            logger.info("获取B端请求用户的加密数据：param={}", param);
            ZmfDecodeModel zmfDecodeModel = DataHandlerJAVAUtil.decode(param);
            logger.info("authentication，得到B端请求用户的解密数据，zmfDecodeModel={}", zmfDecodeModel);
            if (zmfDecodeModel == null || StringUtils.isBlank(zmfDecodeModel.getOrgid())) {
                return ResultModel.wrapError();
            }
            UserInfo userInfo = zmfDecodeModel.getUserInfo();
            if (StringUtils.isBlank(userInfo.getCard())
                    || StringUtils.isBlank(userInfo.getName())
                    || StringUtils.isBlank(userInfo.getPhone())) {
                return ResultModel.wrapError();
            }
            String url = zmfDecodeModel.getUserInfo().getReturnUrl();
            String key = null;
            try {
                if (StringUtils.isBlank(key)) {
                    key = "key=" + UUID.randomUUID().toString();
                }
                if (StringUtils.isNotBlank(key)) {
                    redisUtil.set(key, url);
                    redisUtil.setKeyExpire(key, 2);
                    logger.info("key={},redisValue={}", key,redisUtil.get(key));
                }
            } catch (Exception e) {

            }
            //2、把数据保存到我们的数据库
            /*2.1、校验该用户有没有资格访问*/
            Business business = businessService.getBusinessByOrgId(zmfDecodeModel.getOrgid());
            logger.info("authentication，得到B端的机构的调用数据，business={}", business);
            if (business == null || business.getVaild().equals(1) || business.getRemainAmount() <= 0) {
                return ResultModel.wrapError();
            }
            Date date = business.getCreateTime();
            business.setCallCount(business.getCallCount() + 1);
            /*2.2、保存B端数据到数据库*/
            if (DateUtil.compare(date.getTime(), DateUtil.getCurrent0Time()) > 0) {
                businessService.updateBusinessCallCount(business.getId(), business.getOrgid(), business.getCallCount());
            } else {
                businessService.insertBusiness(business);
            }
            logger.info("authentication，得到B端的机构将要保存到数据库的数据，business={}", business);
            /*3、把我的数据发送给供应商*/
            String sendParam = DataHandlerJAVAUtil.encode(zmfDecodeModel,key);
            logger.info("得到我将要发送给供应商的加密数据，sendParam={}", sendParam);
            String result = HttpUtils.sendPost(sendParam, pushMessageUrl);
            logger.info("得到供应商返回给我的认证的URL，result={}", result);
            return result;
        } catch (Exception e) {
            logger.warn("getZmf，认证之前的调用抛异常：", e);
            return ResultModel.wrapError();
        }
    }
    @GetMapping(value = "/callUrl")
    public String callUrl(String key, HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(key)) {
                return ResultModel.wrapError();
            }
            logger.info("key{}",key);
            key = key.replaceAll(" ","+");
            String returnUrlCache = "";
            if (StringUtils.isNotBlank(key)) {
                returnUrlCache = redisUtil.get("key=" + key);
            }
            logger.info("重定向到B端用户的URL:{}",returnUrlCache);
            response.sendRedirect(returnUrlCache);
            return null;
        } catch (Exception e) {
            logger.warn("returnUrl方法抛异常：",e);
            return ResultModel.wrapError();
        }
    }

    @RequestMapping(value = "obtainZmf",method = RequestMethod.POST)
    public String obtainZmf(String param,HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(param)) {
                return ResultModel.wrapError("参数为空");
            }
            param = param.replaceAll(" ", "+");
            logger.info("获取B端请求用户的加密数据：param={}", param);
            ZmfDecodeModel zmfDecodeModel = DataHandlerJAVAUtil.decode(param);
            logger.info("obtainZmf，得到B端请求用户的解密数据，zmfDecodeModel={}", zmfDecodeModel);
            if (zmfDecodeModel == null || StringUtils.isBlank(zmfDecodeModel.getOrgid())) {
                return ResultModel.wrapError();
            }
            String orgid=zmfDecodeModel.getOrgid();
            UserInfo userInfo = zmfDecodeModel.getUserInfo();
            if (StringUtils.isBlank(userInfo.getCard())
                    || StringUtils.isBlank(userInfo.getName())
                    || StringUtils.isBlank(userInfo.getPhone())) {
                return ResultModel.wrapError();
            }
            //2、把数据保存到我们的数据库
            /*2.1、校验该用户有没有资格访问*/
            Business business = businessService.getBusinessByOrgId(orgid);
            logger.info("obtainZmf，得到B端的机构的调用数据，business={}", business);
            if (business == null || business.getVaild().equals(1) || business.getRemainAmount() <= 0) {
                return ResultModel.wrapError();
            }
            String sendParam = DataHandlerJAVAUtil.encode(zmfDecodeModel,"");
            logger.info("obtainZmf,得到我将要发送给供应商的加密数据，sendParam={}", sendParam);
            String result = HttpUtils.sendPost(sendParam, obtainZmfUrl);
            logger.info("obtainZmf,得到芝麻分数据，result={}", result);
            //1、得到芝麻分数据
            ZmfResultModel zmfResultModel = JSON.parseObject(result, ZmfResultModel.class);
            logger.info("obtainZmf，得到芝麻分的数据（对象）：zmfResultModel={}",zmfResultModel);
            if (zmfResultModel == null) {
                return ResultModel.wrapError();
            }
            if (!zmfResultModel.getStatus().equals("success")) {
                return ResultModel.wrapError("芝麻分返回失败");
            }
            //2、保存数据到我们自己的表
            /*2.1 如果没有今天的记录，就新建一条*/
            Date date = business.getCreateTime();
            /*2.2、保存B端数据到数据库*/
            business.setSuccessCount(business.getSuccessCount() + 1);
            business.setPullCount(business.getPullCount()+1);
            business.setZmfCount(business.getZmfCount()+1);
            if (DateUtil.compare(date.getTime(), DateUtil.getCurrent0Time()) < 0) {
                businessService.insertBusiness(business);
            } else {
                businessService.updateBusiness(business);
            }
            logger.info("obtainZmf，得到B端的机构的调用数据，business={}",business);
            /*2.3、计算剩余价格*/

            /*2.4 保存用户数据*/
            User user = new User();
            user.setCard(zmfResultModel.getData().getCard());
            user.setName(zmfResultModel.getData().getName());
            user.setPhone(zmfResultModel.getData().getPhone());
            user.setZmf(zmfResultModel.getData().getZmf());
            userService.insertUser(user);
            logger.info("得到将要保存的用户数据，user={}",user);
            ZmfResponseModel zmfResponseModel = new ZmfResponseModel();
            zmfResponseModel.setData(JSON.toJSONString(zmfResultModel.getData()));
            zmfResponseModel.setOrgid(orgid);
            zmfResponseModel.setStatus(zmfResultModel.getStatus());
            String jsonString = JSON.toJSONString(zmfResponseModel);
            logger.info("把数据转化成json串，jsonString={}",jsonString);
            response.setContentType("text/json");
            response.setCharacterEncoding("UTF-8");
            return jsonString;
        } catch (Exception e) {
            logger.warn("obtainZmf,获取芝麻分抛异常，e={}", e);
            return ResultModel.wrapError();
        }
    }
    @RequestMapping(value = "/getHygz",method = RequestMethod.POST)
    public String getHygz(String param) {
        try {
            if (StringUtils.isBlank(param)) {
                return ResultModel.wrapError();
            }
            param = param.replaceAll(" ","+");
            logger.info("getHygz,获取B端请求用户的加密数据：param={}",param);
            ZmfDecodeModel zmfDecodeModel = DataHandlerJAVAUtil.decode(param);
            logger.info("getHygz，得到B端请求用户的解密数据，zmfDecodeModel={}",zmfDecodeModel);
            if (zmfDecodeModel == null || StringUtils.isBlank(zmfDecodeModel.getOrgid())) {
                return ResultModel.wrapError();
            }
            UserInfo userInfo = zmfDecodeModel.getUserInfo();
            if (StringUtils.isBlank(userInfo.getCard())
                    || StringUtils.isBlank(userInfo.getName())
                    || StringUtils.isBlank(userInfo.getPhone())) {
                return ResultModel.wrapError();
            }
            Business business = businessService.getBusinessByOrgId(zmfDecodeModel.getOrgid());
            logger.info("getHygz，得到B端的机构的调用数据，business={}",business);
            if (business == null || business.getVaild().equals(1) || business.getRemainAmount() <= 0) {
                return ResultModel.wrapError();
            }
            /*记录调用量*/
            business.setHygzCount(business.getHygzCount()+1);
            business.setPullCount(business.getPullCount()+1);
            Date date = business.getCreateTime();
            if (DateUtil.compare(date.getTime(), DateUtil.getCurrent0Time()) < 0) {
                businessService.insertBusiness(business);
            } else {
                businessService.updateBusiness(business);
            }
            /*计算余额*/


            logger.info("getHygz，得到B端的机构的调用数据，business={}",business);
            String sendParam = DataHandlerJAVAUtil.encode(zmfDecodeModel,"");
            logger.info("getHygz,得到我将要发送给供应商的加密数据，sendParam={}",sendParam);
            String result= HttpUtils.sendPost(sendParam,hygzUrl);
            logger.info("getHygz,得到供应商返回给我的认证的URL，result={}",result);
            return result;
        } catch (Exception e) {
            logger.warn("行业关注抛异常：",e);
            return ResultModel.wrapError();
        }
    }
}
