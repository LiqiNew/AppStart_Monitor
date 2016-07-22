package com.liqi.appstart.acitivity;


import com.liqi.appstart.R;
import com.liqi.appstart.service.AppStart_Monitor_Service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
/**
 * 监听指定程序有没有挂掉，挂掉重新打开
 * @author Liqi
 *
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startService(getBindService(AppStart_Monitor_Service.class));
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(100*2);
					System.exit(0);
				} catch (InterruptedException e) {
					System.exit(0);
				}
			}
		}).start();
	}
	/**
	 * 获取服务意图
	 * 
	 * @return
	 */
	private Intent getBindService(Class<?> cls) {
		Intent intent = new Intent(MainActivity.this, cls);
		return intent;
	}
}
