package com.suda.msgcenter.util;


import com.suda.msgcenter.main.SudaConstants;

import android.util.Log;

public class LogUtil {
	public static void e(String msg) {
		if (SudaConstants.DEBUG_MODE)
			Log.e(SudaConstants.APP_TAG, msg);
	}

	public static void w(String msg) {
		if (SudaConstants.DEBUG_MODE)
			Log.w(SudaConstants.APP_TAG, msg);
	}

	public static void d(String msg) {
		if (SudaConstants.DEBUG_MODE)
			Log.d(SudaConstants.APP_TAG, msg);
	}

	public static void i(String msg) {
		if (SudaConstants.DEBUG_MODE)
			Log.i(SudaConstants.APP_TAG, msg);
	}

	public static void v(String msg) {
		if (SudaConstants.DEBUG_MODE)
			Log.v(SudaConstants.APP_TAG, msg);
	}

	public static void e(String tag, String msg) {
		if (SudaConstants.DEBUG_MODE)
			Log.e(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (SudaConstants.DEBUG_MODE)
			Log.w(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (SudaConstants.DEBUG_MODE)
			Log.d(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (SudaConstants.DEBUG_MODE)
			Log.i(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (SudaConstants.DEBUG_MODE)
			Log.v(tag, msg);
	}
}
