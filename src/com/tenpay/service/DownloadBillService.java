package com.tenpay.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.slf4j.LoggerFactory;

import com.tenpay.common.Configure;
import com.tenpay.common.Log;
import com.tenpay.common.TenpayHttpClient;
import com.tenpay.handler.DownloadBillRequestHandler;

public class DownloadBillService extends BaseService {

	private static Log log = new Log(LoggerFactory.getLogger(BaseService.class));

	public DownloadBillService() {
	}

	// 接口特殊，重写方法
	protected String sendPost(DownloadBillRequestHandler requestHandler)
			throws Exception {

		TenpayHttpClient httpClient = new TenpayHttpClient();

		httpClient.setTimeOut(10);// 设置请求超时时间(秒)
		httpClient.setMethod("POST");
		httpClient.setCharset("GBK");

		// 设置请求内容
		String requestUrl = requestHandler.getRequestURL();
		log.i("requestUrl=" + requestUrl);
		httpClient.setReqContent(requestUrl);
		String resContent = null;
		// 远程调用
		if (httpClient.call()) {
			resContent = httpClient.getResContent();
		} else {
			log.e("后台调用通信失败，responseCode=" + httpClient.getResponseCode()
					+ ",errInfo=" + httpClient.getErrInfo());
		}
		log.i("resContent=" + resContent);
		return resContent;
	}

	public void run(String billdate) throws Exception {

		DownloadBillRequestHandler reqHandler = new DownloadBillRequestHandler(
				null, null);
		// 拼装请求
		reqHandler.init();
		reqHandler.setKey(Configure.getKey());
		reqHandler.setGateUrl(Configure.DOWNLOAD_BILL_API);
		reqHandler.setParameter("spid", Configure.getBargainorId());
		reqHandler.setParameter("trans_time", billdate);// 格式YYYY-MM-DD,如2015-08-26
		reqHandler.setParameter("stamp", Long.toString(System
				.currentTimeMillis() / 1000));
		reqHandler.setParameter("cft_signtype", "0");
		reqHandler.setParameter("mchtype", "0");

		String billContent = sendPost(reqHandler);
         
		//输出文件
		OutputStream os = new FileOutputStream(new File("F:/data/bill"
				+ billdate + ".txt"), true);
		os.write(billContent.getBytes());
	}

	public static void main(String[] args) throws Exception {
		DownloadBillService DownloadBillService = new DownloadBillService();
		DownloadBillService.run("2015-11-20");
	}

}
