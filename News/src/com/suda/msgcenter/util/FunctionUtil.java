package com.suda.msgcenter.util;

import java.io.File;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.suda.msgcenter.R;
import com.suda.msgcenter.main.SudaApplication;
import com.suda.msgcenter.main.SudaConstants;

public class FunctionUtil {

	public static String EncoderByMd5(String str) {
		if (TextUtils.isEmpty(str)) {
			return "";
		}
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md5.update(str.getBytes());
		String strDes = bytes2Hex(md5.digest()); // to HexString
		return strDes;
	}

	private static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;

		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

	public static boolean isImageFile(String fileName) {
		if (TextUtils.isEmpty(fileName)) {
			return false;
		}
		if (fileName.toLowerCase().endsWith(".bmp")) {
			return true;
		}
		if (fileName.toLowerCase().endsWith(".png")) {
			return true;
		}

		if (fileName.toLowerCase().endsWith(".jpg")) {
			return true;
		}

		if (fileName.toLowerCase().endsWith(".gif")) {
			return true;
		}

		return false;
	}

	public static boolean isToday(Date date) {
		if (date == null) {
			return false;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Calendar today = Calendar.getInstance();

		if (cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
			return true;
		}
		return false;

	}

	public static boolean isOverTime(Date date, int minute) {
		if (date == null) {
			return false;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Calendar now = Calendar.getInstance();

		if ((now.getTimeInMillis() - date.getTime()) > minute * 60 * 1000) {
			return true;
		}
		return false;
	}

	public static Date parseChineseDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINESE);
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public static Date parseDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d", Locale.US);
		try {
			return sdf.parse(str.trim());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public static Date parseTime(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d HH:mm", Locale.US);
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public static Date parseDateTime(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public static Date parseCurrentTime(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	
	public static String getDateString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
		return sdf.format(date == null ? new Date() : date);
	}

	public static String getParaDateString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
		return sdf.format(date == null ? new Date() : date);
	}

	public static Date parseParaDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public static String getMonthDayString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		return sdf.format(date == null ? new Date() : date);
	}
	public static String getMonthString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.US);
		return sdf.format(date == null ? new Date() : date);
	}
	public static String getMonthString2(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM", Locale.US);
		return sdf.format(date == null ? new Date() : date);
	}

	public static String getTimeString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
		return sdf.format(date == null ? new Date() : date);
	}

	public static String getLogTimeString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.US);
		return sdf.format(date == null ? new Date() : date);
	}

	public static String getDateString(String date) {
		String dateString = date;
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年M月d日", Locale.CHINESE);
			dateString = sdf2.format(new SimpleDateFormat("yyyy-MM-dd").parse(date));
		} catch (ParseException e) {
		}
		return dateString;
	}

	public static String parseHourTime(String str) {
		String dateString = str;
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm", Locale.US);
			dateString = sdf2.format(new SimpleDateFormat("HH:mm:ss", Locale.US).parse(str));
		} catch (ParseException e) {
			 dateString = str;
		}
		return dateString;
	}
	
	public static String getDateTimeString(String date) {
		String dateString = date;
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE);
			dateString = sdf2.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date));
		} catch (ParseException e) {
		}
		return dateString;
	}

	public static String getMonthTimeString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.US);
		return sdf.format(date == null ? new Date() : date);
	}

	public static String getDateString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		return sdf.format(date == null ? new Date() : date);
	}

	public static boolean isTimestampChanged(Date d1, Date d2) {
		String s1 = getTimeString(d1);
		String s2 = getTimeString(d2);

		return !s1.equals(s2);
	}

	public static Date lastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, value);
		return cal.getTime();
	}

	public static boolean isSameDay(Date d1, Date d2) {
		String s1 = getDateString(d1);
		String s2 = getDateString(d2);

		return s1.equals(s2);
	}

	public static String ToDBC(String input) {// 全角转半角
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public static String ToSBC(String input) {
		// 半角转全角：
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue;
			}
			if (c[i] < 127 && c[i] > 32)
				c[i] = (char) (c[i] + 65248);
		}
		return new String(c);
	}

	public static boolean isWithinMinutes(Calendar c1, Calendar c2, int minutes) {
		if (c1 == null || c2 == null) {
			return false;
		}
		long pass = Math.abs(c2.getTimeInMillis() - c1.getTimeInMillis());

		if (pass < (minutes * 60 * 1000)) {
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param localVersion
	 *            String
	 * @param serverVersion
	 *            String
	 * @return 0 = same, -1 = local version newer than server, 1 = local version
	 *         old than server, -2 unknown
	 */
	public static int checkVersion(String localVersion, String serverVersion) {
		try {
			if (TextUtils.isEmpty(localVersion) || TextUtils.isEmpty(serverVersion)) {
				return -2;
			}
			String[] localVs = localVersion.split("\\.");
			String[] serverVs = serverVersion.split("\\.");

			int index = Math.min(localVs.length, serverVs.length);
			for (int i = 0; i < index; i++) {
				if (getInt(localVs[i]) == getInt(serverVs[i])) {
					continue;
				} else if (getInt(localVs[i]) < getInt(serverVs[i])) {
					return 1;
				} else {
					return -1;
				}
			}
		} catch (Exception e) {
			return 1;
		}
		return 0;
	}

	public static int getInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return 0;
		}
	}

	public static void openFile(File f, Context context) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getMIMEType(f);
		intent.setDataAndType(Uri.fromFile(f), type);
		context.startActivity(intent);

	}

	public static String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg")
				|| end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg")
				|| end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			type = "application/vnd.android.package-archive";
		} else {
			type = "*";
		}
		if (end.equals("apk")) {
		} else {
			type += "/*";
		}
		return type;
	}

	public static String formatNumberStr(String str) {
		if (str == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			int chr = str.charAt(i);
			if (chr >= 0x30 && chr <= 0x39) {
				sb.append((char) chr);
			}
		}
		return sb.toString();
	}

	public static boolean isMobileNumber(String str) {
		String s = formatNumberStr(str);
		if (("" + s).length() == 11 && s.startsWith("1")) {
			return true;
		}
		return false;
	}

	public static boolean isSDCardExist() {
		return (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED));
	}

	public static boolean isSDCardReadOnly() {
		return (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED_READ_ONLY));
	}

	public static boolean isNetworkConnected() {
		ConnectivityManager cManager = (ConnectivityManager) SudaApplication.getInstance().getSystemService(
				Context.CONNECTIVITY_SERVICE);

		if (cManager != null) {
			NetworkInfo localNetworkInfo = cManager.getActiveNetworkInfo();
			if (localNetworkInfo != null)
				return localNetworkInfo.isConnectedOrConnecting();
		}
		return false;
	}

	public static boolean is3GNetwork() {
		ConnectivityManager cManager = (ConnectivityManager) SudaApplication.getInstance().getSystemService(
				Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null) {
			if (info.getState().name().equalsIgnoreCase("connected") && info.getTypeName().equalsIgnoreCase("mobile")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param context
	 * @param netType
	 *            1:WIFI 2:MOBILE
	 * @return
	 */
	public static boolean is3GNetwork(Context context, int netType) {
		ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info == null) {
			LogUtil.i(SudaConstants.APP_TAG, "Network is Unusable");
			return false;
		} else {
			LogUtil.i(SudaConstants.APP_TAG,
					"#Network is :--->" + info.getState().name() + ",Nettype :--->" + info.getTypeName());
			String type = "MOBILE";
			if (netType == 1) {
				type = "WIFI";
			} else if (netType == 2) {
				type = "MOBILE";
			}
			if (info.getState().name().equalsIgnoreCase("connected") && info.getTypeName().equalsIgnoreCase(type)) {// 1:WIFI
																													// 2:MOBILE
				return true;
			}
		}
		return false;
		/*
		 * if (info != null) { String type = "MOBILE" ; if(netType == 1){ type =
		 * "WIFI"; }else if(netType == 2){ type = "MOBILE"; }
		 * 
		 * if (info.getState().name().equalsIgnoreCase("connected") &&
		 * info.getTypeName().equalsIgnoreCase(type)) {//1:WIFI 2:MOBILE
		 * LogUtil.i(TAG, "#Network is "+type+" mode!"); return true; } }
		 * 
		 * return false;
		 */
	}
	// 判断当前时间是否在时间段内
	public static boolean isNow(int startHour, int startMinute, int endHour, int endMinute) {
		SimpleDateFormat formatters = new SimpleDateFormat("HH:mm");
		Date curDates = new Date(System.currentTimeMillis());// 获取当前时间
		String strs = formatters.format(curDates);
		String[] curStr = new String[]{};
		curStr = strs.split(":");
		int curHour = Integer.parseInt(curStr[0]);
		int curMinute = Integer.parseInt(curStr[1]);

		if (startHour <= curHour && curHour <= endHour) {
			if (startHour <= curHour && startMinute <= curMinute) {
				return true;
			} else if (endHour >= curHour && curMinute <= endMinute) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isActive(Date begainDate, Date endDate) {
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间

		if (begainDate != null && endDate != null) {
			int begainYear = begainDate.getYear();
			int begainMonth = begainDate.getMonth();
			int begainDay = begainDate.getDay();
			int endYear = endDate.getYear();
			int endMonth = endDate.getMonth();
			int endDay = endDate.getDay();
			int curYear = curDate.getYear();
			int curMonth = curDate.getMonth();
			int curDay = curDate.getDay();

			if ((begainDate.before(curDate) && endDate.after(curDate))
					|| (begainYear == curYear && begainMonth == curMonth && begainDay == curDay)
					|| (endYear == curYear && endMonth == curMonth && endDay == curDay)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	public static boolean isChinaNet() {
		TelephonyManager tm = (TelephonyManager) SudaApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
		return "46003".equals(tm.getNetworkOperator());
	}
	public static String getMobileModel() {
		String s = "";
		if (Build.MODEL == null) {
			s = Build.MANUFACTURER;
		} else {
			s = Build.MODEL.startsWith(Build.MANUFACTURER) ? Build.MODEL : (Build.MANUFACTURER + "," + Build.MODEL);
		}
		return s;
	}

	public static boolean hasAddShortcut(Context context) {
		boolean isInstallShortcut1 = false;
		boolean isInstallShortcut2 = false;
		final android.content.ContentResolver cr = context.getContentResolver();
		final String AUTHORITY = "com.android.launcher.settings";
		final Uri CONTENT_URI_1 = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
		android.database.Cursor c1 = cr.query(CONTENT_URI_1, new String[]{"title", "iconResource"}, "title=?",
				new String[]{context.getResources().getString(R.string.app_name).trim()}, null);
		if (c1 != null && c1.getCount() > 0) {
			isInstallShortcut1 = true;
		}

		if (c1 != null)
			c1.close();

		final String AUTHORITY_2 = "com.android.launcher2.settings";
		final Uri CONTENT_URI_2 = Uri.parse("content://" + AUTHORITY_2 + "/favorites?notify=true");
		android.database.Cursor c2 = cr.query(CONTENT_URI_2, new String[]{"title", "iconResource"}, "title=?",
				new String[]{context.getResources().getString(R.string.app_name).trim()}, null);
		if (c2 != null && c2.getCount() > 0) {
			isInstallShortcut2 = true;
		}

		if (c2 != null)
			c2.close();

		return isInstallShortcut1 || isInstallShortcut2;
	}

	public static void delOldShortcut(Context context) {
		Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
		String appClass = context.getPackageName() + ".WapGroupActivity";

		shortcut.setComponent(new android.content.ComponentName(context.getPackageName(), appClass));

		context.sendBroadcast(shortcut);
	}

	public static boolean isSportVoice(Context context) {
		android.content.pm.PackageManager pm = context.getPackageManager();
		List activities = pm.queryIntentActivities(new Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH),
				0);
		return activities.size() != 0;
	}


	public static String changeUrl(String wapurl) {
		String url = wapurl;
		if (!TextUtils.isEmpty(wapurl) && wapurl.contains("http-")) {
			url = wapurl.replaceFirst("-", ":");
		}
		return url;
	}

	public static int checkVendor(String phnum) {
		if (!isMobileNumber(phnum)) {
			return -1;
		}
		List<String> telecom = Arrays.asList(new String[]{"133", "153", "189", "180", "181"});
		List<String> unicom = Arrays.asList(new String[]{"130", "131", "132", "145", "155", "156", "185", "186"});
		List<String> mobile = Arrays.asList(new String[]{"134", "135", "136", "137", "138", "139", "147", "150", "151",
				"152", "157", "158", "159", "182", "187", "188"});

		String header = phnum.substring(0, 3);
		if (telecom.contains(header)) {
			return 0;
		} else if (mobile.contains(header)) {
			return 1;
		} else if (unicom.contains(header)) {
			return 2;
		} else {
			return 3;
		}
	}

	public static boolean isMobile(String mobiles) {
		java.util.regex.Pattern p = java.util.regex.Pattern
				.compile("^((13[0-9])|(15[0-3])|(15[5-9])|(18[0-2])|(18[5-9])|145|147)\\d{8}$");
		java.util.regex.Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(str);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}

	public static boolean isTel(String tel) {
		String str = "^\\d{3}-?\\d{8}|\\d{4}-?\\d{8}$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(str);
		java.util.regex.Matcher m = p.matcher(tel);
		return m.matches();
	}

	public static boolean isValidToken(String token) {
		String str = "^v+[0-9]{1,3}_[0-9a-fA-F]+$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(str);
		java.util.regex.Matcher m = p.matcher(token);
		return m.matches();
	}

	public static String getCurrentVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * 校验QQ号 要求： 1. 5-15位 2. 0不能开头 3. 只能是数字
	 */
	public static boolean isQQNum(String qq) {
		String regex = "[1-9][0-9]{4,14}";
		return qq.matches(regex);
	}

	public static String getDeviceID(Context ct) {
		TelephonyManager teleMgr = (TelephonyManager) ct.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = teleMgr.getDeviceId();
		boolean isZore = false;
		try {
			if (Integer.parseInt(deviceId) == 0) {
				isZore = true;
			}
		} catch (Exception e) {
			isZore = false;
		}
		if (TextUtils.isEmpty(deviceId) || isZore) {
			deviceId = getDeviceInfo();
		}
		return deviceId;
	}

	public static String getDeviceInfo() {
		String m_szDevIDShort = android.os.Build.BOARD + android.os.Build.BRAND + android.os.Build.CPU_ABI
				+ android.os.Build.DEVICE + android.os.Build.DISPLAY + android.os.Build.HOST
				+ android.os.Build.MANUFACTURER + android.os.Build.MODEL + android.os.Build.PRODUCT
				+ android.os.Build.TYPE + android.os.Build.USER; // 13 digits
		// we make this look like a valid IMEI
		return m_szDevIDShort;
	}

	public static String formatDouble(double d) {
		return String.format("%.2f", d);
	}

	public static String formatDouble1f(double d) {
		return String.format("%.1f", d);
	}

	/**
	 * 返回 -1:网络不通; 0：wifi; 1：3G; 2：2G
	 */
	public static int getNetworkType(Context ct) {
		ConnectivityManager manager = (ConnectivityManager) ct.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			return -1;
		}
		int netType = info.getType();
		int netSubtype = info.getSubtype();
		if (netType == ConnectivityManager.TYPE_WIFI) {
			return 0;
		} else if (netType == ConnectivityManager.TYPE_MOBILE
				&& (netSubtype == TelephonyManager.NETWORK_TYPE_GPRS || netSubtype == TelephonyManager.NETWORK_TYPE_EDGE)) {
			return 2;
		} else {
			return 1;
		}
	}

	/**
	 * 判断当前界面是否是桌面
	 */
	public static boolean isHome(Context c) {
		ActivityManager mActivityManager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		return getHomes(c).contains(rti.get(0).topActivity.getPackageName());
	}
	/**
	 * 判断当前界面是否显示悬浮框
	 */
	public static boolean isFloatDlgShown(Context c) {
		ActivityManager mActivityManager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		return rti.get(0).topActivity.getClassName().contains("FloatDlgActivity");
	}
	/**
	 * 获得属于桌面的应用的应用包名称
	 * 
	 * @return 返回包含所有包名的字符串列表
	 */
	public static List<String> getHomes(Context c) {
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = c.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfo) {
			names.add(ri.activityInfo.packageName);
		}
		return names;
	}

	public static int getResourceByReflect(String imageName) {
		Class drawable = R.drawable.class;
		Field field = null;
		int r_id = 0;
		try {
			field = drawable.getField(imageName);
			r_id = field.getInt(field.getName());
		} catch (Exception e) {
			r_id = 0;
			Log.e("WXCS", "getResourceByReflect", e);
		}
		return r_id;
	}
}
