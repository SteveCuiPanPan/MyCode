package com.suda.msgcenter.ui;

import java.util.ArrayList;
import java.util.List;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.suda.msgcenter.R;
import com.suda.msgcenter.api.InfoDataModel;
import com.suda.msgcenter.api.InfoDataModel.InfoDataCompleteListener;
import com.suda.msgcenter.bean.NewsBean;
import com.suda.msgcenter.bean.NotiBean;
import com.suda.msgcenter.main.SudaApplication;
import com.suda.msgcenter.main.SudaConstants;
import com.suda.msgcenter.ui.NotiFragment.NotiFragmentListener;
import com.suda.msgcenter.util.LogUtil;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends FragmentActivity {

	private ViewPager vpager;

	private TextView tv1, tv2;
	private ImageView iv;
	private Button back;
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 1;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private Paint paint;
	private float textwidth;

	private InfoDataModel infoModel;
	private List<Fragment> fragmentlist;
	private NewsFragment newsfragment;
	private NotiFragment notifragment;
	private String[] title;

	private boolean hasNoti = false;
	private boolean loginChecked = false;

	private final static int NEWS_ERROR = -1;
	private final static int NOTI_ERROR = -2;
	private final static int LOGIN_ERROR = -3;
	private final static int ALL_SUCCESS = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);

		InitTextView();
		InitImageView();
		InitViewPager();
		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY, SudaConstants.API_KEY);

		processIntent(getIntent());
	}

	private void InitImageView() {

		paint = tv1.getPaint();
		textwidth = paint.measureText(tv1.getText().toString());
		bmpW = (int) textwidth;

		iv = (ImageView) findViewById(R.id.cursor);
		LayoutParams params = (LayoutParams) iv.getLayoutParams();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		int marginleft = screenW / 4 - bmpW / 2;
		params.setMargins(marginleft, 0, 0, 0);
		iv.setLayoutParams(params);
		offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
	}

	private void InitTextView() {
		tv1 = (TextView) findViewById(R.id.tv1);
		tv2 = (TextView) findViewById(R.id.tv2);
		back = (Button)findViewById(R.id.button_back);

		tv1.setOnClickListener(new MyOnClickListener(0));
		tv2.setOnClickListener(new MyOnClickListener(1));
		
		back.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 两个标题的点击事件
	 */
	private class MyOnClickListener implements OnClickListener {
		int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			vpager.setCurrentItem(index);
		}
	}

	private void InitViewPager() {

		vpager = (ViewPager) findViewById(R.id.vPager);
		title = new String[] { "苏大头条", "校内通知" };
		fragmentlist = new ArrayList<Fragment>();
		newsfragment = new NewsFragment();
		notifragment = new NotiFragment();
		FragmentManager fm = getSupportFragmentManager();

		fragmentlist.add(newsfragment);
		fragmentlist.add(notifragment);

		infoModel = new InfoDataModel(mlistener);

		vpager.setAdapter(new MyViewPagerAdapter(fm));
		vpager.setCurrentItem(0);
		vpager.setOnPageChangeListener(new MyPageChangeListener());

	}

	/**
	 * (一) 实现InfoDataModel的监听的回调接口，判断各数据信息是否完成 只是方法
	 */
	private InfoDataCompleteListener mlistener = new InfoDataCompleteListener() {

		// 新闻获取是否成功
		@Override
		public void OnNewsDataComplete(int flag, List<NewsBean> beans) {
			// dismissWaitDlg();

			if (flag == ALL_SUCCESS) {
				// View view = View.inflate(getApplicationContext(),
				// R.layout.news_fragment, null);
				// ll_news_loading = (LinearLayout)
				// view.findViewById(R.id.ll_news_loading);
				// ll_news_loading.setVisibility(View.GONE);
				//
				// hasNews = true;
				// newsfragment.setNewsData(beans); //--->NewsFragment
				// beans<---InfoDataModel
				// } else if (flag == NEWS_ERROR) {
				// Toast.makeText(getApplicationContext(), "获取新闻失败",
				// Toast.LENGTH_SHORT).show();
			}

		}

		// 通知获取是否成功
		@Override
		public void OnNotiDataComplete(int flag, List<NotiBean> beans) {
			
			if (flag == ALL_SUCCESS) {
				hasNoti = true;
				notifragment.setNotiData(beans);
			} else if (flag == NOTI_ERROR) {
				Toast.makeText(getApplicationContext(), "获取通知失败",
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	/**
	 * 装载Fragment的适配器(newsfragment和notifragment;)
	 * 
	 */
	private class MyViewPagerAdapter extends FragmentPagerAdapter {

		public MyViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragmentlist.get(arg0);
		}

		@Override
		public int getCount() {
			return fragmentlist.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {

			return title[position];
		}
	}

	/**
	 * 处理viewPager页面滑动的事件
	 */
	private class MyPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;

		/**
		 * 状态改变的时候调用 arg0 ==1时默示正在滑动，arg0==2时默示滑动完毕了，arg0==0时默示什么都没做。
		 */
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		/**
		 * 当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法会一直得到 arg0:当前页面，及你点击滑动的页面 arg1:当前页面偏移的百分比
		 * arg2:当前页面偏移的像素位置
		 */
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		/**
		 * 此方法是页面跳转完后得到调用，arg0是你当前选中的页面的Position（位置编号）。
		 */
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = new TranslateAnimation(one * currIndex, one
					* arg0, 0, 0);
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			iv.startAnimation(animation);
			if (arg0 == 0) {
				tv1.setTextColor(Color.parseColor("#2894FF"));
				tv2.setTextColor(Color.parseColor("#000000"));
				// switchToNewsPage(false);
			}
			if (arg0 == 1) {
				tv1.setTextColor(Color.parseColor("#000000"));
				tv2.setTextColor(Color.parseColor("#2894FF"));
				//switchToNotiPage(false);
			}
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		LogUtil.d("Activity onNewIntent()");
		processIntent(intent);
	}

	private void processIntent(Intent intent) {
		String action = intent.getAction();
		LogUtil.d(action == null ? "no action" : action);
		if (SudaConstants.ACTION_NOTI_CLICK.equals(action)) {
			vpager.setCurrentItem(1);
		}
		// 否则，保持原状
		//if (vpager.getCurrentItem() != 0)
			// switchToNewsPage(true);
			// else
			//switchToNotiPage(true);
	}

	@Override
	protected void onPause() {
		//dismissWaitDlg();
		super.onPause();
	}

/*	private void switchToNotiPage(boolean needRefresh) {

		if (needRefresh || !hasNoti) {
			//infoModel.getNoti(1);
		}

	}*/
	

/*	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}*/

}
