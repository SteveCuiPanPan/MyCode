package com.suda.msgcenter.ui;

import java.util.ArrayList;
import java.util.Vector;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.suda.msgcenter.R;
import com.suda.msgcenter.api.InfoDataModel;
import com.suda.msgcenter.api.Json_Factory;
import com.suda.msgcenter.api.MsgCenterAPI;
import com.suda.msgcenter.bean.ImageNewsBean;
import com.suda.msgcenter.bean.NewsBean;
import com.suda.msgcenter.main.SudaApplication;
import com.suda.msgcenter.main.SudaConstants;
import com.suda.msgcenter.ui.CustomListView.OnLoadMoreListener;
import com.suda.msgcenter.ui.CustomListView.OnRefreshListener;
import com.suda.msgcenter.util.DateFormat;
import com.suda.msgcenter.util.ImageCache;
import com.suda.msgcenter.util.ImageWorker;
import com.suda.msgcenter.util.LogUtil;

import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class NewsFragment extends BaseFragment {

	public final static int NEWS_SUCCESS = 0;
	public final static int NEWS_ERROR = -1;
	public final static int SET_NEWSLIST = 1;
	public final static int REFRESH_SUCCESS = 2;
	public final static int LOADMORE_SUCCESS = 3;
	public static final int IMAGE_NEWS_SUCCESS = 4;

	private View view2;
	private int frameheight;// 图片的高度
	private FrameLayout fl;
	private ViewPager viewPager; // android-support-v4中的滑动组件
	private List<ImageView> imageViews; // 滑动的图片集合
	private String[] titles; // 图片标题
	private String[] imageUrls; // 图片ID
	private List<View> dots; // 图片标题正文的那些点
	private int window_width;
	private int window_height;
	private TextView tv_title;
	private int currentItem = 0; // 当前图片的索引号
	private ImageWorker imgeWorker;

	// 切换当前显示的图片
	private Handler handler1 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
		};
	};

	private int pageNo = 1;
	List<NewsBean> total = new ArrayList<NewsBean>(); // 放置记录的总条数

	private CustomListView newslist;
	private NewsAdapter adapter;
	private ImageNewsAdapter imageNewsAdapter;
	private Activity activity;
	private boolean firstload = true;
	private boolean isRefreshing = false;
	private boolean hasImageNews = true;

	private ImageView iv_loadingfail;
	private LinearLayout ll_news_loading;
	private LinearLayout ll_news_loading_icon;
	private ArrayList<NewsBean> newsBeans = new ArrayList<NewsBean>();
	private ArrayList<ImageNewsBean> imgnewsBeans = new ArrayList<ImageNewsBean>();
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NEWS_SUCCESS:
				if (ll_news_loading.getVisibility() != View.GONE) {
					ll_news_loading.setVisibility(View.GONE);
				}
				if (newslist.getVisibility() != View.VISIBLE) {
					newslist.setVisibility(View.VISIBLE);
				}

				firstload = false;
				total.addAll(newsBeans);

				setNewsData(total);
				if (pageNo == 1) {
					newslist.setAdapter(adapter);
				}
				adapter.notifyDataSetChanged();
				if (newsBeans.size() < 10) {

					newslist.setCanLoadMore(false);
					newslist.setDiv(false);

				} else {
					pageNo++;
				}

				newslist.onLoadMoreComplete();

				break;
			case NEWS_ERROR:
				ll_news_loading_icon.setVisibility(View.GONE);
				if (iv_loadingfail.getVisibility() != View.VISIBLE) {
					iv_loadingfail.setVisibility(View.VISIBLE);
				}
				break;

			case REFRESH_SUCCESS:
				total.removeAll(total);
				total.addAll(newsBeans);
				setNewsData(total);
				if (pageNo == 1) {
					newslist.setAdapter(adapter);
				}
				adapter.notifyDataSetChanged();
				if (newsBeans.size() < 10) {

					newslist.setCanLoadMore(false);
					newslist.setDiv(false);

				} else {
					if (newslist.getCanLoadMore() == false)
						newslist.setCanLoadMore(true);
					pageNo++;
				}
				newslist.onRefreshComplete();
				isRefreshing = false;
				break;

			case IMAGE_NEWS_SUCCESS:
				imageUrls = new String[imgnewsBeans.size()];
				titles = new String[imgnewsBeans.size()];
				ImageNewsBean bean;
				for (int i = 0; i < imgnewsBeans.size(); i++) {
					bean = imgnewsBeans.get(i);
					titles[i] = bean.getTitle();
					imageUrls[i] = bean.getImgUrl();
				}

				// 初始化图片资源
				imageViews = new ArrayList<ImageView>();
				for (int i = 0; i < 4; i++) {
					ImageView imageView = new ImageView(getActivity());
					if (!TextUtils.isEmpty(imageUrls[i])) {

						imgeWorker.loadBitmap(imageUrls[i], imageView,
								SudaApplication.SCREEN_WEIDTH / 4,
								SudaApplication.SCREEN_WEIDTH / 4);

					} else {
						imageView.setImageResource(R.drawable.article_bigimage);
					}
					imageView.setScaleType(ScaleType.CENTER_CROP);
					DisplayMetrics dm = new DisplayMetrics();
					getActivity().getWindowManager().getDefaultDisplay()
							.getMetrics(dm);
					window_height = dm.heightPixels;// 获取分辨率宽度
					frameheight = window_height / 3;// 获取要显示的高度
					imageViews.add(imageView);
				}

				dots = new ArrayList<View>();
				dots.add(view2.findViewById(R.id.v_dot0));
				dots.add(view2.findViewById(R.id.v_dot1));
				dots.add(view2.findViewById(R.id.v_dot2));
				dots.add(view2.findViewById(R.id.v_dot3));
				tv_title.setText(titles[0]);
				if (hasImageNews) {
					viewPager.setAdapter(imageNewsAdapter);
					LayoutParams layoutParams = (LayoutParams) fl
							.getLayoutParams();
					layoutParams.height = frameheight;
					fl.setLayoutParams(layoutParams);
					newslist.addHeaderView(view2);
					hasImageNews = false;
				} else {
					imageNewsAdapter.notifyDataSetChanged();
				}
				break;

			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.activity = activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement NewsFragmentListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.news_fragment, null);
		view2 = inflater.inflate(R.layout.head_image, null);
		imgeWorker = getImageWorker();
		tv_title = (TextView) view2.findViewById(R.id.tv_title);
		viewPager = (ViewPager) view2.findViewById(R.id.vp);
		// 设置一个监听器，当ViewPager中的页面改变时调用
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		fl = (FrameLayout) view2.findViewById(R.id.fl_main);

		iv_loadingfail = (ImageView) view.findViewById(R.id.iv_loadingfail);

		ll_news_loading = (LinearLayout) view
				.findViewById(R.id.ll_news_loading);
		ll_news_loading_icon = (LinearLayout) view
				.findViewById(R.id.ll_news_loading_icon);
		newslist = (CustomListView) view.findViewById(R.id.lv_newslist);
		imageNewsAdapter = new ImageNewsAdapter();
		adapter = new NewsAdapter();
		newslist.setAdapter(adapter);
		newslist.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pageNo = 1;
				isRefreshing = true;
				getNews();
				getImageNews();
			}
		}, 0);

		newslist.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				getNews();
			}
		});

		newslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println("--position-->" + position);
				NewsBean bean = (NewsBean) adapter.getItem(position - 2);
				Intent intent = new Intent(getActivity(),
						NewsDetailActivity.class);
				System.out.println("--bean.getId()-->" + bean.getId());
				intent.putExtra("topicid", bean.getId());
				intent.putExtra("newstitle", bean.getTitle());
				intent.putExtra("newstime",
						DateFormat.DateToString(bean.getSendTime()));
				intent.putExtra("newscontent", bean.getContent());
				startActivity(intent);
			}
		});

		iv_loadingfail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_loadingfail.setVisibility(View.GONE);
				ll_news_loading_icon.setVisibility(View.VISIBLE);
				getNews();
				getImageNews();
			}
		});

		return view;
	}

	public void setNewsData(List<NewsBean> beans) {
		if (adapter != null)
			adapter.setData(beans);
	}

	public class NewsAdapter extends BaseAdapter {
		private List<NewsBean> beans;

		public void setData(List<NewsBean> beans) {
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
			if (getCount() == 0) {
				return null;
			}

			ViewHolder viewholder;
			String imageUrl;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.news_listitem, null);

				viewholder = new ViewHolder();
				viewholder.title = (TextView) convertView
						.findViewById(R.id.tv_newstitle);
				viewholder.content = (TextView) convertView
						.findViewById(R.id.tv_newscontent);
				viewholder.time = (TextView) convertView
						.findViewById(R.id.tv_time);

				convertView.setTag(viewholder);
			} else {
				viewholder = (ViewHolder) convertView.getTag();
			}

			NewsBean bean = beans.get(position);
			imageUrl = bean.getImgUrl();

			LinearLayout ll = (LinearLayout) convertView
					.findViewById(R.id.ll_news_img);
			ImageView iv = new ImageView(getActivity());
			iv.setScaleType(ScaleType.FIT_XY);
			// 清空ll的里面的view对象
			ll.removeAllViews();

			ll.addView(iv, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));

			if (!TextUtils.isEmpty(imageUrl)) {
				imgeWorker.loadBitmap(imageUrl, iv,
						SudaApplication.SCREEN_WEIDTH / 4,
						SudaApplication.SCREEN_WEIDTH / 4);

			} else {
				iv.setImageResource(R.drawable.list_default_icon);
			}
			viewholder.title.setText(bean.getTitle());
			viewholder.time
					.setText(DateFormat.DateToString(bean.getSendTime()));
			return convertView;
		}
	}

	static class ViewHolder {
		TextView title;
		TextView content;
		TextView time;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			// fragment可见时加载数据
			if (firstload) {
				getNews();
				getImageNews();
			}
		} else {
			// fragment不可见时不执行操作
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	private void getNews() {

		new Thread(new Runnable() {
			boolean isgetfromnet = false;
			boolean isgetfromlocal = false;
			@Override
			public void run() {

				try {

					newsBeans = getNetNews();
					System.out.println("网络数据");
					
					if (newsBeans == null) {
						isgetfromnet = false;
						newsBeans = getSaveNews();
						System.out.println("本地数据");
						if(newsBeans==null){
							isgetfromlocal = false;
						}else{
							isgetfromlocal = true;
						}						
					}else{
						isgetfromnet = true;
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (isgetfromnet||isgetfromlocal) {
					if (isRefreshing) {
						handler.sendEmptyMessage(REFRESH_SUCCESS);
					} else {
						handler.sendEmptyMessage(NEWS_SUCCESS);
					}
				} else if(!isgetfromlocal&&!isgetfromnet){
					handler.sendEmptyMessage(NEWS_ERROR);
				}

			}
		}).start();

		

	}

//////////////////////获取新闻///////////////////////////////////////////////////////
	
	public ArrayList<NewsBean> getNetNews() throws Exception {

		String jsonstr = MsgCenterAPI.getNews(pageNo - 1);
		ArrayList<NewsBean> beans = new ArrayList<NewsBean>();
		beans = Json_Factory.getNewsResultFromContent(jsonstr, beans);
		if (beans != null && beans.size() > 0) {
			saveNews(jsonstr);
		}
		return beans;
	}

	private ArrayList<NewsBean> getSaveNews() throws Exception {
		String jsonstr = getSaveJsonNews();
		System.out.println("json" + jsonstr);
		ArrayList<NewsBean> beans = new ArrayList<NewsBean>();
		beans = Json_Factory.getNewsResultFromContent(jsonstr, beans);
		return beans;
	}

	public String getSaveJsonNews() {
		SharedPreferences settings = getActivity().getSharedPreferences(
				"news_data", 0);
		return settings.getString("news", "");
	}

	private void saveNews(String json) {
		Editor sharedata = getActivity().getSharedPreferences("news_data", 0)
				.edit();
		sharedata.putString("news", json);
		sharedata.commit();
	}
	
////////////////////获取图片新闻////////////////////////////////////////////////////
	
	private void getImageNews() {

		new Thread(new Runnable() {
			boolean isgetfromnet = false;
			boolean isgetfromlocal = false;
			@Override
			public void run() {

				try {

					imgnewsBeans = getNetImgNews();
					System.out.println("网络--数据");
					
					if (imgnewsBeans == null) {
						isgetfromnet = false;
						imgnewsBeans = getSaveImgNews();
						System.out.println("本地--数据");
						if(imgnewsBeans==null){
							isgetfromlocal = false;
						}else{
							isgetfromlocal = true;
						}						
					}else {
						isgetfromnet = true;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}

				
					handler.sendEmptyMessage(IMAGE_NEWS_SUCCESS);
				

			}
		}).start();

	}
	
	public ArrayList<ImageNewsBean> getNetImgNews() throws Exception {

		String jsonstr = MsgCenterAPI.getImageNews();
		ArrayList<ImageNewsBean> beans = new ArrayList<ImageNewsBean>();
		beans = Json_Factory.getImgNewsResultFromContent(jsonstr, beans);
		if (beans != null && beans.size() > 0) {
			saveImgNews(jsonstr);
		}
		return beans;
	}
	
	private ArrayList<ImageNewsBean> getSaveImgNews() throws Exception {
		
		String jsonstr = getSaveJsonImgNews();
		ArrayList<ImageNewsBean> beans = new ArrayList<ImageNewsBean>();
		beans = Json_Factory.getImgNewsResultFromContent(jsonstr, beans);
		return beans;
	}
	
	public String getSaveJsonImgNews() {
		SharedPreferences settings = getActivity().getSharedPreferences(
				"img_news_data", 0);
		return settings.getString("img_news", "");
	}

	private void saveImgNews(String json) {
		Editor sharedata = getActivity().getSharedPreferences("img_news_data", 0)
				.edit();
		sharedata.putString("img_news", json);
		sharedata.commit();
	}
	
/*	public void getImageNews() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean isgetnews = false;
				try {
					String jsonstr = MsgCenterAPI.getImageNews();
					if (!TextUtils.isEmpty(jsonstr)) {
						JSONObject jsonObj = new JSONObject(jsonstr);
						JSONArray jsonArr = jsonObj.getJSONArray("data");
						imgnewsBeans = new ArrayList<ImageNewsBean>();
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
							imgnewsBeans.add(bean);
						}
						if (imgnewsBeans.size() > 0)
							isgetnews = true;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (isgetnews) {
					handler.sendEmptyMessage(IMAGE_NEWS_SUCCESS);
				} else
					handler.sendEmptyMessage(NEWS_ERROR);
			}
		}).start();

	}*/

	/**
	 * 换行切换任务
	 * 
	 * @author Administrator
	 * 
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				// System.out.println("currentItem: " + currentItem);
				currentItem = (currentItem + 1) % imageViews.size();
				handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
			}
		}

	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;
			tv_title.setText(titles[position]);
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * 填充ViewPager页面的适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class ImageNewsAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object instantiateItem(View arg0, final int arg1) {
			View view = imageViews.get(arg1);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					ImageNewsBean bean = imgnewsBeans.get(arg1);
					Intent intent = new Intent(getActivity(),
							NewsDetailActivity.class);
					intent.putExtra("topicid", bean.getId());
					intent.putExtra("newstitle", bean.getTitle());
					intent.putExtra("newstime", bean.getSendTime());
					intent.putExtra("newscontent", bean.getContent());
					startActivity(intent);
				}
			});
			((ViewPager) arg0).addView(view);
			return view;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}

	private ImageWorker mImageWorker;

	public ImageWorker getImageWorker() {
		if (mImageWorker == null) {
			ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams();
			ImageWorker.ScreenWeith = getResources().getDisplayMetrics().widthPixels;
			cacheParams.memCacheSize = (1024 * 1024 * getMemoryClass(getActivity()
					.getApplicationContext())) / 4;
			// cacheParams.clearDiskCacheOnStart = true;
			mImageWorker = ImageWorker.newInstance();
			mImageWorker.setCommonResID(R.drawable.list_default_icon,
					R.drawable.list_default_icon);
			mImageWorker.addParams(SudaConstants.APP_TAG, cacheParams);
		}
		return mImageWorker;
	}

	public int getMemoryClass(Context context) {
		return ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
	}

}
