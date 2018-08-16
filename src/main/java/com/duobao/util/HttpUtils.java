package com.duobao.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class HttpUtils {
    private static final Log logger = LogFactory.getLog(HttpUtils.class);

    public static String sendPost(String data,String pushMessageUrl) throws Exception {
        String returnStr = "";
        CloseableHttpClient client = HttpClients.createDefault();

        try {
            HttpPost httppost = new HttpPost(pushMessageUrl);
            List<BasicNameValuePair> list = new ArrayList();
            list.add(new BasicNameValuePair("param", data));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httppost.setEntity(entity);
            CloseableHttpResponse response = client.execute(httppost);
            if (response.getStatusLine().getStatusCode() == 200) {
                returnStr = EntityUtils.toString(response.getEntity());
            }
        } catch (Exception var12) {
            var12.printStackTrace();
        } finally {
            client.close();
        }
        return returnStr;
    }

    public static String sendGet(String returnUrl,String param) throws Exception {
        String returnStr = "";
        CloseableHttpClient client = HttpClients.createDefault();

        try {
            param = URLEncoder.encode(param, "UTF-8");
            HttpGet httpGet = new HttpGet(returnUrl+"?param="+param);
            CloseableHttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                returnStr = EntityUtils.toString(response.getEntity());
            }
            logger.info("returnStr:"+returnStr);
        } catch (Exception var12) {
            var12.printStackTrace();
        } finally {
            client.close();
        }
        return returnStr;
    }
}
