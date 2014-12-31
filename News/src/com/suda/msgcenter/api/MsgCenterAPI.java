package com.suda.msgcenter.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;

import com.suda.msgcenter.util.HttpUtil;

public class MsgCenterAPI {
	private static final String URL_NEWS_LIST = "http://10.10.65.199:8080/mialab-palmsuda/palmsuda?cmd=suda.news.list";
	private static final String URL_COMMENT_LIST = "http://10.10.65.199:8080/mialab-palmsuda/palmsuda?cmd=suda.news.comment.list";
	private static final String URL_COMMENT_ADD = "http://10.10.65.199:8080/mialab-palmsuda/palmsuda?cmd=suda.news.comment.add";
	private static final String URL_COMMENT_SUPPORT = "http://10.10.65.199:8080/mialab-palmsuda/palmsuda?cmd=suda.news.comment.edit";
	private static final String URL_NEWS_CONTENT = "http://10.10.65.199:8080/mialab-palmsuda/palmsuda?cmd=suda.news.detail";
	private static final String URL_COMMENT_TOTAL = "http://10.10.65.199:8080/mialab-palmsuda/palmsuda?cmd=suda.news.comment.count";

	public static String getImageNews() {
		String response = HttpUtil.doHttpPost(URL_NEWS_LIST
				+ "&type=2&pageIndex=0&pageSize=10", null);
		return response;
	}

	public static String getNews(int pageNo) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("pageIndex", pageNo + ""));
		params.add(new BasicNameValuePair("pageSize", 10 + ""));
		String response = HttpUtil
				.doHttpPost(URL_NEWS_LIST + "&type=0", params);
		
		return response;
	}

	public static String getNewsContent(String topicId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", topicId));
		String response = HttpUtil.doHttpPost(URL_NEWS_CONTENT, params);
		return response;
	}

	public static String getNoti(int pageNo) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("pageIndex", pageNo + ""));
		params.add(new BasicNameValuePair("pageSize", 10 + ""));
		String response = HttpUtil
				.doHttpPost(URL_NEWS_LIST + "&type=1", params);
		return response;
	}

	public static String getComment(int pageNo, String topicId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("topicId", topicId));
		params.add(new BasicNameValuePair("pageNo", pageNo + ""));
		params.add(new BasicNameValuePair("pageSize", 30 + ""));
		String response = HttpUtil.doHttpPost(URL_COMMENT_LIST, params);
		return response;
	}

	public static String sendTopicComment(String topicId, String reviewer,
			String content) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("topicId", topicId));
		params.add(new BasicNameValuePair("reviewer", reviewer));
		params.add(new BasicNameValuePair("content", content));
		// String response = HttpUtil.doHttpPost(URL_COMMENT_ADD, params);
		HttpGet httpGet = HttpUtil.getHttpGet(URL_COMMENT_ADD, params);
		String response = HttpUtil.doHttpGet(httpGet);
		// System.out.println("--rep-->"+response);
		return response;
	}

	public static String sendSupportCount(String commmentId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", commmentId));
		String response = HttpUtil.doHttpPost(URL_COMMENT_SUPPORT, params);

		// System.out.println("--rep-->"+response);
		return response;
	}

	public static String getCommentTotal(String topicId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("topicid", topicId));
		String response = HttpUtil.doHttpPost(URL_COMMENT_TOTAL, params);

		// System.out.println("--rep-->"+response);
		return response;
	}
}
