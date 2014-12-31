package com.suda.msgcenter.main;

import java.util.List;

import com.baidu.frontia.FrontiaApplication;
import com.suda.msgcenter.util.LogUtil;

import android.app.Application;
import android.content.SharedPreferences;

public class SudaApplication extends Application {

	private static SudaApplication app;
	private SharedPreferences settings;
	public List<String> tagList;
	public static int SCREEN_WEIDTH = 0;
	public static int SCREEN_HEIGTH = 0;
	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		settings = getSharedPreferences("APP_SETTING", MODE_PRIVATE);
		SCREEN_WEIDTH = getResources().getDisplayMetrics().widthPixels;
		SCREEN_HEIGTH = getResources().getDisplayMetrics().heightPixels;
		FrontiaApplication.initFrontiaApplication(getApplicationContext());
	}

	public static SudaApplication getInstance() {
		return app;
	}

	public String getSaveString(String key) {
		return settings.getString(key, "");
	}

	public void setSaveString(String key, String value) {
		settings.edit().putString(key, value).commit();
	}

	public boolean getSaveBoolean(String key) {
		return settings.getBoolean(key, false);
	}

	public void setSaveBoolean(String key, boolean value) {
		settings.edit().putBoolean(key, value).commit();
	}
}
