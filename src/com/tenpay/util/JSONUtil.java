package com.tenpay.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {

	/**
	 * 将 Json 形式的字符串转换为 Map(返回第一级元素)
	 */
	public static Map doJSONParse(String jsonStr) throws JSONException,
			IOException {

		if (null == jsonStr || "".equals(jsonStr)) {
			return null;
		}

		Map m = new HashMap();

		JSONObject jsonObj = new JSONObject(jsonStr);

		Iterator it = jsonObj.keys();
		while (it.hasNext()) {
			String k = it.next().toString();
			String v = jsonObj.get(k).toString();
			m.put(k, v);
		}
		return m;
	}

}