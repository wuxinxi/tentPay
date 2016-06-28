package com.tenpay.handler;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import com.tenpay.util.JSONUtil;

/**
 * 解析JSON应答类
 * 
 */

public class JSONClientResponseHandler extends ClientResponseHandler {

	protected void doParse() throws JSONException, IOException {
		String jsonContent = this.getContent();

		// 解析JSON,得到map
		Map m = JSONUtil.doJSONParse(jsonContent);

		// 设置参数
		Iterator it = m.keySet().iterator();
		while (it.hasNext()) {
			String k = (String) it.next();
			String v = (String) m.get(k);
			this.setParameter(k, v);
		}

	}

}
