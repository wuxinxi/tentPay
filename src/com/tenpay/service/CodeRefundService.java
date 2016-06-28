package com.tenpay.service;

import org.slf4j.LoggerFactory;

import com.tenpay.common.Configure;
import com.tenpay.common.Log;
import com.tenpay.handler.RequestHandler;
import com.tenpay.handler.XMLClientResponseHandler;
import com.tenpay.util.MD5Util;

public class CodeRefundService extends BaseService {

	private static Log log = new Log(LoggerFactory
			.getLogger(CodeRefundService.class));

	public CodeRefundService() {
	}

	public String sendPost(RequestHandler requestHandler) throws Exception {
		return super.sendPost(requestHandler);
	}

	public void run(String outTradeNo, String outRefundNo, String totalFee,
			String refundFee) throws Exception {

		// 拼装请求
		RequestHandler reqHandler = new RequestHandler(null, null);
		reqHandler.init();
		reqHandler.setKey(Configure.getKey());
		reqHandler.setGateUrl(Configure.CODE_REFUND_API);
		reqHandler.setParameter("partner", Configure.getBargainorId());
		reqHandler.setParameter("input_charset", "UTF-8");
		reqHandler.setParameter("service_version", "1.1");
		reqHandler.setParameter("out_refund_no", outRefundNo);
		reqHandler.setParameter("out_trade_no", outTradeNo);
		reqHandler.setParameter("total_fee", totalFee);
		reqHandler.setParameter("refund_fee", refundFee);
		reqHandler.setParameter("op_user_id", Configure.getOpUserId());
		reqHandler.setParameter("op_user_passwd", MD5Util.MD5Encode(Configure
				.getOpUserPasswd(), ""));

		String result = sendPost(reqHandler);

		// 解析XML对象
		XMLClientResponseHandler xMLClientResponseHandler = new XMLClientResponseHandler();
		xMLClientResponseHandler.setContent(result);
		xMLClientResponseHandler.setKey(Configure.getKey());

		String retcode = xMLClientResponseHandler.getParameter("retcode");
		String retmsg = xMLClientResponseHandler.getParameter("retmsg");

		// 应答验签
		// if (xMLClientResponseHandler.isTenpaySign() && ("0").equals(retcode))
		// {
		if (("0").equals(retcode)) {
			// 退款申请调用成功,返回退款状态
			/*
			 * 退款状态 refund_status 4，10：退款成功。 3，5，6：退款失败。 8，9，11:退款处理中。
			 * 1，2未确定，需要商户原退款单号重新发起。
			 * 7：转入代发，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，资金回流到商户的现金帐号
			 * ，需要商户人工干预，通过线下或者财付通转账的方式进行退款。
			 */
			log.i("退款申请成功,订单号：" + outTradeNo + "商户退款单号" + outRefundNo
					+ "的退款状态是："
					+ xMLClientResponseHandler.getParameter("refund_status"));

		} else {
			// 返回失败
			log.i("验签失败或业务错误,retcode:" + retcode + "retmsg:" + retmsg);
			// 同个退款单号多次请求，财付通当一个单处理，只会退一次款。
			// 如果出现退款返回失败，请稍后采用原退款单号重新发起，避免出现重复退款
		}

	}

	public static void main(String[] args) throws Exception {
		CodeRefundService codeRefundService = new CodeRefundService();
		codeRefundService.run("201511151432354553", "109201511151432354553",
				"1", "1");
	}

}
