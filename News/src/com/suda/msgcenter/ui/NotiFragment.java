package com.suda.msgcenter.ui;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.suda.msgcenter.R;
import com.suda.msgcenter.api.MsgCenterAPI;
import com.suda.msgcenter.bean.NewsBean;
import com.suda.msgcenter.bean.NotiBean;
import com.suda.msgcenter.ui.CustomListView.OnLoadMoreListener;
import com.suda.msgcenter.ui.CustomListView.OnRefreshListener;
import com.suda.msgcenter.util.DateFormat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NotiFragment extends BaseFragment {

	private final static int NOTI_ERROR = -1;
	private final static int NOTI_SUCCESS = 1;
	public final static int REFRESH_SUCCESS = 2;

	private boolean hasNoti = false;
	private boolean loginChecked = false;
	private boolean firstnotiload = true;
	private boolean isRefreshing = false;

	private NotiFragmentListener listener;
	private LinearLayout ll_noti_loading;
	private LinearLayout ll_noti_loading_icon;
	private CustomListView notilist;
	// private FrameLayout layout_content;
	private ImageView iv_loadingfail;
	private NotiListAdapter adapter;
	private ArrayList<NotiBean> notiBeans;
	private List<String> taglist = new ArrayList<String>();
	private int pageNo = 1;
	private List<NotiBean> total = new ArrayList<NotiBean>();

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case NOTI_SUCCESS:
				if (ll_noti_loading.getVisibility() != View.GONE) {
					ll_noti_loading.setVisibility(View.GONE);
				}
				if (notilist.getVisibility() != View.VISIBLE) {
					notilist.setVisibility(View.VISIBLE);
				}
				firstnotiload = false;
				total.addAll(notiBeans);

				setNotiData(total);
				if (pageNo == 1) {
					notilist.setAdapter(adapter);
				}
				adapter.notifyDataSetChanged();
				if (notiBeans.size() < 10) {

					notilist.setCanLoadMore(false);
					notilist.setDiv(false);

				} else {
					pageNo++;
				}
				//System.out.println("--first:pageNo-->" + pageNo);
				notilist.onLoadMoreComplete();
				break;
			case NOTI_ERROR:
				ll_noti_loading_icon.setVisibility(View.GONE);
				if (iv_loadingfail.getVisibility() != View.VISIBLE) {
					iv_loadingfail.setVisibility(View.VISIBLE);
				}
				break;

			case REFRESH_SUCCESS:
				total.removeAll(total);
				total.addAll(notiBeans);
				setNotiData(total);
				if (pageNo == 1) {
					notilist.setAdapter(adapter);
				}
				adapter.notifyDataSetChanged();
				if (notiBeans.size() < 10) {
					// System.out.println("--newsBeans.size()-->"+
					// notiBeans.size());
					notilist.setCanLoadMore(false);
					notilist.setDiv(false);

				} else {
					if (notilist.getCanLoadMore() == false)
						notilist.setCanLoadMore(true);
					pageNo++;
				}
				notilist.onRefreshComplete();
				isRefreshing = false;
				break;
			}
		};
	};

	public void setNotiData(List<NotiBean> beans) {
		if (adapter != null) {
			adapter.setData(beans);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.noti_fragment, null);
		ll_noti_loading = (LinearLayout) view
				.findViewById(R.id.ll_noti_loading);
		ll_noti_loading_icon = (LinearLayout) view
				.findViewById(R.id.ll_noti_loading_icon);

		iv_loadingfail = (ImageView) view.findViewById(R.id.iv_loadingfail);
		notilist = (CustomListView) view.findViewById(R.id.lv_notilist);
		adapter = new NotiListAdapter();
		notilist.setAdapter(adapter);
		notilist.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pageNo = 1;
				isRefreshing = true;
				getNoti();
			}
		}, 1);

		notilist.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				getNoti();
			}
		});

		notilist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				NotiBean bean = (NotiBean) adapter.getItem(position-1);
				System.out.println("noti"+position);
				Intent intent = new Intent(getActivity(),
						NotiDetailActivity.class);
				intent.putExtra("notititle", bean.getTitle());
				intent.putExtra("notitime", DateFormat.DateToString(bean.getSendTime()));
				intent.putExtra("topicId", bean.getId());
				startActivity(intent);
			}
		});
		iv_loadingfail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_loadingfail.setVisibility(View.GONE);
				ll_noti_loading_icon.setVisibility(View.VISIBLE);
				getNoti();
			}
		});

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			// fragment可见时加载数据
			if (firstnotiload) {
				getNoti();
			}
		} else {
			// fragment不可见时不执行操作
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	public void getNoti() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean isgetnoti = false;
				try {
					String jsonstr = MsgCenterAPI.getNoti(pageNo-1);
		//			System.out.println(jsonstr);
					if (!TextUtils.isEmpty(jsonstr)) {

						//JSONArray jsonArr = new JSONArray(jsonstr);
						JSONObject jsonObj = new JSONObject(jsonstr);
						JSONArray jsonArr = jsonObj.getJSONArray("data");
						notiBeans = new ArrayList<NotiBean>();
						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonobj = jsonArr.getJSONObject(i);
							NotiBean bean = new NotiBean();
							bean.setId(jsonobj.getString("id"));
							bean.setTitle(jsonobj.getString("title"));
							bean.setContent(jsonobj.getString("content"));
							bean.setSenderId(jsonobj.getString("senderId"));
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

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (isgetnoti)
					if (isRefreshing) {
						handler.sendEmptyMessage(REFRESH_SUCCESS);
					} else {
						handler.sendEmptyMessage(NOTI_SUCCESS);
					}
				else
					handler.sendEmptyMessage(NOTI_ERROR);
			}
		}).start();
	}

	public interface NotiFragmentListener {
		public void onLogin(String username, String password);

		public void onLogout();
	}

}
