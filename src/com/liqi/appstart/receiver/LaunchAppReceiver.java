package com.liqi.appstart.receiver;

import com.liqi.appstart.service.AppStart_Monitor_Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 开机自启之后开启服务
 * 
 * @author Administrator
 * 
 */
public class LaunchAppReceiver extends BroadcastReceiver {
	//系统重启监听
	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context context, Intent intent) {
		String action=intent.getAction();
		//重启监听
		if (ACTION.equals(action))
			context.startService(getBindService(context,
					AppStart_Monitor_Service.class));
	}

	/**
	 * 获取服务意图
	 * 
	 * @return
	 */
	private Intent getBindService(Context context, Class<?> cls) {
		Intent intent = new Intent(context, cls);
		return intent;
	}
}
