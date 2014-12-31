package com.suda.msgcenter.ui;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suda.msgcenter.R;
import com.suda.msgcenter.bean.NotiBean;
import com.suda.msgcenter.util.DateFormat;

public class NotiListAdapter extends BaseAdapter {

	private List<NotiBean> beans;

	public void setData(List<NotiBean> beans) {
		this.beans = beans;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return beans == null ? 0 : beans.size();
	}

	@Override
	public Object getItem(int position) {
		return beans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.noti_listitem, null);
			viewholder = new ViewHolder();
			viewholder.title = (TextView) convertView
					.findViewById(R.id.tv_title);
			viewholder.content = (TextView) convertView
					.findViewById(R.id.tv_content);
			viewholder.time = (TextView) convertView.findViewById(R.id.tv_time);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		NotiBean bean = beans.get(position);
		viewholder.title.setText(bean.getTitle());
		// String content = bean.getContent();
		// String content1 = content.substring(5);
		// viewholder.content.setText(content1);
		viewholder.time.setText(DateFormat.DateToString(bean.getSendTime()));
		

		return convertView;
	}

	static class ViewHolder {
		TextView title;
		TextView content;
		TextView time;
	}

}
