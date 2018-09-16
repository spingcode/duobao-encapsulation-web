package com.duobao.controller;


import com.duobao.entity.Business;
import com.duobao.entity.BusinessUserRelation;
import com.duobao.entity.User;
import com.duobao.model.*;
import com.duobao.service.BusinessService;
import com.duobao.service.UserService;
import com.duobao.util.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/business")
public class BusinessController {
    Logger logger = LoggerFactory.getLogger(BusinessController.class);
    @Autowired
    private BusinessService businessService;
    @Autowired
    private UserService userService;
    String pushMessageUrl = "http://www.hlqz.top//index.php?g=Interfaces&m=Zmf&a=zmf_url";
    String hygzUrl="http://www.hlqz.top//index.php?g=Interfaces&m=Hydt&a=zs_hydt";
    //
    @RequestMapping(value = "/getZmf", method = RequestMethod.POST)
    public String getZmf(String param) {
        //1、获取B端数据
        try {
            if (StringUtils.isBlank(param)) {
                return ResultModel.wrapError("参数为空");
            }
            param = param.replaceAll(" ","+");
            logger.info("获取B端请求用户的加密数据：param={}",param);
            ZmfDecodeModel zmfDecodeModel = DataHandlerUtil.decode(param);
            logger.info("getZmf，得到B端请求用户的解密数据，zmfDecodeModel={}",zmfDecodeModel);
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
            logger.info("getZmf，得到B端的机构的调用数据，business={}",business);
            if (business == null || business.getVaild().equals(1) || business.getRemainAmount() <= 0) {
                return ResultModel.wrapError();
            }
            Date date = business.getCreateTime();
            /*2.2、保存B端数据到数据库*/
            /*如果最近的一条记录是今天的，就更新*/
            BusinessUserRelation relation = businessService.getBusinessUserRelationByOrgIdCard(userInfo.getCard(),business.getOrgid());
            if (relation != null&&DateUtil.compare(relation.getCreateTime().getTime(), DateUtil.getCurrent0Time()) > 0) {
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
                businessService.updateBusinessCallCount(business.getId(),business.getOrgid(), business.getCallCount());
                businessService.updateBusinessReturnUrl(business.getId(),business.getOrgid(), userInfo.getReturnUrl());
            } else {
                business.setCallCount(business.getCallCount() + 1);
                business.setCallCount(business.getCallCount());
                business.setReturnUrl(userInfo.getReturnUrl());
                businessService.insertBusiness(business);
            }
            logger.info("getZmf，得到B端的机构将要保存到数据库的数据，business={}",business);
            /*3、把我的数据发送给供应商*/
            String sendParam = DataHandlerUtil.encode(zmfDecodeModel);
            logger.info("得到我将要发送给供应商的加密数据，sendParam={}",sendParam);
            String result= HttpUtils.sendPost(sendParam,pushMessageUrl);
            logger.info("得到供应商返回给我的认证的URL，result={}",result);
            return result;
        } catch (Exception e) {
            logger.warn("getZmf，认证之前的调用抛异常：",e);
            return ResultModel.wrapError();
        }
    }

    @GetMapping(value = "/zhiMaFenCallBackHL")
    public String zhiMaFenCallBackHL(String param,HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(param)) {
                return ResultModel.wrapError();
            }
            param = param.replaceAll(" ","+");
            logger.info("供应商开始回调我的returnUrl 参数为：param:{}",param);
            //1、得到芝麻分数据
            ZmfResultModel zmfResultModel = DataHandlerUtil.decodeZmf(param);
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
                String result = DataHandlerUtil.decodeResult(zmfResultModel);
                logger.info("供应商给我的芝麻分数据重新加密给B端，result={}",result);
                String returnUrl1=business.getReturnUrl();
                returnUrl1=returnUrl1+"?param="+result;
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
            String result = DataHandlerUtil.decodeResult(zmfResultModel);
            logger.info("供应商给我的芝麻分数据重新加密给B端，result={}",result);
            //String response = HttpUtils.sendGet(business.getReturnUrl(), result);
            String returnUrl=business.getReturnUrl();
            logger.info("重定向到B端用户的URL:{}",returnUrl);
            returnUrl=returnUrl+"?param="+result;
            logger.info("重定向到B端用户的URL:{}",returnUrl);
            response.sendRedirect(returnUrl);
            return null;
        } catch (Exception e) {
            logger.warn("returnUrl方法抛异常：",e);
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
            ZmfDecodeModel zmfDecodeModel = DataHandlerUtil.decode(param);
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
            business.setHygzCount(business.getHygzCount()+1);
            business.setPullCount(business.getPullCount()+1);
            businessService.updateBusinessPushCount(business.getId(),business.getOrgid(),business.getPullCount());
            businessService.updateBusinessHygzCount(business.getId(),business.getOrgid(),business.getHygzCount());
            Business business_old=businessService.getLastSecondRemainAmount(business.getOrgid());
            Double RemainAmountLast=0.0;
            if (business_old == null) {
                RemainAmountLast = business.getRemainAmount() - business.getHygzPrice() * business.getHygzCount();
            } else {
                RemainAmountLast = business_old.getRemainAmount()-business.getHygzPrice()*(business.getHygzCount()-business_old.getHygzCount());
            }
            business.setRemainAmount(RemainAmountLast);
            businessService.updateBusinessRemainAmount(business.getId(),business.getOrgid(),business.getRemainAmount());
            logger.info("getHygz，得到B端的机构的调用数据，business={}",business);
            String sendParam = DataHandlerUtil.encodeHygz(zmfDecodeModel);
            logger.info("getHygz,得到我将要发送给供应商的加密数据，sendParam={}",sendParam);
            String result= HttpUtils.sendPost(sendParam,hygzUrl);
            logger.info("getHygz,得到供应商返回给我的认证的URL，result={}",result);
            return result;
        } catch (Exception e) {
            logger.warn("行业关注抛异常：",e);
            return ResultModel.wrapError();
        }
    }

    @GetMapping(value = "/userzhiMaFenCallBackHL")
    public String userzhiMaFenCallBackHL(String param) {
        logger.info("进入用户的回调函数，param={}",param);
        String decodeString =Base64Utils.decode(param);
        String data = Ts.decry_RC4(decodeString, "UCJYLVVL");
        logger.info("用户解密之后的数据，param={}",data);
        return param;
    }
}
