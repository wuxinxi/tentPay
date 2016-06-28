package com.tenpay.service;

import static java.lang.Thread.sleep;

import org.slf4j.LoggerFactory;

import com.tenpay.common.Configure;
import com.tenpay.common.Log;
import com.tenpay.handler.JSONClientResponseHandler;
import com.tenpay.handler.RequestHandler;
import com.tenpay.util.MD5Util;

public class CodeReverseService extends BaseService {

	private static Log log = new Log(LoggerFactory
			.getLogger(CodeReverseService.class));

	public CodeReverseService() {

	}

	public String sendPost(RequestHandler requestHandler) throws Exception {
		return super.sendPost(requestHandler);
	}

	public boolean doOneReverse(String outTradeNo) throws Exception {

		sleep(5000);// 等待一定时间再进行撤销，避免状态还没来得及被更新

		// 拼装请求
		RequestHandler reqHandler = new RequestHandler(null, null);
		reqHandler.init();
		reqHandler.setKey(Configure.getKey());
		reqHandler.setGateUrl(Configure.CODE_REVERSE_API);
		reqHandler.setParameter("partner", Configure.getBargainorId());
		reqHandler.setParameter("input_charset", "UTF-8");
		reqHandler.setParameter("service_version", "1.1");
		reqHandler.setParameter("out_trade_no", outTradeNo);
		reqHandler.setParameter("total_fee", "1");
		reqHandler.setParameter("op_user_id", Configure.getOpUserId());
		reqHandler.setParameter("op_user_passwd", MD5Util.MD5Encode(Configure
				.getOpUserPasswd(), ""));

		String result = sendPost(reqHandler);

		// 解析XML对象
		JSONClientResponseHandler jSONClientResponseHandler = new JSONClientResponseHandler();
		jSONClientResponseHandler.setContent(result);

		String retcode = jSONClientResponseHandler.getParameter("retcode");
		String retmsg = jSONClientResponseHandler.getParameter("retmsg");

		// 应答验签
		if ("0".equals(retcode)) {
			// 撤销成功
			log.i("撤销订单成功,订单号为:" + outTradeNo);
			return true;
		} else {
			// 撤销失败
			log.i("撤销失败，订单号为:" + outTradeNo + ",retcode:" + retcode + "retmsg:"
					+ retmsg);
			return false;
		}

	}

	public boolean doReverseLoop(String outTradeNo) throws Exception {

		// 最多撤销三次，
		int maxLoopCount = 3;
		// 撤销返回成功即退出，
		for (int i = 0; i < maxLoopCount; i++) {
			if (doOneReverse(outTradeNo)) {
				return true;
			}
		}
		log.w("多次撤销仍失败，转后续处理，订单号：" + outTradeNo);
		return false;
	}

}
