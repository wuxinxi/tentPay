package com.tenpay.service;

import static java.lang.Thread.sleep;

import org.slf4j.LoggerFactory;

import com.tenpay.common.Configure;
import com.tenpay.common.Log;
import com.tenpay.handler.RequestHandler;
import com.tenpay.handler.XMLClientResponseHandler;

public class CodeQueryService extends BaseService {

	private static Log log = new Log(LoggerFactory
			.getLogger(CodeQueryService.class));

	public CodeQueryService() {
	}

	public String sendPost(RequestHandler requestHandler) throws Exception {
		return super.sendPost(requestHandler);
	}

	public boolean doOneCodeQuery(int waitingTime, String outTradeNo)
			throws Exception {
		sleep(waitingTime);// 等待一定时间再进行查询，避免状态还没来得及被更新

		// 拼装请求
		RequestHandler reqHandler = new RequestHandler(null, null);
		reqHandler.init();
		reqHandler.setKey(Configure.getKey());
		reqHandler.setGateUrl(Configure.CODE_QUERY_API);
		reqHandler.setParameter("partner", Configure.getBargainorId());
		reqHandler.setParameter("charset", "UTF-8");
		reqHandler.setParameter("ver", "2.0");
		reqHandler.setParameter("out_trade_no", outTradeNo);

		String result = sendPost(reqHandler);

		// 解析XML对象
		XMLClientResponseHandler xMLClientResponseHandler = new XMLClientResponseHandler();
		xMLClientResponseHandler.setContent(result);
		xMLClientResponseHandler.setKey(Configure.getKey());

		// 应答验签
		if (xMLClientResponseHandler.isTenpaySignForCodePayQuery()
				&& ("0").equals(xMLClientResponseHandler
						.getParameter("retcode"))) {
			// 查询成功
			log.i("查询到订单支付成功,财付通支付流水号="
					+ xMLClientResponseHandler.getParameter("transaction_id"));

			return true;
		} else {
			// 查询失败
			log.i("查询到订单支付不成功");
			return false;
		}

	}

	public boolean doPayQueryLoop(int loopCount, int waitingTime,
			String outTradeNo) throws Exception {
		// 至少查询一次
		if (loopCount == 0) {
			loopCount = 1;
		}
		// 进行循环查询
		for (int i = 0; i < loopCount; i++) {
			if (doOneCodeQuery(waitingTime, outTradeNo)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) throws Exception {

		CodeQueryService codeQueryService = new CodeQueryService();
		codeQueryService.doOneCodeQuery(1000, "201511051434546090");
	}

}
