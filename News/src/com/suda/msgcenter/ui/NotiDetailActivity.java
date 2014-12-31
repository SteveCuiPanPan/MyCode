package com.suda.msgcenter.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.suda.msgcenter.R;
import com.suda.msgcenter.api.MsgCenterAPI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class NotiDetailActivity extends Activity {
	
	private static final int GET_NOTIS_CONTENT_SUCCESS = 0;
	private static final int GET_NOTIS_CONTENT_ERROR = 1;
	private TextView tvtitle,tvtime,tvcontent;
	private Button btn_back, btn_share;

    private	String content,topicId;
    
    private Handler handler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case GET_NOTIS_CONTENT_SUCCESS:
				Spanned htmlSpan = Html.fromHtml(content);
				tvcontent.setText(htmlSpan);
				tvcontent.setMovementMethod(LinkMovementMethod.getInstance());
				break;

			}
    	};
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noti_details);
		btn_back = (Button) findViewById(R.id.button_back);
		btn_share = (Button) findViewById(R.id.button_share);
		btn_back.setOnClickListener(listener);
		btn_share.setOnClickListener(listener);
		tvtitle = (TextView)findViewById(R.id.tv_noti_title);
		tvcontent = (TextView)findViewById(R.id.tv_noti_detailweb);
		tvtime = (TextView)findViewById(R.id.tv_noti_time);
		tvtitle.setText(getIntent().getStringExtra("notititle"));
		tvtime.setText(getIntent().getStringExtra("notitime"));
		topicId = getIntent().getStringExtra("topicId");
		
		getNotisContent();
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
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		  MenuInflater inflater = getMenuInflater();  
		    inflater.inflate(R.menu.main, menu);  
		return super.onCreateOptionsMenu(menu);
	}

	public void getNotisContent() {
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
					handler.sendEmptyMessage(GET_NOTIS_CONTENT_SUCCESS);
				else
					handler.sendEmptyMessage(GET_NOTIS_CONTENT_ERROR);

			}
		}).start();
	}
}
