package com.suda.msgcenter.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.suda.msgcenter.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class CustomListView extends ListView implements OnScrollListener {

	/** 加载中 */
	private final static int ENDINT_LOADING = 1;
	/** 自动完成刷新 */
	private final static int ENDINT_AUTO_LOAD_DONE = 2;

	/** 显示格式化日期模板 */
	private final static String DATE_FORMAT_STR = "yyyy年MM月dd日 HH:mm";

	/**
	 * 上次更新时间的毫秒值
	 */
	private long lastUpdateTime;

	/**
	 * 为了防止不同界面的下拉刷新在上次更新时间上互相有冲突，使用id来做区分
	 */
	private int mId = -1;

	/** 实际的padding的距离与界面上偏移距离的比例 */
	private final static int RATIO = 3;
	/**
	 * 一分钟的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_MINUTE = 60 * 1000;
	/**
	 * 一小时的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_HOUR = 60 * ONE_MINUTE;
	/**
	 * 一天的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_DAY = 24 * ONE_HOUR;
	/**
	 * 一月的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_MONTH = 30 * ONE_DAY;
	/**
	 * 一年的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_YEAR = 12 * ONE_MONTH;
	/**
	 * 上次更新时间的字符串常量，用于作为SharedPreferences的键值
	 */
	private static final String UPDATED_AT = "updated_at";

	/**
	 * 用于存储上次更新时间
	 */
	private SharedPreferences preferences;

	private final static int RELEASE_TO_REFRESH = 0;
	private final static int PULL_TO_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	private int mHeadState;
	private int mEndState;

	private LayoutInflater mInflater;

	private View mEndRootView;
	private ProgressBar mEndLoadProgressBar;
	private TextView mEndLoadTipsTextView;

	// private String PATH =
	// "http://192.168.1.100:8080/listview_divpage/news?pageNo=";

	private boolean is_divpage;

	List<String> total = new ArrayList<String>(); // 放置记录的总条数

	private LinearLayout mHeadView;
	private TextView mTipsTextView;
	private TextView mLastUpdatedTextView;
	private ImageView mArrowImageView;
	private ProgressBar mProgressBar;
	private int mHeadViewWidth;
	private int mHeadViewHeight;

	private int mStartY;
	private boolean mIsBack;

	private int mFirstItemIndex;
	private int count;
	private boolean isDiv = true;
	private boolean mCanLoadMore = true;

	public boolean isDiv() {
		return isDiv;
	}

	public void setDiv(boolean isDiv) {
		this.isDiv = isDiv;
	}

	public boolean getCanLoadMore() {
		return mCanLoadMore;
	}

	public void setCanLoadMore(boolean pCanLoadMore) {
		mCanLoadMore = pCanLoadMore;
		if (mCanLoadMore) {
			addFooterView();
		}
	}

	/** headView动画 */
	private RotateAnimation mArrowAnim;
	/** headView反转动画 */
	private RotateAnimation mArrowReverseAnim;

	/** 用于保证startY的值在一个完整的touch事件中只被记录一次 */
	private boolean mIsRecored;

	private OnRefreshListener mRefreshListener;
	private OnLoadMoreListener mLoadMoreListener;

	public CustomListView(Context context) {
		super(context);
		init(context);
	}

	public CustomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void init(Context context) {
		mInflater = LayoutInflater.from(context);

		addHeadView();

		addFooterView();

		setOnScrollListener(this);

		initPullImageAnimation(0); // 设置箭头动画

	}

	private void addFooterView() {
		mEndRootView = mInflater.inflate(R.layout.listfooter_more, null);
		mEndRootView.setVisibility(View.VISIBLE);
		mEndLoadProgressBar = (ProgressBar) mEndRootView
				.findViewById(R.id.pull_to_refresh_progress);
		mEndLoadTipsTextView = (TextView) mEndRootView
				.findViewById(R.id.load_more);
		addFooterView(mEndRootView);
		mEndState = ENDINT_AUTO_LOAD_DONE;
	}

	private void changeEndViewByState() {

		switch (mEndState) {
		case ENDINT_LOADING:// 加载中

			mEndLoadTipsTextView.setText("加载中....");
			mEndLoadTipsTextView.setVisibility(View.VISIBLE);
			mEndLoadProgressBar.setVisibility(View.VISIBLE);
			mEndRootView.setVisibility(View.VISIBLE);
			break;

		case ENDINT_AUTO_LOAD_DONE:// 自动刷新完成

			// 更 多
			mEndLoadTipsTextView.setText("加载更多");
			mEndLoadTipsTextView.setVisibility(View.VISIBLE);
			mEndLoadProgressBar.setVisibility(View.GONE);
			break;

		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		mFirstItemIndex = firstVisibleItem;
		is_divpage = (firstVisibleItem + visibleItemCount == totalItemCount);

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mCanLoadMore) {
			if (is_divpage && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				if (mEndState != ENDINT_LOADING) {
					if (mHeadState != REFRESHING) {
						mEndState = ENDINT_LOADING;
						changeEndViewByState();
						onLoadMore();
					}

				}
			}
		} else if (mEndRootView != null
				&& mEndRootView.getVisibility() == VISIBLE) {
			mEndRootView.setVisibility(View.GONE);
			this.removeFooterView(mEndRootView);
		}

	}

	// 相当于构造函数
	public void setOnLoadListener(OnLoadMoreListener pLoadMoreListener) {
		if (pLoadMoreListener != null) {
			mLoadMoreListener = pLoadMoreListener;
			// addFooterView();、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、
		}
	}

	/**
	 * 正在加载更多，FootView显示 ： 加载中...
	 */
	private void onLoadMore() {
		if (mLoadMoreListener != null) {
			// 加载中...
			mEndLoadTipsTextView.setText(R.string.p2refresh_doing_end_refresh);
			mEndLoadTipsTextView.setVisibility(View.VISIBLE);
			mEndLoadProgressBar.setVisibility(View.VISIBLE);
			mLoadMoreListener.onLoadMore();
		}
	}

	/**
	 * 加载更多完成
	 */
	public void onLoadMoreComplete() {
		mEndState = ENDINT_AUTO_LOAD_DONE;
		changeEndViewByState();
	}

	/**
	 * 加载更多监听接口
	 */
	public interface OnLoadMoreListener {
		public void onLoadMore();
	}

	// //////////////////////////////////////////////////////////////////
	// 下拉刷新实现
	// /////////////////////////////////////////////////////////////////
	/**
	 * 添加下拉刷新的HeadView
	 */
	private void addHeadView() {

		preferences = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		mHeadView = (LinearLayout) mInflater.inflate(R.layout.head, null);

		mArrowImageView = (ImageView) mHeadView
				.findViewById(R.id.head_arrowImageView);
		mArrowImageView.setMinimumWidth(70);
		mArrowImageView.setMinimumHeight(50);
		mProgressBar = (ProgressBar) mHeadView
				.findViewById(R.id.head_progressBar);
		mTipsTextView = (TextView) mHeadView
				.findViewById(R.id.head_tipsTextView);
		mLastUpdatedTextView = (TextView) mHeadView
				.findViewById(R.id.head_lastUpdatedTextView);

		measureView(mHeadView); // 测量HeadView宽高(注意：此方法仅适用于LinearLayout，请读者自己测试验证。)
		mHeadViewHeight = mHeadView.getMeasuredHeight();
		mHeadViewWidth = mHeadView.getMeasuredWidth();

		mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
		mHeadView.invalidate(); // invalidate()是用来刷新View的，必须是在UI线程中进行工作。比如在修改某个view的显示时，调用invalidate()才能看到重新绘制的界面。invalidate()的调用是把之前的旧的view从主UI线程队列中pop掉。

		Log.v("size", "width:" + mHeadViewWidth + " height:" + mHeadViewHeight);

		refreshUpdatedAtValue();

		addHeaderView(mHeadView, null, false);

		mHeadState = DONE;
	}

	/**
	 * 测量HeadView宽高(注意：此方法仅适用于LinearLayout，请读者自己测试验证。)
	 * 
	 * @param pChild
	 */
	private void measureView(View pChild) {
		ViewGroup.LayoutParams p = pChild.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;

		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		pChild.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * 实例化下拉刷新的箭头的动画效果
	 * 
	 * @param pAnimDuration
	 *            动画运行时长
	 */
	private void initPullImageAnimation(final int pAnimDuration) {

		int _Duration;

		if (pAnimDuration > 0) {
			_Duration = pAnimDuration;
		} else {
			_Duration = 250;
		}

		Interpolator _Interpolator = new LinearInterpolator();

		mArrowAnim = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mArrowAnim.setInterpolator(_Interpolator);
		mArrowAnim.setDuration(_Duration);
		mArrowAnim.setFillAfter(true);

		mArrowReverseAnim = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mArrowReverseAnim.setInterpolator(_Interpolator);
		mArrowReverseAnim.setDuration(_Duration);
		mArrowReverseAnim.setFillAfter(true);
	}

	public boolean onTouchEvent(MotionEvent event) {

		// if (mEndState == ENDINT_LOADING)
		// 如果存在加载更多功能，并且当前正在加载更多，默认不允许下拉刷新，必须加载完毕后才能使用。
		// return super.onTouchEvent(event);

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			if (mFirstItemIndex == 0 && !mIsRecored) {
				mIsRecored = true;
				mStartY = (int) event.getY();
			}
			break;

		case MotionEvent.ACTION_UP:

			if (mHeadState != REFRESHING && mHeadState != LOADING) {
				if (mHeadState == DONE) {

				}
				if (mHeadState == PULL_TO_REFRESH) {
					mHeadState = DONE;
					changeHeaderViewByState();
				}
				if (mHeadState == RELEASE_TO_REFRESH) {
					mHeadState = REFRESHING;
					changeHeaderViewByState();
					onRefresh();
				}
			}

			mIsRecored = false;
			mIsBack = false;

			break;

		case MotionEvent.ACTION_MOVE:
			int tempY = (int) event.getY();

			if (!mIsRecored && mFirstItemIndex == 0) {
				mIsRecored = true;
				mStartY = tempY;
			}

			if (mHeadState != REFRESHING && mIsRecored && mHeadState != LOADING) {

				// 保证在设置padding的过程中，当前的位置一直是在head，
				// 否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
				// 可以松手去刷新了
				if (mHeadState == RELEASE_TO_REFRESH) {

					setSelection(0);

					// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
					if (((tempY - mStartY) / RATIO < mHeadViewHeight)
							&& (tempY - mStartY) > 0) {
						mHeadState = PULL_TO_REFRESH;
						changeHeaderViewByState();
					}
					// 一下子推到顶了
					else if (tempY - mStartY <= 0) {
						mHeadState = DONE;
						changeHeaderViewByState();
					}
					// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
				}
				// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
				if (mHeadState == PULL_TO_REFRESH) {

					// setSelection(0);

					// 下拉到可以进入RELEASE_TO_REFRESH的状态
					if ((tempY - mStartY) / RATIO >= mHeadViewHeight) {
						mHeadState = RELEASE_TO_REFRESH;
						mIsBack = true;
						changeHeaderViewByState();
					} else if (tempY - mStartY <= 0) {
						mHeadState = DONE;
						changeHeaderViewByState();
					}
				}

				if (mHeadState == DONE) {
					if (tempY - mStartY > 0) {
						mHeadState = PULL_TO_REFRESH;
						changeHeaderViewByState();
					}
				}

				if (mHeadState == PULL_TO_REFRESH) {
					mHeadView.setPadding(0, -1 * mHeadViewHeight
							+ (tempY - mStartY) / RATIO, 0, 0);

				}

				if (mHeadState == RELEASE_TO_REFRESH) {
					mHeadView.setPadding(0, (tempY - mStartY) / RATIO
							- mHeadViewHeight, 0, 0);
				}
			}
			break;
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 当HeadView状态改变时候，调用该方法，以更新界面
	 */
	private void changeHeaderViewByState() {
		switch (mHeadState) {
		case RELEASE_TO_REFRESH:
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
			mTipsTextView.setVisibility(View.VISIBLE);
			mLastUpdatedTextView.setVisibility(View.VISIBLE);

			mArrowImageView.clearAnimation();
			mArrowImageView.startAnimation(mArrowAnim);
			// 松开刷新
			mTipsTextView.setText(R.string.release_to_refresh);

			break;
		case PULL_TO_REFRESH:
			mProgressBar.setVisibility(View.GONE);
			mTipsTextView.setVisibility(View.VISIBLE);
			mLastUpdatedTextView.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (mIsBack) {
				mIsBack = false;
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(mArrowReverseAnim);
				// 下拉刷新
				mTipsTextView.setText(R.string.pull_to_refresh);
			} else {
				// 下拉刷新
				mTipsTextView.setText(R.string.pull_to_refresh);
			}
			break;

		case REFRESHING:
			mHeadView.setPadding(0, 0, 0, 0);

			// 实际上这个的setPadding可以用动画来代替。我没有试，但是我见过。其实有的人也用Scroller可以实现这个效果，

			mProgressBar.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.GONE);
			// 正在刷新...
			mTipsTextView.setText(R.string.refreshing);
			mLastUpdatedTextView.setVisibility(View.VISIBLE);

			break;
		case DONE:
			mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);

			// 此处可以改进，同上所述。

			mProgressBar.setVisibility(View.GONE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setImageResource(R.drawable.arrow);
			// 下拉刷新
			mTipsTextView.setText(R.string.pull_to_refresh);
			mLastUpdatedTextView.setVisibility(View.VISIBLE);

			break;
		}

		refreshUpdatedAtValue();
	}

	/**
	 * 正在下拉刷新
	 */
	private void onRefresh() {
		if (mRefreshListener != null) {
			mRefreshListener.onRefresh();
		}
	}

	/**
	 * 主要更新一下刷新时间啦！
	 * 
	 * @param adapter
	 */
	/*
	 * public void setAdapter(BaseAdapter adapter) { // 最近更新: Time
	 * mLastUpdatedTextView.setText(getResources().getString(
	 * R.string.p2refresh_refresh_lasttime) + new
	 * SimpleDateFormat(DATE_FORMAT_STR, Locale.CHINA) .format(new Date()));
	 * super.setAdapter(adapter); }
	 */

	/**
	 * 刷新下拉头中上次更新时间的文字描述。
	 */
	private void refreshUpdatedAtValue() {
		lastUpdateTime = preferences.getLong(UPDATED_AT + mId, -1);
		long currentTime = System.currentTimeMillis();
		long timePassed = currentTime - lastUpdateTime;
		long timeIntoFormat;
		String updateAtValue;
		if (lastUpdateTime == -1) {
			updateAtValue = getResources().getString(R.string.not_updated_yet);
		} else if (timePassed < 0) {
			updateAtValue = getResources().getString(R.string.time_error);
		} else if (timePassed < ONE_MINUTE) {
			updateAtValue = getResources().getString(R.string.updated_just_now);
		} else if (timePassed < ONE_HOUR) {
			timeIntoFormat = timePassed / ONE_MINUTE;
			String value = timeIntoFormat + "分钟";
			updateAtValue = String.format(
					getResources().getString(R.string.updated_at), value);
		} else if (timePassed < ONE_DAY) {
			timeIntoFormat = timePassed / ONE_HOUR;
			String value = timeIntoFormat + "小时";
			updateAtValue = String.format(
					getResources().getString(R.string.updated_at), value);
		} else if (timePassed < ONE_MONTH) {
			timeIntoFormat = timePassed / ONE_DAY;
			String value = timeIntoFormat + "天";
			updateAtValue = String.format(
					getResources().getString(R.string.updated_at), value);
		} else if (timePassed < ONE_YEAR) {
			timeIntoFormat = timePassed / ONE_MONTH;
			String value = timeIntoFormat + "个月";
			updateAtValue = String.format(
					getResources().getString(R.string.updated_at), value);
		} else {
			timeIntoFormat = timePassed / ONE_YEAR;
			String value = timeIntoFormat + "年";
			updateAtValue = String.format(
					getResources().getString(R.string.updated_at), value);
		}
		mLastUpdatedTextView.setText(updateAtValue);
	}

	/**
	 * 下拉刷新完成
	 */
	public void onRefreshComplete() {

		mHeadState = DONE;
		// 最近更新: Time
		/*
		 * mLastUpdatedTextView.setText(getResources().getString(
		 * R.string.p2refresh_refresh_lasttime) + new
		 * SimpleDateFormat(DATE_FORMAT_STR, Locale.CHINA) .format(new Date()));
		 */
		preferences.edit()
				.putLong(UPDATED_AT + mId, System.currentTimeMillis()).commit();
		changeHeaderViewByState();
	}

	public void setOnRefreshListener(OnRefreshListener pRefreshListener, int id) {
		if (pRefreshListener != null) {
			mRefreshListener = pRefreshListener;
		}
		mId = id;
	}

	/**
	 * 下拉刷新监听接口
	 */
	public interface OnRefreshListener {
		public void onRefresh();
	}
}