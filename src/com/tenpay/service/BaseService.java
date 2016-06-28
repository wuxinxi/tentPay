package com.tenpay.service;

import java.io.File;

import org.slf4j.LoggerFactory;

import com.tenpay.common.Configure;
import com.tenpay.common.Log;
import com.tenpay.common.TenpayHttpClient;
import com.tenpay.handler.RequestHandler;

public class BaseService {

	private static Log log = new Log(LoggerFactory.getLogger(BaseService.class));

	public BaseService() {
	}

	protected String sendPost(RequestHandler requestHandler) throws Exception {

		TenpayHttpClient httpClient = new TenpayHttpClient();
         
		//设置商户证书
		httpClient.setCertInfo(new File(Configure.getCertLocalPath()),
				Configure.getCertPassword());
		httpClient.setTimeOut(5);// 设置请求超时时间(秒)
		httpClient.setMethod("POST");

		// 设置请求内容
		String requestUrl = requestHandler.getRequestURL();
		log.i("requestUrl=" + requestUrl);
		httpClient.setReqContent(requestUrl);
		String resContent = null;
		// 远程调用
		if (httpClient.call()) {
			resContent = httpClient.getResContent();
		} else {
			log.e("后台调用通信失败，responseCode=" + httpClient.getResponseCode() + ",errInfo="
					+ httpClient.getErrInfo());
		}
		log.i("resContent=" + resContent);
		return resContent;
	}

}
