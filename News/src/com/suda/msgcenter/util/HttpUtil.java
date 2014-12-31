package com.suda.msgcenter.util;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.suda.msgcenter.main.SudaConstants;



public class HttpUtil {

	public static String doHttpGet(String url){
		String strResponse = null;
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
		HttpConnectionParams.setSoTimeout(httpParameters, 5 * 1000);
		DefaultHttpClient client = new DefaultHttpClient(httpParameters);
		HttpGet get = new HttpGet(url);
		try{
			HttpResponse httpResponse = client.execute(get);
			if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				strResponse = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
			}
		}catch(IOException e){
			
		}catch(ParseException e){
			
		}
		return strResponse;
	}
	
	public static String doHttpPost(String url,List<NameValuePair> params){
		if(SudaConstants.DEBUG_MODE){
			StringBuffer sb = new StringBuffer();
			sb.append(url);
			if(params!=null){
				sb.append("?");
				for(NameValuePair np:params){
					sb.append(np.getName());
					sb.append("=");
					sb.append(np.getValue());
					sb.append("&");
				}
				sb.deleteCharAt(sb.length()-1);
			}
			LogUtil.d(sb.toString());
		}
		String response = null;
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
		HttpConnectionParams.setSoTimeout(httpParameters, 5 * 1000);
		DefaultHttpClient client = new DefaultHttpClient(httpParameters);
		HttpPost httpPost = new HttpPost(url);
		try {
			if(params!=null){
				httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			}
			HttpResponse httpResponse = client.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				response = EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
				
			}
		}catch (UnsupportedEncodingException e) {
		
		}catch(IOException e){
			
		}catch(ParseException e){
		
		}
		return response;
	}
	
	public static HttpGet getHttpGet(String url, List<NameValuePair> params) {

		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (params != null) {
			list.addAll(params);
		}		
		HttpGet httpGet = null;
		httpGet = new HttpGet(url + "&" + URLEncodedUtils.format(list, HTTP.UTF_8));
		httpGet.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 3000);
		httpGet.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		return httpGet;
	}
	
	public static String doHttpGet(HttpGet httpGet) {
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			HttpConnectionParams.setSoTimeout(httpParameters, 5 * 1000);
			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			HttpResponse httpResponse = client.execute(httpGet);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String response = EntityUtils.toString(httpResponse.getEntity());
				return response;
			} else {
				httpGet.abort();
			}
		} catch (Exception e) {
			httpGet.abort();
		}
		return "";
	}
}
