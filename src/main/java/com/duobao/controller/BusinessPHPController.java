package com.duobao.controller;


import com.alibaba.fastjson.JSON;
import com.duobao.Cache.LocalCacheUtils;
import com.duobao.entity.Business;
import com.duobao.entity.BusinessUserRelation;
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
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping(value = "/business/php/")
public class BusinessPHPController {
    Logger logger = LoggerFactory.getLogger(BusinessPHPController.class);
    @Autowired
    private BusinessService businessService;
    @Autowired
    private UserService userService;
    @Autowired
    private LocalCacheUtils localCacheUtils;

    @Autowired
    private RedisUtil redisUtil;
    String pushMessageUrl = "http://www.hlqz.top//index.php?g=Interfaces&m=Zmf&a=zmf_url";
    String obtainZmfUrl="http://www.hlqz.top//index.php?g=Interfaces&m=ZmfMf&a=getZmf";
    String hygzUrl = "http://www.hlqz.top//index.php?g=Interfaces&m=Hydt&a=zs_hydt";

    //
    @RequestMapping(value = "/getZmf", method = RequestMethod.POST)
    public String getZmf(String param) {
        //1、获取B端数据
        try {
            if (StringUtils.isBlank(param)) {
                return ResultModel.wrapError("参数为空");
            }
            param = param.replaceAll(" ", "+");
            logger.info("获取B端请求用户的加密数据：param={}", param);
            ZmfDecodeModel zmfDecodeModel = DataHandlerUtilForPHP.decode(param);
            String url = zmfDecodeModel.getUserInfo().getReturnUrl();
            String key = null;
            try {
                logger.info("localCacheUtils={}", localCacheUtils);
                try {
                    key = url.substring(url.indexOf("key="), url.indexOf("&"));
                } catch (Exception e) {
                    logger.info("获取key失败，key={}", key);
                }
                if (StringUtils.isBlank(key) || zmfDecodeModel.getOrgid().contains("random")) {
                    key = "key=" + UUID.randomUUID().toString();
                }
                if (StringUtils.isNotBlank(key)) {
                    localCacheUtils.set(key, zmfDecodeModel.getUserInfo().getReturnUrl());
                    redisUtil.set(key, zmfDecodeModel.getUserInfo().getReturnUrl());
                    redisUtil.setKeyExpire(key, 2);
                    logger.info("key={},value={},redisValue={}", key, localCacheUtils.get(key), redisUtil.get(key));
                }
            } catch (Exception e) {

            }
            logger.info("getZmf，得到B端请求用户的解密数据，zmfDecodeModel={}", zmfDecodeModel);
            if (zmfDecodeModel == null || StringUtils.isBlank(zmfDecodeModel.getOrgid())) {
                return ResultModel.wrapError();
            }
            UserInfo userInfo = zmfDecodeModel.getUserInfo();
            if (StringUtils.isBlank(userInfo.getCard())
                    || StringUtils.isBlank(userInfo.getName())
                    || StringUtils.isBlank(userInfo.getPhone())
                    || StringUtils.isBlank(userInfo.getReturnUrl())) {
                return ResultModel.wrapError();
            }
            //2、把数据保存到我们的数据库
            /*2.1、校验该用户有没有资格访问*/
            Business business = businessService.getBusinessByOrgId(zmfDecodeModel.getOrgid());
            logger.info("getZmf，得到B端的机构的调用数据，business={}", business);
            if (business == null || business.getVaild().equals(1) || business.getRemainAmount() <= 0) {
                return ResultModel.wrapError();
            }
            Date date = business.getCreateTime();
            /*2.2、保存B端数据到数据库*/
            /*如果最近的一条记录是今天的，就更新*/
            BusinessUserRelation relation = businessService.getBusinessUserRelationByOrgIdCard(userInfo.getCard(), business.getOrgid());
            if (relation != null && DateUtil.compare(relation.getCreateTime().getTime(), DateUtil.getCurrent0Time()) > 0) {
                relation.setNum(0);
                businessService.updateBusinessUserRelation(relation);
            } else {
                BusinessUserRelation businessUserRelation = new BusinessUserRelation();
                businessUserRelation.setCard(userInfo.getCard());
                businessUserRelation.setOrgid(business.getOrgid());
                businessService.saveBusinessUserRelation(businessUserRelation);
            }
            if (DateUtil.compare(date.getTime(), DateUtil.getCurrent0Time()) > 0) {
                business.setCallCount(business.getCallCount() + 1);
                businessService.updateBusinessCallCount(business.getId(), business.getOrgid(), business.getCallCount());
                businessService.updateBusinessReturnUrl(business.getId(), business.getOrgid(), userInfo.getReturnUrl());
            } else {
                business.setCallCount(business.getCallCount() + 1);
                business.setCallCount(business.getCallCount());
                business.setReturnUrl(userInfo.getReturnUrl());
                businessService.insertBusiness(business);
            }
            logger.info("getZmf，得到B端的机构将要保存到数据库的数据，business={}", business);
            /*3、把我的数据发送给供应商*/
            String sendParam = DataHandlerUtilForPHP.encode(zmfDecodeModel, key);
            logger.info("得到我将要发送给供应商的加密数据，sendParam={}", sendParam);
            String result = HttpUtils.sendPost(sendParam, pushMessageUrl);
            logger.info("得到供应商返回给我的认证的URL，result={}", result);
            return result;
        } catch (Exception e) {
            logger.warn("getZmf，认证之前的调用抛异常：", e);
            return ResultModel.wrapError();
        }
    }

    @RequestMapping(value = "/authentication", method = RequestMethod.POST)
    public String authentication(String param) {
        //1、获取B端数据
        try {
            if (StringUtils.isBlank(param)) {
                return ResultModel.wrapError("参数为空");
            }
            param = param.replaceAll(" ", "+");
            logger.info("获取B端请求用户的加密数据：param={}", param);
            ZmfDecodeModel zmfDecodeModel = DataHandlerUtilForPHP.decode(param);
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
                logger.info("localCacheUtils={}", localCacheUtils);
                try {
                    key = url.substring(url.indexOf("key="), url.indexOf("&"));
                } catch (Exception e) {
                    logger.info("获取key失败，key={}", key);
                }
                if (StringUtils.isBlank(key)) {
                    key = "key=" + UUID.randomUUID().toString();
                }
                if (StringUtils.isNotBlank(key)) {
                    localCacheUtils.set(key, zmfDecodeModel.getUserInfo().getReturnUrl());
                    redisUtil.set(key, zmfDecodeModel.getUserInfo().getReturnUrl());
                    redisUtil.setKeyExpire(key, 2);
                    logger.info("key={},value={},redisValue={}", key, localCacheUtils.get(key), redisUtil.get(key));
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
            /*2.2、保存B端数据到数据库*/
            if (DateUtil.compare(date.getTime(), DateUtil.getCurrent0Time()) > 0) {
                business.setCallCount(business.getCallCount() + 1);
                businessService.updateBusinessCallCount(business.getId(), business.getOrgid(), business.getCallCount());
                businessService.updateBusinessReturnUrl(business.getId(), business.getOrgid(), userInfo.getReturnUrl());
            } else {
                business.setCallCount(business.getCallCount() + 1);
                business.setCallCount(business.getCallCount());
                business.setReturnUrl(userInfo.getReturnUrl());
                businessService.insertBusiness(business);
            }
            logger.info("authentication，得到B端的机构将要保存到数据库的数据，business={}", business);
            /*3、把我的数据发送给供应商*/
            String sendParam = DataHandlerUtilForPHP.encodeAuthentication(zmfDecodeModel,key);
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
                returnUrlCache = localCacheUtils.get("key="+key);
                logger.info("缓存获取的值为：{}",returnUrlCache);
                if (StringUtils.isBlank(returnUrlCache)) {
                    returnUrlCache = redisUtil.get("key=" + key);
                }
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
    public String obtainZmf(String param) {
        try {
            if (StringUtils.isBlank(param)) {
                return ResultModel.wrapError("参数为空");
            }
            param = param.replaceAll(" ", "+");
            logger.info("获取B端请求用户的加密数据：param={}", param);
            ZmfDecodeModel zmfDecodeModel = DataHandlerUtilForPHP.decode(param);
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

            String sendParam = DataHandlerUtilForPHP.encodeAuthentication(zmfDecodeModel);
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
            /*2.1 保存B端数据*/
            logger.info("obtainZmf，得到B端的机构的调用数据，business={}",business);
            business.setSuccessCount(business.getSuccessCount() + 1);
            businessService.updateBusinessSucessCount(business.getId(),orgid, business.getSuccessCount());

            business.setPullCount(business.getPullCount()+1);
            businessService.updateBusinessPushCount(business.getId(),orgid,business.getPullCount());

            business.setZmfCount(business.getZmfCount()+1);
            businessService.updateBusinessZmfCount(business.getId(),orgid,business.getZmfCount());

            Business business_old=businessService.getLastSecondRemainAmount(business.getOrgid());
            Double RemainAmountLast=0.0;
            if (business_old == null) {
                RemainAmountLast = business.getTotalAmount() - business.getZmfPrice() * business.getZmfCount();
            } else {
                RemainAmountLast = business_old.getRemainAmount()-business.getZmfPrice()*(business.getZmfCount()-business_old.getZmfCount());
            }
            business.setRemainAmount(RemainAmountLast);
            businessService.updateBusinessRemainAmount(business.getId(),business.getOrgid(),business.getRemainAmount());
            logger.info("obtainZmf，得到B端的机构的调用数据，business={}",business);
            /*2.2 保存用户数据*/
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
            return jsonString;
        } catch (Exception e) {
            logger.warn("obtainZmf,获取芝麻分抛异常，e={}", e);
            return ResultModel.wrapError();
        }
    }
    @GetMapping(value = "/zhiMaFenCallBackHL")
    public String zhiMaFenCallBackHL(String param, String key, HttpServletResponse response) {

        try {
            if (StringUtils.isBlank(param)) {
                return ResultModel.wrapError();
            }
            logger.info("key{}",key);
            param = param.replaceAll(" ","+");
            logger.info("供应商开始回调我的returnUrl 参数为：param:{}",param);
            //1、得到芝麻分数据
            ZmfResultModel zmfResultModel = DataHandlerUtilForPHP.decodeZmf(param);
            logger.info("zhiMaFenCallBackHL，得到供应商调用我的returnUrl给我的解密之后的数据（对象）：zmfResultModel={}",zmfResultModel);
            if (zmfResultModel == null) {
                return ResultModel.wrapError();
            }
            //2、保存数据到我们自己的表
            /*2.1 保存B端数据*/
            BusinessUserRelation relation = businessService.getOrgIdByCard(zmfResultModel.getData().getCard());
            String orgid=relation.getOrgid();
            Business business = businessService.getBusinessByOrgId(orgid);
            if (relation.getNum() == 1) {
                zmfResultModel.setOrgid(orgid);
                String result = DataHandlerUtilForPHP.decodeResult(zmfResultModel);
                logger.info("供应商给我的芝麻分数据重新加密给B端，result={}",result);
                String returnUrl1=business.getReturnUrl();
                returnUrl1=returnUrl1+"param="+result;
                if (StringUtils.isNotBlank(key)) {
                    String returnUrlCache = localCacheUtils.get("key="+key);
                    logger.info("缓存获取的值为：key,{},{}",key,returnUrlCache);
                    if (StringUtils.isNotBlank(returnUrlCache)) {
                        returnUrl1 = returnUrlCache + "param=" + result;
                        logger.info("缓存获取的值为：{}", returnUrl1);
                    } else {
                        returnUrlCache = redisUtil.get("key=" + key);
                        returnUrl1 = returnUrlCache + "param=" + result;
                        logger.info("redis缓存获取的值为：{}", returnUrl1);
                    }
                }
                logger.info("重定向到B端用户的URL:{}",returnUrl1);
                response.sendRedirect(returnUrl1);
                return null;
            }
            if (relation.getNum() == 0) {
                relation.setNum(1);
                businessService.updateBusinessUserRelation(relation);
            }
            logger.info("zhiMaFenCallBackHL，得到B端的机构的调用数据，business={}",business);
            business.setSuccessCount(business.getSuccessCount() + 1);
            businessService.updateBusinessSucessCount(business.getId(),orgid, business.getSuccessCount());

            business.setPullCount(business.getPullCount()+1);
            businessService.updateBusinessPushCount(business.getId(),orgid,business.getPullCount());

            business.setZmfCount(business.getZmfCount()+1);
            businessService.updateBusinessZmfCount(business.getId(),orgid,business.getZmfCount());

            Business business_old=businessService.getLastSecondRemainAmount(business.getOrgid());
            Double RemainAmountLast=0.0;
            if (business_old == null) {
                RemainAmountLast = business.getTotalAmount() - business.getZmfPrice() * business.getZmfCount();
            } else {
                RemainAmountLast = business_old.getRemainAmount()-business.getZmfPrice()*(business.getZmfCount()-business_old.getZmfCount());
            }
            business.setRemainAmount(RemainAmountLast);
            businessService.updateBusinessRemainAmount(business.getId(),business.getOrgid(),business.getRemainAmount());
            logger.info("zhiMaFenCallBackHL，得到B端的机构的调用数据，business={}",business);
            /*2.2 保存用户数据*/
            User user = new User();
            user.setCard(zmfResultModel.getData().getCard());
            user.setName(zmfResultModel.getData().getName());
            user.setPhone(zmfResultModel.getData().getPhone());
            user.setZmf(zmfResultModel.getData().getZmf());
            userService.insertUser(user);
            logger.info("得到将要保存的用户数据，user={}",user);
            zmfResultModel.setOrgid(orgid);
            String result = DataHandlerUtilForPHP.decodeResult(zmfResultModel);
            logger.info("供应商给我的芝麻分数据重新加密给B端，result={}",result);
            String returnUrl=business.getReturnUrl();
            returnUrl=returnUrl+"param="+result;
            if (StringUtils.isNotBlank(key)) {
                String returnUrlCache = localCacheUtils.get("key="+key);
                logger.info("缓存获取的值为：{}",returnUrlCache);
                if (StringUtils.isNotBlank(returnUrlCache)) {
                    returnUrl = returnUrlCache+"param="+result;
                    logger.info("缓存获取的值为：{}",returnUrl);
                }else {
                    returnUrlCache = redisUtil.get("key=" + key);
                    returnUrl = returnUrlCache + "param=" + result;
                    logger.info("redis缓存获取的值为：key={},{}", key,returnUrl);
                }
            }
            logger.info("重定向到B端用户的URL:{}",returnUrl);
            response.sendRedirect(returnUrl);
            return null;
        } catch (Exception e) {
            logger.warn("returnUrl方法抛异常：",e);
            return ResultModel.wrapError();
        }
    }

    @RequestMapping(value = "/getHygz", method = RequestMethod.POST)
    public String getHygz(String param) {
        try {
            if (StringUtils.isBlank(param)) {
                return ResultModel.wrapError();
            }
            param = param.replaceAll(" ", "+");
            logger.info("getHygz,获取B端请求用户的加密数据：param={}", param);
            ZmfDecodeModel zmfDecodeModel = DataHandlerUtilForPHP.decode(param);
            logger.info("getHygz，得到B端请求用户的解密数据，zmfDecodeModel={}", zmfDecodeModel);
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
            logger.info("getHygz，得到B端的机构的调用数据，business={}", business);
            if (business == null || business.getVaild().equals(1) || business.getRemainAmount() <= 0) {
                return ResultModel.wrapError();
            }
            business.setHygzCount(business.getHygzCount() + 1);
            business.setPullCount(business.getPullCount() + 1);
            businessService.updateBusinessPushCount(business.getId(), business.getOrgid(), business.getPullCount());
            businessService.updateBusinessHygzCount(business.getId(), business.getOrgid(), business.getHygzCount());
            Business business_old = businessService.getLastSecondRemainAmount(business.getOrgid());
            Double RemainAmountLast = 0.0;
            if (business_old == null) {
                RemainAmountLast = business.getRemainAmount() - business.getHygzPrice() * business.getHygzCount();
            } else {
                RemainAmountLast = business_old.getRemainAmount() - business.getHygzPrice() * (business.getHygzCount() - business_old.getHygzCount());
            }
            business.setRemainAmount(RemainAmountLast);
            businessService.updateBusinessRemainAmount(business.getId(), business.getOrgid(), business.getRemainAmount());
            logger.info("getHygz，得到B端的机构的调用数据，business={}", business);
            String sendParam = DataHandlerUtilForPHP.encodeHygz(zmfDecodeModel);
            logger.info("getHygz,得到我将要发送给供应商的加密数据，sendParam={}", sendParam);
            String result = HttpUtils.sendPost(sendParam, hygzUrl);
            logger.info("getHygz,得到供应商返回给我的认证的URL，result={}", result);
            return result;
        } catch (Exception e) {
            logger.warn("行业关注抛异常：", e);
            return ResultModel.wrapError();
        }
    }

    @RequestMapping(value = "/getNewHygz", method = RequestMethod.POST)
    public String getNewHygz(String param) {
        try {
            if (StringUtils.isBlank(param)) {
                return ResultModel.wrapError();
            }
            param = param.replaceAll(" ", "+");
            logger.info("getHygz,获取B端请求用户的加密数据：param={}", param);
            ZmfDecodeModel zmfDecodeModel = DataHandlerUtilForPHP.decode(param);
            logger.info("getHygz，得到B端请求用户的解密数据，zmfDecodeModel={}", zmfDecodeModel);
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
            logger.info("getHygz，得到B端的机构的调用数据，business={}", business);
            if (business == null || business.getVaild().equals(1) || business.getRemainAmount() <= 0) {
                return ResultModel.wrapError();
            }
            business.setHygzCount(business.getHygzCount() + 1);
            business.setPullCount(business.getPullCount() + 1);
            businessService.updateBusinessPushCount(business.getId(), business.getOrgid(), business.getPullCount());
            businessService.updateBusinessHygzCount(business.getId(), business.getOrgid(), business.getHygzCount());
            Business business_old = businessService.getLastSecondRemainAmount(business.getOrgid());
            Double RemainAmountLast = 0.0;
            if (business_old == null) {
                RemainAmountLast = business.getRemainAmount() - business.getHygzPrice() * business.getHygzCount();
            } else {
                RemainAmountLast = business_old.getRemainAmount() - business.getHygzPrice() * (business.getHygzCount() - business_old.getHygzCount());
            }
            business.setRemainAmount(RemainAmountLast);
            businessService.updateBusinessRemainAmount(business.getId(), business.getOrgid(), business.getRemainAmount());
            logger.info("getHygz，得到B端的机构的调用数据，business={}", business);
            String sendParam = DataHandlerUtilForPHP.encodeHygzNew(zmfDecodeModel);
            logger.info("getHygz,得到我将要发送给供应商的加密数据，sendParam={}", sendParam);
            String result = HttpUtils.sendPost(sendParam, hygzUrl);
            logger.info("getHygz,得到供应商返回给我的认证的URL，result={}", result);
            return result;
        } catch (Exception e) {
            logger.warn("行业关注抛异常：", e);
            return ResultModel.wrapError();
        }
    }

    @GetMapping(value = "/userzhiMaFenCallBackHL")
    public String userzhiMaFenCallBackHL(String param) {
        logger.info("进入用户的回调函数，param={}", param);
        String decodeString = Base64Utils.decode(param);
        String data = Ts.decry_RC4(decodeString, "UCJYLVVL");
        logger.info("用户解密之后的数据，param={}", data);
        return param;
    }
}
