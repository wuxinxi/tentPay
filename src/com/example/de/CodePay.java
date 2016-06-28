package com.example.de;

import org.slf4j.LoggerFactory;
import com.tenpay.common.Configure;
import com.tenpay.common.Log;
import com.tenpay.handler.JSONClientResponseHandler;
import com.tenpay.handler.RequestHandler;
import com.tenpay.service.CodePayService;
import com.tenpay.service.CodeQueryService;
import com.tenpay.service.CodeReverseService;
import com.tenpay.util.TenpayUtil;

public class CodePay {

	private static Log log = new Log(LoggerFactory.getLogger(CodePay.class));

	public void run(String authCode, String outTradeNo, String totalFee)
			throws Exception {
		RequestHandler reqHandler = new RequestHandler(null, null);
		reqHandler.init();
		reqHandler.setKey(Configure.getKey());
		reqHandler.setGateUrl(Configure.CODE_PAY_API);

		// 拼装扣款请求
		reqHandler.setParameter("bargainor_id", Configure.getBargainorId()); // 商户号
		reqHandler.setParameter("charset", "1");
		reqHandler.setParameter("ver", "2.0");
		reqHandler.setParameter("desc", "测试test");
		reqHandler.setParameter("sp_billno", outTradeNo);
		reqHandler.setParameter("total_fee", totalFee);
		reqHandler.setParameter("fee_type", "1");
		reqHandler.setParameter("pay_channel", "1");
		reqHandler.setParameter("auth_code", authCode);
		reqHandler.setParameter("sp_device_id", "WP00000001");

		CodePayService codePayService = new CodePayService();
		CodeQueryService codeQueryService = new CodeQueryService();
		CodeReverseService codeReverseService = new CodeReverseService();

		String reslut = codePayService.sendPost(reqHandler);

		JSONClientResponseHandler jSONClientResponseHandler = new JSONClientResponseHandler();
		jSONClientResponseHandler.setContent(reslut);
		String retcode = jSONClientResponseHandler.getParameter("retcode");
		String retmsg = jSONClientResponseHandler.getParameter("retmsg");

		if (("0").equals(retcode)) {
			// (1)直接扣款成功
			// ------处理业务逻辑更新支付状态
			log.i("一次性支付成功,财付通支付流水号="
					+ jSONClientResponseHandler.getParameter("transaction_id"));

		} else {
			log.i("业务返回失败,retcode:" + retcode + "retmsg:" + retmsg);

			if (("130231024").equals(retcode) || ("66227006").equals(retcode)
					|| ("66227008").equals(retcode)) {
				// (2)扣款明确失败(包括但不限于以上3种错误码)
				// ------根据实际情况明确提示用户，指导接下来的操作
				if (("130231024").equals(retcode)) {
					log.w("【支付扣款明确失败】原因是：" + retmsg);// code不存在,提示用户刷新一维码/二维码,重新扫码
				} else if (("66227006").equals(retcode)) {
					log.w("【支付扣款明确失败】原因是：" + retmsg);// 用户授权码无效,提示用户刷新一维码/二维码,重新扫码
				} else if (("66227008").equals(retcode)) {
					log.w("【支付扣款明确失败】原因是：" + retmsg);// 余额不足
				}
				// ------调用撤销接口
				codeReverseService.doReverseLoop(outTradeNo);

			} else if (("66227005").equals(retcode)) {
				// (3)需要输入支付密码
				// ------等当返回结果提示“用户正在支付”时，商户系统可设置间隔时间(建议10秒)重新查询支付结果，直到支付成功或超时(建议30秒)
				// ------仍失败则调用撤销接口
				log.w("【提示用户输入支付密码】");
				if (codeQueryService.doPayQueryLoop(3, 10000, outTradeNo)) {
					log.i("需要用户输入密码,查询到支付成功");
				} else {
					log.i("需要用户输入密码,在一定时间内没有查询到支付成功,走撤销流程");
					codeReverseService.doReverseLoop(outTradeNo);
				}
			} else {
				// (4)其他未知失败
				// ------等待5秒后调用查询订单API
				// ------仍失败则调用撤销接口
				if (codeQueryService.doPayQueryLoop(1, 5000, outTradeNo)) {
					log.i("未知失败,查询到支付成功");
				} else {
					log.i("未知失败,未查询到支付成功,走撤销流程");
					codeReverseService.doReverseLoop(outTradeNo);
				}

			}

		}
	}

	public static void main(String[] args) throws Exception {

		CodePay codePay = new CodePay();

		// 模拟一个商户订单号
		String currTime = TenpayUtil.getCurrTime();
		String strDate = currTime.substring(0, 8);
		String strTime = currTime.substring(8, currTime.length());
		String strRandom = TenpayUtil.buildRandom(4) + "";
		String outTradeNo = strDate + strTime + strRandom;

		codePay.run("910266461329126170", outTradeNo, "1");
	}

}
