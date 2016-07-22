package com.liqi.appstart.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

public class FileAllUtlis {
	/**
	 * 根据路径删除指定的目录或文件，无论存在与否
	 * 
	 * @param sPath
	 *            要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false。
	 */
	public static boolean DeleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return flag;
		} else {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				return deleteFile(file);
			} else { // 为目录时调用删除目录方法
				return deleteDirectory(sPath);
			}
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param sPath
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	private static boolean deleteFile(File file) {
		boolean flag = false;
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param sPath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	private static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i]);
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断程序是否运行
	 * 
	 * @return
	 */
	public static boolean isAppRunning(String pgkName, Context context) {
		boolean isAppRunning = false;
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(pgkName)
					&& info.baseActivity.getPackageName().equals(pgkName)) {
				isAppRunning = true;
				System.out.println("pgkName+<<<程序运行中>>>");
				break;
			}
		}
		return isAppRunning;
	}

	/**
	 * 通过Uri去判断程序是不是已经安装
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isAppInstalledUri(Context context, String uri) {
		PackageManager pm = context.getPackageManager();
		boolean installed = false;
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			installed = false;
		}
		return installed;
	}

	/**
	 * 通过包名去判断程序是不是已经安装
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isAppInstalled(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		List<String> pName = new ArrayList<String>();
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(packageName);
	}

	/**
	 * 根据标识打开一个其它APPA
	 * 
	 * @param comp
	 */
	public static Intent startActivityNew(ComponentName comp) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(comp);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction("android.intent.action.VIEW");
		System.out.println("<<<开启一个新的APP>>>");
		return intent;
	}

	/**
	 * 查看手机本地存储加密的包名
	 * 
	 * @param miMa
	 *            文件密码
	 * @param path
	 *            文件路径
	 * @throws Exception
	 */
	public static String inputFileContent(String password, String path) {
		String content = null;
		try {
			File file = new File(path);
			if (isFilePath(file)) {
				FileInputStream inputStream = new FileInputStream(file);
				ByteArrayOutputStream bytePutStream = new ByteArrayOutputStream();
				if (password != null) {
					byte[] buffer = new byte[1024];
					int con = 0;
					while ((con = inputStream.read(buffer)) != -1) {
						bytePutStream.write(buffer, 0, con);
					}
					bytePutStream.close();

					inputStream.close();
					// 文件解密
					content = AESEncryptor.decrypt(password,
							bytePutStream.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * 判断路径是否存在
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isFilePath(File file) {
		boolean isFile = false;
		if (file.exists() && file.isFile()) {
			isFile = true;
		} else {
			isFile = false;
		}
		return isFile;

	}

	/**
	 * 获取保存路径
	 * 
	 * @param activity
	 * @param name
	 *            路径名字
	 * @return
	 */
	public static String getPath(Context activity, String name) {
		String path = "";
		// 判断是否安装有SD卡
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			path = Environment.getExternalStorageDirectory() + "/" + name;
		} else {
			path = activity.getCacheDir() + "/" + name;
		}
		return path;
	}
}
