package com.suda.msgcenter.api;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.suda.msgcenter.bean.ImageNewsBean;
import com.suda.msgcenter.bean.NewsBean;



public class Json_Factory {

	public static ArrayList<NewsBean> getNewsResultFromContent(String jsonstr,ArrayList<NewsBean> data) throws Exception{
		if (!TextUtils.isEmpty(jsonstr)) {
			JSONObject jsonObj;
			try {
				jsonObj = new JSONObject(jsonstr);
				JSONArray jsonArr = jsonObj.getJSONArray("data");
				//List<NewsBean> Beans = new ArrayList<NewsBean>();
				for (int i = 0; i < jsonArr.length(); i++) {
					JSONObject jsonobj = jsonArr.getJSONObject(i);
					NewsBean bean = new NewsBean();
					bean.setId(jsonobj.getString("id"));
					bean.setTitle(jsonobj.getString("title"));
					bean.setContent(jsonobj.getString("content"));
					bean.setSenderId(jsonobj.getString("senderId"));
					bean.setType(jsonobj.getString("type"));
					bean.setImgUrl(jsonobj.getString("imgUrl"));
					bean.setFileUrl(jsonobj.getString("fileUrl"));
					bean.setSendTime(jsonobj.getString("sendTime"));
					bean.setTag(jsonobj.getString("tag"));
					data.add(bean);
				}
				return data;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		return null;
	}
	public static ArrayList<ImageNewsBean> getImgNewsResultFromContent(String jsonstr,ArrayList<ImageNewsBean> data) throws Exception{
		if (!TextUtils.isEmpty(jsonstr)) {
			JSONObject jsonObj;
			try {
				jsonObj = new JSONObject(jsonstr);
				JSONArray jsonArr = jsonObj.getJSONArray("data");
			
				for (int i = 0; i < jsonArr.length(); i++) {
					JSONObject jsonobj = jsonArr.getJSONObject(i);
					ImageNewsBean bean = new ImageNewsBean();
					bean.setId(jsonobj.getString("id"));
					bean.setTitle(jsonobj.getString("title"));
					bean.setContent(jsonobj.getString("content"));
					bean.setSenderId(jsonobj.getString("senderId"));
					bean.setType(jsonobj.getString("type"));
					bean.setImgUrl(jsonobj.getString("imgUrl"));
					bean.setFileUrl(jsonobj.getString("fileUrl"));
					bean.setSendTime(jsonobj.getString("sendTime"));
					bean.setTag(jsonobj.getString("tag"));
					data.add(bean);
				}
				return data;
			} catch (JSONException e) {
			
				e.printStackTrace();
			}	
		}
		return null;
	}
}
