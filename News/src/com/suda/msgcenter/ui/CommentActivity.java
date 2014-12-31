package com.suda.msgcenter.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.suda.msgcenter.R;
import com.suda.msgcenter.api.MsgCenterAPI;
import com.suda.msgcenter.bean.CommentBean;
import com.suda.msgcenter.main.SudaApplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CommentActivity extends Activity {

	private static final int COMMENT_SUCCESS = 1;
	private static final int COMMENT_ERROR = 0;
	private static final int GET_COMMENT_SUCCESS = 2;
	private static final int GET_COMMENT_ERROR = 3;
	private static final int GET_FIRST_COMMENT_SUCCESS = 4;
	private static final int GET_FIRST_COMMENT_ERROR = 5;
	private static final int GET_COMMENT_EMPTY = 6;
	private static final int SEND_SUPPORT_SUCCESS = 7;

	private boolean firstload = true;
	private SudaApplication app;

	private ImageView iv_loadingfail, iv_comment_empty;
	private LinearLayout ll_comment_loading;
	private LinearLayout ll_comment_loading_icon;
	private RelativeLayout rl_comment;

	private CommentAdapter adapter;
	private ListView lv_comment;
	private String comment, commentId;
	private Button btn_back;
	private List<CommentBean> commentBeans;
	private TextView tv_send;
	private EditText et_comment;
	private LinearLayout ll_comment;
	private String topicId;
	private int pageNo = 1;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_COMMENT_EMPTY:

				if (ll_comment_loading.getVisibility() != View.GONE) {
					ll_comment_loading.setVisibility(View.GONE);
				}
				if (rl_comment.getVisibility() != View.VISIBLE) {
					rl_comment.setVisibility(View.VISIBLE);
				}
				if (iv_comment_empty.getVisibility() == View.GONE) {
					iv_comment_empty.setVisibility(View.VISIBLE);
				}
				firstload = false;
				break;
			case COMMENT_SUCCESS:

				Toast.makeText(CommentActivity.this, "评论成功", 0).show();
				break;

			case COMMENT_ERROR:

				Toast.makeText(CommentActivity.this, "评论失败", 0).show();
				break;
			case GET_FIRST_COMMENT_SUCCESS:
				if (ll_comment_loading.getVisibility() != View.GONE) {
					ll_comment_loading.setVisibility(View.GONE);
				}
				if (rl_comment.getVisibility() != View.VISIBLE) {
					rl_comment.setVisibility(View.VISIBLE);
				}
				firstload = false;
				adapter.setData(commentBeans);

				break;

			case GET_FIRST_COMMENT_ERROR:
				ll_comment_loading_icon.setVisibility(View.GONE);
				if (iv_loadingfail.getVisibility() != View.VISIBLE) {
					iv_loadingfail.setVisibility(View.VISIBLE);
				}
				firstload = true;
				// Toast.makeText(CommentActivity.this, "评论失败", 0).show();
				break;

			case GET_COMMENT_SUCCESS:
				if (iv_comment_empty.getVisibility() != View.GONE) {
					iv_comment_empty.setVisibility(View.GONE);
				}
				adapter.setData(commentBeans);
				break;

			case GET_COMMENT_ERROR:

				break;
			case SEND_SUPPORT_SUCCESS:
				app.setSaveBoolean(commentId, true);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_comment);
		tv_send = (TextView) findViewById(R.id.tv_send);
		btn_back = (Button) findViewById(R.id.button_back);
		lv_comment = (ListView) findViewById(R.id.lv_news_comment);
		ll_comment = (LinearLayout) findViewById(R.id.ll_comment);
		ll_comment_loading = (LinearLayout) findViewById(R.id.ll_comment_loading);
		ll_comment_loading_icon = (LinearLayout) findViewById(R.id.ll_comment_loading_icon);
		rl_comment = (RelativeLayout) findViewById(R.id.rl_comment);
		iv_loadingfail = (ImageView) findViewById(R.id.iv_loadingfail);
		iv_comment_empty = (ImageView) findViewById(R.id.iv_comment_empty);
		et_comment = (EditText) findViewById(R.id.et_comment);
		app = SudaApplication.getInstance();
		adapter = new CommentAdapter();

		// commentBeans = (List<CommentBean>)
		// getIntent().getSerializableExtra("list");
		topicId = getIntent().getStringExtra("topicid");
		lv_comment.setAdapter(adapter);
		// adapter.setData(commentBeans);
		//
		btn_back.setOnClickListener(listener);
		tv_send.setOnClickListener(listener);
		ll_comment.setOnClickListener(listener);
		iv_loadingfail.setOnClickListener(listener);
		getComment();
		lv_comment.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				CommentBean bean = commentBeans.get(arg2);
				commentId = bean.getId();

			}
		});
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_back:
				finish();
				break;

			case R.id.iv_loadingfail:

				iv_loadingfail.setVisibility(View.GONE);
				ll_comment_loading_icon.setVisibility(View.VISIBLE);
				getComment();
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
					break;
				}

			case R.id.tv_send:

				comment = et_comment.getText().toString();
				if (TextUtils.isEmpty(comment)) {
					Toast.makeText(CommentActivity.this, "请输入评论内容", 0).show();
				} else {
					sendTopicComment();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					if (ll_comment.getVisibility() == View.GONE) {
						ll_comment.setVisibility(View.VISIBLE);
						et_comment.setVisibility(View.GONE);
					}
					getComment();
				}
				break;
			}
		}
	};

	private class CommentAdapter extends BaseAdapter {

		private List<CommentBean> list;

		public void setData(List<CommentBean> list) {
			this.list = list;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list == null ? 0 : list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final ViewHolder viewholder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.comment_item, null);
				
				viewholder = new ViewHolder();
				viewholder.content = (TextView) convertView
						.findViewById(R.id.tv_comment);
				viewholder.support = (TextView) convertView
						.findViewById(R.id.tv_support_count);
				viewholder.time = (TextView) convertView
						.findViewById(R.id.tv_time);
				
				convertView.setTag(viewholder);
			} else {
				viewholder = (ViewHolder) convertView.getTag();
			}
			LinearLayout ll = (LinearLayout)convertView.findViewById(R.id.ll_support);
			final ImageView iv_support = new ImageView(CommentActivity.this);
			iv_support.setScaleType(ScaleType.FIT_XY);
			iv_support.setImageResource(R.drawable.comment_support);
			ll.removeAllViews();
			ll.addView(iv_support, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			
			CommentBean bean = list.get(position);
			viewholder.content.setText(bean.getContent());
			viewholder.support.setText(bean.getSupport());
			viewholder.time.setText(bean.getCreateTime());
			final int count = Integer.parseInt(bean.getSupport());
			iv_support.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Drawable drawable = iv_support.getResources()
							.getDrawable(R.drawable.comment_support_done);
					// viewholder.iv_support.setImageResource(R.drawable.comment_support_done);
					iv_support.setImageDrawable(drawable);
					viewholder.support.setText((count + 1) + "");
					iv_support.setClickable(false);
					Boolean isSupport = app.getSaveBoolean(commentId);
					if (!isSupport)
						sendSupportCount();

				}
			});
			return convertView;
		}

	}

	static class ViewHolder {

		TextView content;
		TextView support;
		TextView time;

	}

	public void sendSupportCount() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean sendsupportRsp = false;

				try {
					String jsonstr = MsgCenterAPI.sendSupportCount(commentId);
					JSONObject jsonRoot = new JSONObject(jsonstr);
					if (jsonRoot.has("ResponseMsg")) {
						JSONObject jsonObject = jsonRoot
								.getJSONObject("ResponseMsg");
						String code = jsonObject.getString("code");
						if (!TextUtils.isEmpty(code) && code.equals("0")) {
							sendsupportRsp = true;
						}
					}
				}

				catch (JSONException e) {
					e.printStackTrace();
				}

				if (sendsupportRsp) {
					handler.sendEmptyMessage(SEND_SUPPORT_SUCCESS);
				} else {
					// handler.sendEmptyMessage(COMMENT_ERROR);
				}

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

	public void getComment() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean isgetcomment = false;
				boolean iscommentempty = false;

				try {
					String jsonstr = MsgCenterAPI.getComment(pageNo, topicId);
					if (!TextUtils.isEmpty(jsonstr)) {

						JSONObject jsonObj = new JSONObject(jsonstr);
						JSONArray jsonArr = jsonObj.getJSONArray("data");
						commentBeans = new ArrayList<CommentBean>();
						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonobj = jsonArr.getJSONObject(i);
							CommentBean bean = new CommentBean();
							bean.setId(jsonobj.getString("id"));
							bean.setTopicId(jsonobj.getString("topicid"));
							bean.setContent(jsonobj.getString("content"));
							bean.setReviewer(jsonobj.getString("reviewer"));
							bean.setCreateTime(jsonobj.getString("createTime"));
							bean.setSupport(jsonobj.getString("support"));
							bean.setStatus(jsonobj.getString("status"));
							commentBeans.add(bean);

						}

						if (commentBeans.size() > 0) {
							isgetcomment = true;

						}
						if (commentBeans.size() == 0) {
							isgetcomment = true;
							iscommentempty = true;
						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (firstload) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (isgetcomment && firstload && !iscommentempty) {
					handler.sendEmptyMessage(GET_FIRST_COMMENT_SUCCESS);
				} else if (isgetcomment && firstload && iscommentempty) {
					handler.sendEmptyMessage(GET_COMMENT_EMPTY);
				} else if (!isgetcomment && firstload) {

					handler.sendEmptyMessage(GET_FIRST_COMMENT_ERROR);
				} else if (isgetcomment && !firstload) {
					handler.sendEmptyMessage(GET_COMMENT_SUCCESS);
				} else if (!isgetcomment && !firstload) {
					handler.sendEmptyMessage(GET_COMMENT_ERROR);
				}
			}
		}).start();

	}

}
