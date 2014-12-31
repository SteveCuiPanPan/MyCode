package com.suda.msgcenter.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.suda.msgcenter.bean.NewsBean;
import com.suda.msgcenter.bean.NotiBean;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

public class InfoDataModel {
	private InfoDataCompleteListener listener;

	private final static int NEWS_ERROR = -1;
	private final static int NOTI_ERROR = -2;
	private final static int LOGIN_ERROR = -3;
	private final static int ALL_SUCCESS = 0;
	private final static int NEWS_SUCCESS = 1;
	private final static int NOTI_SUCCESS = 2;
	private final static int LOGIN_SUCCESS = 3;

	private List<NewsBean> newsBeans;
	private List<NotiBean> notiBeans;


	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case NEWS_SUCCESS:
				listener.OnNewsDataComplete(ALL_SUCCESS, newsBeans);
				break;
			case NEWS_ERROR:
				listener.OnNewsDataComplete(NEWS_ERROR, null);
				break;
			case NOTI_SUCCESS:
				listener.OnNotiDataComplete(ALL_SUCCESS,notiBeans);
				break;
			case NOTI_ERROR:
				listener.OnNotiDataComplete(NOTI_ERROR, null);
				break;

			}
		};
	};

	public InfoDataModel(InfoDataCompleteListener listener) {
		this.listener = listener;
	}
/**
 * 
 */
//	public void getNews() {
//		
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				boolean isgetnews = false;
//				try {
//					String jsonstr = InfoPlatformAPI.getNews();
//					if (!TextUtils.isEmpty(jsonstr)) {
//						JSONArray jsonArr = new JSONArray(jsonstr);
//						newsBeans = new ArrayList<NewsBean>();
//						for (int i = 0; i < jsonArr.length(); i++) {
//							JSONObject jsonobj = jsonArr.getJSONObject(i);
//							NewsBean bean = new NewsBean();
//							bean.setId(jsonobj.getString("id"));
//							bean.setTitle(jsonobj.getString("title"));
//							bean.setContent(jsonobj.getString("content"));
//							bean.setPublisherName(jsonobj
//									.getString("publisherName"));
//							bean.setType(jsonobj.getString("type"));
//							bean.setImgUrl(jsonobj.getString("imgUrl"));
//							bean.setFileUrl(jsonobj.getString("fileUrl"));
//							bean.setAlterTime(jsonobj.getString("alterTime"));
//							bean.setCreateTime(jsonobj.getString("createTime"));
//							bean.setTag(jsonobj.getString("tag"));
//							newsBeans.add(bean);
//						}
//						if (newsBeans.size() > 0)
//							isgetnews = true;
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				if (isgetnews)
//					handler.sendEmptyMessage(NEWS_SUCCESS);
//				else
//					handler.sendEmptyMessage(NEWS_ERROR);
//			}
//		}).start();
//	}

	/**
	 * 
	 * @param username
	 *//*
	public void getNoti(final int pageNo) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean isgetnoti = false;
				try {
					String jsonstr = InfoPlatformAPI.getNoti(pageNo);
					if (!TextUtils.isEmpty(jsonstr)) {
						JSONObject jsonObj = new JSONObject(jsonstr);
						JSONArray jsonArr = jsonObj.getJSONArray("rows");
						notiBeans = new ArrayList<NotiBean>();
						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonobj = jsonArr.getJSONObject(i);
							NotiBean bean = new NotiBean();
							bean.setId(jsonobj.getString("id"));
							bean.setContent(jsonobj.getString("content"));
							bean.setSenderId(jsonobj.getString("senderId"));
							bean.setSenderName(jsonobj.getString("senderName"));
							bean.setSendTime(jsonobj.getString("sendTime"));
							bean.setTag(jsonobj.getString("tag"));
							notiBeans.add(bean);
						}
						if (notiBeans.size() > 0)
							isgetnoti = true;
						else
							isgetnoti = false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (isgetnoti)
					handler.sendEmptyMessage(NOTI_SUCCESS);
				else
					handler.sendEmptyMessage(NOTI_ERROR);
			}
		}).start();
	}*/

	

	public interface InfoDataCompleteListener {
		public void OnNewsDataComplete(int flag, List<NewsBean> beans);

		public void OnNotiDataComplete(int flag, List<NotiBean> beans);

	}
}
