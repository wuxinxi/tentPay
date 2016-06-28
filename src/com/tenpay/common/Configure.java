package com.tenpay.common;

public class Configure {

	// 商户ID
	private static String bargainorId = "1900000109";
	
	//商户秘钥
	private static String key = "8934e7d15453e97507ef794cf7b0519d";

	// HTTPS证书的本地路径
	private static String certLocalPath = "c:/key/1900000109.pfx";

	// HTTPS证书密码，证书密码初始化后通过系统短信发送到商户注册人手机上,请注意查收
	private static String certPassword = "532398";
	
	//商户后台操作员账号
	private static String opUserId = "1900000109";
	
	//商户后台操作员账号密码
	private static String opUserPasswd = "111111";

	// 以下是几个API的路径：
	// 1）提交付款码支付API
	public static String CODE_PAY_API = "https://myun.tenpay.com/cgi-bin/scan/code_pay.cgi";

	// 2）查询订单API
	public static String CODE_QUERY_API = "https://myun.tenpay.com/cgi-bin/clientv1.0/qpay_order_query.cgi";

	// 3）退款API
	public static String CODE_REFUND_API = "https://api.qpay.qq.com/cgi-bin/scan/code_refund.cgi";

	// 4）退款查询API
	public static String CODE_REFUND_QUERY_API = "https://myun.tenpay.com/cgi-bin/scan/code_refund_query.cgi";

	// 5）撤销API
	public static String CODE_REVERSE_API = "https://api.qpay.qq.com/cgi-bin/scan/code_reverse.cgi";

	// 6）下载对账单API
	public static String DOWNLOAD_BILL_API = "http://api.mch.tenpay.com/cgi-bin/mchdown_real_new.cgi";

	public static String getBargainorId() {
		return bargainorId;
	}

	public static void setBargainorId(String bargainorId) {
		Configure.bargainorId = bargainorId;
	}

	public static String getKey() {
		return key;
	}

	public static void setKey(String key) {
		Configure.key = key;
	}

	public static String getCertLocalPath() {
		return certLocalPath;
	}

	public static void setCertLocalPath(String certLocalPath) {
		Configure.certLocalPath = certLocalPath;
	}

	public static String getCertPassword() {
		return certPassword;
	}

	public static void setCertPassword(String certPassword) {
		Configure.certPassword = certPassword;
	}

	public static String getOpUserId() {
		return opUserId;
	}

	public static void setOpUserId(String opUserId) {
		Configure.opUserId = opUserId;
	}

	public static String getOpUserPasswd() {
		return opUserPasswd;
	}

	public static void setOpUserPasswd(String opUserPasswd) {
		Configure.opUserPasswd = opUserPasswd;
	}
	
}

	