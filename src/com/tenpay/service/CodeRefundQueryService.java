package com.tenpay.service;

import org.slf4j.LoggerFactory;

import com.tenpay.common.Configure;
import com.tenpay.common.Log;
import com.tenpay.handler.RequestHandler;
import com.tenpay.handler.XMLClientResponseHandler;
import com.tenpay.util.TenpayUtil;

public class CodeRefundQueryService extends BaseService {

	private static Log log = new Log(LoggerFactory
			.getLogger(CodeRefundQueryService.class));

	public CodeRefundQueryService() {
	}

	public String sendPost(RequestHandler requestHandler) throws Exception {
		return super.sendPost(requestHandler);
	}

	public void run(String outRefundNo) throws Exception {

		// 拼装请求
		RequestHandler reqHandler = new RequestHandler(null, null);
		reqHandler.init();
		reqHandler.setKey(Configure.getKey());
		reqHandler.setGateUrl(Configure.CODE_REFUND_QUERY_API);
		reqHandler.setParameter("partner", Configure.getBargainorId());
		reqHandler.setParameter("input_charset", "UTF-8");
		reqHandler.setParameter("service_version", "1.1");
		reqHandler.setParameter("out_refund_no", outRefundNo);
		// reqHandler.setParameter("out_trade_no", outTradeNo);

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
			// 退款查询调用成功,返回退款状态
			/*
			 * 退款状态 refund_status 4，10：退款成功。 3，5，6：退款失败。 8，9，11:退款处理中。
			 * 1，2未确定，需要商户原退款单号重新发起。
			 * 7：转入代发，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，资金回流到商户的现金帐号
			 * ，需要商户人工干预，通过线下或者财付通转账的方式进行退款。
			 */
			log.i("退款查询成功");

			int refundCount = TenpayUtil.toInt(xMLClientResponseHandler
					.getParameter("refund_count"));
			
			for (int i = 0; i < refundCount; i++) {
				log.i("商户退款单号"
						+ xMLClientResponseHandler
								.getParameter("out_refund_no_" + i)
						+ "的退款状态是："
						+ xMLClientResponseHandler
								.getParameter("refund_status_" + i));

			}

		} else {
			// 查询失败
			log.i("验签失败或业务错误,retcode:" + retcode + "retmsg:" + retmsg);
		}

	}

	public static void main(String[] args) throws Exception {
		CodeRefundQueryService codeRefundQueryService = new CodeRefundQueryService();
		codeRefundQueryService.run("109201511151432354553");
	}

}
