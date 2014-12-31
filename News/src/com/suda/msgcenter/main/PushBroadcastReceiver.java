package com.suda.msgcenter.main;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.suda.msgcenter.util.LogUtil;

public class PushBroadcastReceiver extends FrontiaPushMessageReceiver {

	@Override
	public void onBind(Context context, int errorCode, String appid,
			String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid="
				+ appid + " userId=" + userId + " channelId=" + channelId
				+ " requestId=" + requestId;
		LogUtil.i(responseString);
		Log.i("TAG", responseString);
		//保存下userId和channelId
		if (errorCode == 0) {
			SudaApplication.getInstance().setSaveString("userId", userId);
			SudaApplication.getInstance().setSaveString("channelId", channelId);
		}
	}

	@Override
	public void onMessage(Context context, String message,
			String customContentString) {
		String messageString = "onMessage() message=\"" + message
				+ "\" customContentString=" + customContentString;
		LogUtil.i(messageString);
		Log.i("TAG", messageString);
	}

	@Override
	public void onNotificationClicked(Context context, String title,
			String description, String customContentString) {
		String notifyString = "onNotificationClicked() title=\"" + title
				+ "\" description=\"" + description + "\" customContent="
				+ customContentString;
		LogUtil.i(notifyString);
		Log.i("TAG", notifyString);

		Intent intent = new Intent();
		intent.setAction(SudaConstants.ACTION_NOTI_CLICK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	@Override
	public void onSetTags(Context context, int errorCode,
			List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onSetTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		LogUtil.i(responseString);
		Log.i("TAG", responseString);
		//TODO
		/*if(errorCode==0&&failTags.isEmpty()){
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<sucessTags.size();i++){
				sb.append(sucessTags.get(i)+",");
			}
			sb.deleteCharAt(sb.length()-1);
			SudaApplication.getInstance().setSaveString("PUSH_TAG", sb.toString());
			SudaApplication.getInstance().setSaveBoolean("TAG_SETTED", true);
		}else{
			SudaApplication.getInstance().setSaveBoolean("TAG_SETTED", false);
		}*/
	}

	@Override
	public void onDelTags(Context context, int errorCode,
			List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onDelTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		LogUtil.i(responseString);
		Log.i("TAG", responseString);
	}

	@Override
	public void onListTags(Context context, int errorCode, List<String> tags,
			String requestId) {
		String responseString = "onListTags errorCode=" + errorCode + " tags="
				+ tags;
		LogUtil.i(responseString);
		Log.i("TAG", responseString);
	}

	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		String responseString = "onUnbind errorCode=" + errorCode
				+ " requestId = " + requestId;
		LogUtil.i(responseString);
		Log.i("TAG", responseString);
	}

}

/*注意如果试图从非activity的非正常途径启动一个activity（例见下文“intent.setFlags()方法中参数的用例”），
 * 比如从一个service中启动一个activity，则intent比如要添加FLAG_ACTIVITY_NEW_TASK标记
 * （编者按：activity要存在于activity的栈中，而非activity的途径启动activity时必然不存在一个activity的栈，
 * 所以要新起一个栈装入启动的activity）。简而言之，跳转到的activity根据情况，可能压在一个新建的栈中。*/
 