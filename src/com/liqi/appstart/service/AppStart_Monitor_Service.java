package com.liqi.appstart.service;


import com.liqi.appstart.utils.FileAllUtlis;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 监听指定项目是否已经打开，没有打开给它激活打开
 * 
 * @author Liqi
 * 
 */
public class AppStart_Monitor_Service extends Service {
	// 从本地指定文件里面获取包名
	private String pkgName;
	private boolean judgeAppRun = true;
	// ASE解锁密码
	private final String PASSWORD = "Liqi543945827";

	/**
	 * �?��心跳机制线程
	 */
	private void judgeAppRun() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						System.out.println("AppStart_Monitor-->心跳执行");
						Thread.sleep(10 * 1000);
						String path = FileAllUtlis.getPath(
								AppStart_Monitor_Service.this,
								"AppStart/Monitor/Pag/appStart.txt");
						pkgName = FileAllUtlis.inputFileContent(PASSWORD, path);
						//System.out.println("是否为空>>>>>" + pkgName);
						if (null != pkgName && !"".equals(pkgName)) {
							String pkgNames[] = pkgName.split("-");
							//System.out.println("切割出来的数据>>>"+Arrays.toString(pkgNames));
							
							if (pkgNames.length > 1) {
								if (FileAllUtlis.isAppInstalled(
										AppStart_Monitor_Service.this, pkgNames[0])) {

//									System.out.println(pkgNames[0]
//											+ "<<<<<<已经安装>>>>>>>");

									if (!FileAllUtlis.isAppRunning(pkgNames[0],
											AppStart_Monitor_Service.this)) {

//										System.out.println(pkgNames[0]
//												+ "------没有运行-----");

										ComponentName comp = new ComponentName(
												pkgNames[0],pkgNames[1]);
										// 打开一个新的APP
										AppStart_Monitor_Service.this.startActivity(FileAllUtlis
												.startActivityNew(comp));
									} 
									//else
//										System.out.println(pkgNames[0]
//												+ ">>>>>>>已经在运行<<<<<<<");
								} else{
									 System.out.println(pkgName+ "=======没有安装=======");
									 //如果项目被卸载了，就删掉本项目保存的配置内容
									 FileAllUtlis.DeleteFolder(path);
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}).start();
	}

	@Override
	public void onCreate() {
		Log.e("服务创建", "服务创建>>>>>");
	};

	// 绑定服务是不走此生命周期�?
	@Override
	public void onStart(Intent intent, int startId) {
		Log.e("服务运行", "服务运行>>>>>");
		// 保证心跳线程单一线程运行(程序意外结束的时候执�?
		if (judgeAppRun) {
			judgeAppRun();
			judgeAppRun = false;
		}
	}

	// 绑定服务是不走此生命周期�?
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("指令运行", "指令运行>>>>>");
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
