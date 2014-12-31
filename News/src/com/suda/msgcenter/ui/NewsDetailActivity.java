package com.suda.msgcenter.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.suda.msgcenter.R;
import com.suda.msgcenter.api.MsgCenterAPI;
import com.suda.msgcenter.bean.CommentBean;
import com.suda.msgcenter.main.SudaConstants;
import com.suda.msgcenter.util.ImageCache;
import com.suda.msgcenter.util.ImageWorker;
import com.suda.msgcenter.util.URLImageParser;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class NewsDetailActivity extends Activity implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final int COMMENT_SUCCESS = 1;
	private static final int COMMENT_ERROR = 0;
	private static final int GET_COMMENT_COUNT_SUCCESS = 2;
	private static final int GET_COMMENT_COUNT_ERROR = 3;
	private static final int GET_NEWS_CONTENT_SUCCESS = 4;
	private static final int GET_NEWS_CONTENT_ERROR = 5;

	Handler mmHandler = new Handler();

	private ImageView iv_loadingfail;

	private LinearLayout ll_news_loading, ll_news_loading_icon, ll_comment;
	private RelativeLayout rl_content;
	private EditText et_comment;
	private TextView tv_send, tv_comment_count;
	private TextView tv_title, tv_time, tv_content;
	private Button btn_back, btn_share;
	private String topicId;
	private String comment;
	private String count;
	private ScrollView sView;
	String content;
	URLImageParser uParser;
	Handler mHandler;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_NEWS_CONTENT_SUCCESS:
				if (ll_news_loading.getVisibility() != View.GONE) {
					ll_news_loading.setVisibility(View.GONE);
				}
				if (rl_content.getVisibility() != View.VISIBLE) {
					rl_content.setVisibility(View.VISIBLE);
				}
				uParser = new URLImageParser(tv_content, imgeWorker, content,
						mHandler);
				
				Spanned htmlSpan = Html.fromHtml(content, uParser, null);
				tv_content.setText(htmlSpan);

				
				break;
			case GET_NEWS_CONTENT_ERROR:
				ll_news_loading_icon.setVisibility(View.GONE);
				if (iv_loadingfail.getVisibility() != View.VISIBLE) {
					iv_loadingfail.setVisibility(View.VISIBLE);
				}
				// Toast.makeText(NewsDetailActivity.this, "评论成功", 0).show();
				break;
			case COMMENT_SUCCESS:

				Toast.makeText(NewsDetailActivity.this, "评论成功", 0).show();
				break;

			case COMMENT_ERROR:

				Toast.makeText(NewsDetailActivity.this, "评论失败", 0).show();
				break;

			case GET_COMMENT_COUNT_SUCCESS:
				if (tv_comment_count.getVisibility() != View.VISIBLE) {
					tv_comment_count.setVisibility(View.VISIBLE);
				}
				tv_comment_count.setText(count + "跟帖");

				break;

			case GET_COMMENT_COUNT_ERROR:
				if (tv_comment_count.getVisibility() != View.VISIBLE) {
					tv_comment_count.setVisibility(View.VISIBLE);
				}
				tv_comment_count.setText("0" + "跟帖");
				break;
			}
		};
	};

	public ImageWorker imgeWorker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_details);
		sView = (ScrollView) findViewById(R.id.sView);
		sView.setVerticalScrollBarEnabled(false);
		sView.setHorizontalScrollBarEnabled(false);
		rl_content = (RelativeLayout) findViewById(R.id.rl_comment);
		ll_comment = (LinearLayout) findViewById(R.id.ll_comment);
		ll_news_loading = (LinearLayout) findViewById(R.id.ll_news_loading);
		ll_news_loading_icon = (LinearLayout) findViewById(R.id.ll_news_loading_icon);
		iv_loadingfail = (ImageView) findViewById(R.id.iv_loadingfail);
		tv_comment_count = (TextView) findViewById(R.id.tv_comment_count);
		et_comment = (EditText) findViewById(R.id.et_comment);

		tv_send = (TextView) findViewById(R.id.tv_send);
		tv_title = (TextView) findViewById(R.id.tv_news_title);
		tv_time = (TextView) findViewById(R.id.tv_news_time);
		tv_content = (TextView) findViewById(R.id.tv_news_detailweb);
		btn_back = (Button) findViewById(R.id.button_back);
		btn_share = (Button) findViewById(R.id.button_share);

		tv_title.setText(getIntent().getStringExtra("newstitle"));
		tv_time.setText(getIntent().getStringExtra("newstime"));

		topicId = getIntent().getStringExtra("topicid");
		getNewsContent();
		getCommentCount();

		mHandler = new Handler();
		imgeWorker = getImageWorker();

		btn_back.setOnClickListener(listener);
		btn_share.setOnClickListener(listener);
		tv_comment_count.setOnClickListener(listener);
		tv_send.setOnClickListener(listener);
		ll_comment.setOnClickListener(listener);
		iv_loadingfail.setOnClickListener(listener);
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_back:
				finish();
				break;

			case R.id.button_share:
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				// 需要指定意图的数据类型
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享"); // 要分享的主题
				shareIntent.putExtra(Intent.EXTRA_TEXT, "掌上苏大发布新闻:"
						+ getIntent().getStringExtra("newstitle")); // 要分享的内容
				shareIntent = Intent.createChooser(shareIntent, "分享");
				startActivity(shareIntent);
				break;

			case R.id.ll_comment:
				if (ll_comment.getVisibility() != View.GONE) {
					ll_comment.setVisibility(View.GONE);
					et_comment.setVisibility(View.VISIBLE);
					et_comment.requestFocus();

					// 获取编辑框焦点
					et_comment.setFocusable(true);
					// 打开软键盘
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					tv_send.setClickable(true);
					tv_send.setEnabled(true);
					break;
				}

			case R.id.tv_comment_count:
				Intent commentIntent = new Intent(NewsDetailActivity.this,
						CommentActivity.class);

				//commentIntent.putExtra("list", (Serializable) commentBeans);
				commentIntent.putExtra("topicid", topicId);
				startActivity(commentIntent);

				break;
			case R.id.tv_send:

				comment = et_comment.getText().toString();
				if (TextUtils.isEmpty(comment)) {
					Toast.makeText(NewsDetailActivity.this, "请输入评论内容", 0)
							.show();
				} else {
					sendTopicComment();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					if (ll_comment.getVisibility() == View.GONE) {
						ll_comment.setVisibility(View.VISIBLE);
						et_comment.setVisibility(View.GONE);
					}
				}
				break;

			case R.id.iv_loadingfail:
				iv_loadingfail.setVisibility(View.GONE);
				ll_news_loading_icon.setVisibility(View.VISIBLE);
				getNewsContent();
				break;
			}
		}
	};

	public ImageWorker mImageWorker = null;

	public ImageWorker getImageWorker() {
		if (mImageWorker == null) {
			ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams();
			ImageWorker.ScreenWeith = getResources().getDisplayMetrics().widthPixels;
			cacheParams.memCacheSize = (1024 * 1024 * getMemoryClass(getApplicationContext())) / 4;
			// cacheParams.clearDiskCacheOnStart = true;
			mImageWorker = ImageWorker.newInstance();
			// mImageWorker.setCommonResID(R.drawable.ic_launcher,
			// R.drawable.ic_launcher);
			mImageWorker.addParams(SudaConstants.APP_TAG, cacheParams);
		}
		return mImageWorker;
	}

	public int getMemoryClass(Context context) {
		return ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (ll_comment.getVisibility() == View.GONE) {
				ll_comment.setVisibility(View.VISIBLE);
				et_comment.setVisibility(View.GONE);
			} else {
				finish();
			}
		}
		return false;
	}

	public void getNewsContent() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean getnewscontent = false;

				try {
					String jsonstr = MsgCenterAPI.getNewsContent(topicId);
					if (!TextUtils.isEmpty(jsonstr)) {
						JSONObject jsonobj = new JSONObject(jsonstr);
						content = jsonobj.getString("content");

						if (!TextUtils.isEmpty(content)) {
							getnewscontent = true;
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (getnewscontent)
					handler.sendEmptyMessage(GET_NEWS_CONTENT_SUCCESS);
				else
					handler.sendEmptyMessage(GET_NEWS_CONTENT_ERROR);

			}
		}).start();
	}

	public void sendTopicComment() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean commentRsp = false;

				try {

					String jsonstr = MsgCenterAPI.sendTopicComment(topicId,
							"游客", comment);

					JSONObject jsonRoot = new JSONObject(jsonstr);
					if (jsonRoot.has("ResponseMsg")) {
						JSONObject jsonObject = jsonRoot
								.getJSONObject("ResponseMsg");
						String code = jsonObject.getString("code");
						if (!TextUtils.isEmpty(code) && code.equals("0")) {
							commentRsp = true;
						}
					}
				}

				catch (JSONException e) {
					e.printStackTrace();
				}

				if (commentRsp) {
					handler.sendEmptyMessage(COMMENT_SUCCESS);
				} else {
					handler.sendEmptyMessage(COMMENT_ERROR);
				}

			}
		}).start();
	}

	public void getCommentCount() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean isgetcomment = false;
				try {
					String jsonstr = MsgCenterAPI.getCommentTotal(topicId);
					if (!TextUtils.isEmpty(jsonstr)) {

						JSONObject jsonObj = new JSONObject(jsonstr);
						JSONObject jsonObject = jsonObj
								.getJSONObject("ResponseMsg");
						count = jsonObject.getString("msg");

						if (!TextUtils.isEmpty(count))
							isgetcomment = true;

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (isgetcomment) {
					handler.sendEmptyMessage(GET_COMMENT_COUNT_SUCCESS);
				} else {
					handler.sendEmptyMessage(GET_COMMENT_COUNT_ERROR);
				}
			}
		}).start();

	}
}
