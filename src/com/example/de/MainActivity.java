package com.example.de;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tenpay.util.TenpayUtil;
import com.wizarpos.barcode.scanner.IScanEvent;
import com.wizarpos.barcode.scanner.ScannerRelativeLayout;
import com.wizarpos.barcode.scanner.ScannerResult;

public class MainActivity extends Activity {
	private ScannerRelativeLayout scanner;
	private Button button;
	public IScanEvent scanLisenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		scanner = (ScannerRelativeLayout) findViewById(R.id.scanner);
		button = (Button) findViewById(R.id.button);
		

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				CodePay codePay = new CodePay();

				// 模拟一个商户订单号
				String currTime = TenpayUtil.getCurrTime();
				String strDate = currTime.substring(0, 8);
				String strTime = currTime.substring(8, currTime.length());
				String strRandom = TenpayUtil.buildRandom(4) + "";
				String outTradeNo = strDate + strTime + strRandom;

				try {
					codePay.run("910266461329126170", outTradeNo, "1");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				scanner.onResume();
//				scanner.startScan();
//				
//				scanLisenter = new ScanSuccesListener();
//				scanner.setScanSuccessListener(scanLisenter);
			}
		});
	}
	
	private class ScanSuccesListener extends IScanEvent {

		@Override
		public void scanCompleted(ScannerResult arg0) {
			// TODO Auto-generated method stub
			if (!arg0.equals("")) {
				Toast.makeText(MainActivity.this, arg0.getResult(), 0).show();
				scanner.stopScan();
			}
			
		}
		
	}

}
